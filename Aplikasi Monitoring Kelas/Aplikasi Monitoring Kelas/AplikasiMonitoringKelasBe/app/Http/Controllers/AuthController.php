<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\RateLimiter;
use Illuminate\Support\Facades\Log;
use Illuminate\Validation\ValidationException;
use Carbon\Carbon;

class AuthController extends Controller
{
    /**
     * Test database connection and show users for debugging
     */
    public function testConnection()
    {
        try {
            // Test database connection
            $users = User::select('id', 'name', 'email', 'role', 'created_at')->get();

            return response()->json([
                'success' => true,
                'message' => 'Database connection successful',
                'data' => [
                    'total_users' => $users->count(),
                    'users' => $users,
                    'database_info' => [
                        'connection' => config('database.default'),
                        'database' => config('database.connections.mysql.database'),
                        'host' => config('database.connections.mysql.host')
                    ]
                ]
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Database connection failed',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Register a new user
     */
    public function register(Request $request)
    {
        try {
            $request->validate([
                'name' => 'required|string|max:255',
                'email' => 'required|string|email|max:255|unique:users',
                'password' => 'required|string|min:8|confirmed',
                'role' => 'required|in:admin,guru,siswa,kurikulum'
            ]);

            $user = User::create([
                'name' => $request->name,
                'email' => $request->email,
                'password' => Hash::make($request->password),
                'role' => $request->role,
            ]);

            $token = $user->createToken('auth_token')->plainTextToken;
            
            // Load the class relationship with the user to get class name
            $user->load('kelas');

            return response()->json([
                'success' => true,
                'message' => 'User berhasil didaftarkan',
                'data' => [
                    'user' => $this->formatUserForResponse($user),
                    'token' => $token,
                    'token_type' => 'Bearer'
                ]
            ], Response::HTTP_CREATED);

        } catch (ValidationException $e) {
            return response()->json([
                'success' => false,
                'message' => 'Data tidak valid',
                'errors' => $e->errors()
            ], Response::HTTP_UNPROCESSABLE_ENTITY);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Login user - Strict authentication with database validation
     */
    public function login(Request $request)
    {
        try {
            // Validasi input yang lebih ketat
            $validated = $request->validate([
                'email' => 'required|email|max:255',
                'password' => 'required|string|min:6|max:255'
            ], [
                'email.required' => 'Email harus diisi',
                'email.email' => 'Format email tidak valid',
                'password.required' => 'Password harus diisi',
                'password.min' => 'Password minimal 6 karakter'
            ]);

            // Log attempt untuk security
            \Log::info('Login attempt', ['email' => $validated['email'], 'ip' => $request->ip()]);

            // Cari user berdasarkan email dengan validasi yang ketat
            $user = User::where('email', $validated['email'])->first();

            // Validasi user exist dan password match
            if (!$user) {
                \Log::warning('Login failed - User not found', ['email' => $validated['email']]);
                return response()->json([
                    'success' => false,
                    'message' => 'Email tidak terdaftar dalam sistem'
                ], Response::HTTP_UNAUTHORIZED);
            }

            // Verifikasi password dengan hash yang tersimpan di database
            if (!Hash::check($validated['password'], $user->password)) {
                \Log::warning('Login failed - Wrong password', ['email' => $validated['email']]);
                return response()->json([
                    'success' => false,
                    'message' => 'Password yang Anda masukkan salah'
                ], Response::HTTP_UNAUTHORIZED);
            }

            // Cek apakah user di-ban
            if ($user->is_banned) {
                \Log::warning('Login failed - User is banned', ['email' => $validated['email']]);
                return response()->json([
                    'success' => false,
                    'message' => 'Akun Anda telah dinonaktifkan. Hubungi administrator.'
                ], Response::HTTP_FORBIDDEN);
            }

            // Pastikan user memiliki role yang valid
            $validRoles = ['admin', 'kurikulum', 'kepala_sekolah', 'siswa', 'guru'];
            if (!in_array($user->role, $validRoles)) {
                \Log::error('Login failed - Invalid user role', ['email' => $validated['email'], 'role' => $user->role]);
                return response()->json([
                    'success' => false,
                    'message' => 'Akun Anda tidak memiliki role yang valid'
                ], Response::HTTP_FORBIDDEN);
            }

            // Hapus token lama jika ada (untuk keamanan)
            $user->tokens()->delete();

            // Generate token baru dengan nama yang spesifik
            $tokenName = 'auth_token_' . $user->role . '_' . now()->timestamp;
            $token = $user->createToken($tokenName, ['*'], now()->addDays(30))->plainTextToken;

            // Log successful login
            \Log::info('Login successful', [
                'user_id' => $user->id,
                'email' => $user->email,
                'role' => $user->role,
                'ip' => $request->ip()
            ]);

            // Load the class relationship with the user to get class name
            $user->load('kelas');
            
            // Response dengan data user yang sudah difilter
            return response()->json([
                'success' => true,
                'message' => 'Login berhasil',
                'data' => [
                    'user' => $this->formatUserForResponse($user), // Return formatted user data
                    'token' => $token,
                    'token_type' => 'Bearer',
                    'expires_at' => now()->addDays(30)->toISOString()
                ]
            ], Response::HTTP_OK);

        } catch (ValidationException $e) {
            \Log::warning('Login validation failed', ['errors' => $e->errors(), 'ip' => $request->ip()]);
            return response()->json([
                'success' => false,
                'message' => 'Data yang Anda masukkan tidak valid',
                'errors' => $e->errors()
            ], Response::HTTP_UNPROCESSABLE_ENTITY);

        } catch (\Exception $e) {
            \Log::error('Login system error', [
                'message' => $e->getMessage(),
                'file' => $e->getFile(),
                'line' => $e->getLine(),
                'ip' => $request->ip()
            ]);

            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan sistem. Silakan coba lagi.',
                'error_code' => 'SYSTEM_ERROR'
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logout user
     */
    public function logout(Request $request)
    {
        try {
            $request->user()->currentAccessToken()->delete();

            return response()->json([
                'success' => true,
                'message' => 'Logout berhasil'
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get authenticated user
     */
    public function user(Request $request)
    {
        try {
            $user = $request->user();
            
            // Load the class relationship with the user to get class name
            $user->load('kelas');
            
            return response()->json([
                'success' => true,
                'message' => 'Data user berhasil diambil',
                'data' => $this->formatUserForResponse($user)
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update user profile
     */
    public function updateProfile(Request $request)
    {
        try {
            $user = $request->user();

            $request->validate([
                'name' => 'sometimes|string|max:255',
                'email' => 'sometimes|string|email|max:255|unique:users,email,' . $user->id,
                'password' => 'sometimes|string|min:8|confirmed'
            ]);

            if ($request->has('name')) {
                $user->name = $request->name;
            }

            if ($request->has('email')) {
                $user->email = $request->email;
            }

            if ($request->has('password')) {
                $user->password = Hash::make($request->password);
            }

            $user->save();
            
            // Load the class relationship with the user to get class name
            $user->load('kelas');

            return response()->json([
                'success' => true,
                'message' => 'Profile berhasil diupdate',
                'data' => $this->formatUserForResponse($user)
            ], Response::HTTP_OK);

        } catch (ValidationException $e) {
            return response()->json([
                'success' => false,
                'message' => 'Data tidak valid',
                'errors' => $e->errors()
            ], Response::HTTP_UNPROCESSABLE_ENTITY);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all users (Admin only)
     */
    public function getAllUsers()
    {
        try {
            $users = User::with('kelas')->get();

            return response()->json([
                'success' => true,
                'message' => 'Data users berhasil diambil',
                'data' => $users
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get list of guru (Kurikulum & Guru role)
     */
    public function getGuruList()
    {
        try {
            $guru = User::select('id', 'name', 'email', 'role', 'mata_pelajaran', 'created_at')
                ->where('role', 'guru')
                ->where('is_banned', false)
                ->orderBy('name', 'asc')
                ->get();

            return response()->json([
                'success' => true,
                'message' => 'Data guru berhasil diambil',
                'data' => $guru
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create new user (Admin only)
     */
    public function createUser(Request $request)
    {
        try {
            $request->validate([
                'name' => 'required|string|max:255',
                'email' => 'required|email|unique:users,email',
                'password' => 'required|string|min:6',
                'role' => 'required|in:admin,kurikulum,kepala_sekolah,siswa',
                'mata_pelajaran' => 'nullable|string|max:255'
            ]);

            $user = User::create([
                'name' => $request->name,
                'email' => $request->email,
                'password' => Hash::make($request->password),
                'role' => $request->role,
                'mata_pelajaran' => $request->mata_pelajaran,
                'is_banned' => false
            ]);

            // Load the class relationship with the user to get class name
            $user->load('kelas');

            return response()->json([
                'success' => true,
                'message' => 'User berhasil dibuat',
                'data' => $this->formatUserForResponse($user)
            ], Response::HTTP_CREATED);

        } catch (ValidationException $e) {
            return response()->json([
                'success' => false,
                'message' => 'Data tidak valid',
                'errors' => $e->errors()
            ], Response::HTTP_UNPROCESSABLE_ENTITY);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update user role (Admin only)
     */
    public function updateUserRole(Request $request, $id)
    {
        try {
            $request->validate([
                'role' => 'required|in:admin,kurikulum,kepala_sekolah,siswa'
            ]);

            $user = User::findOrFail($id);
            $user->role = $request->role;
            $user->save();
            
            // Load the class relationship with the user to get class name
            $user->load('kelas');

            return response()->json([
                'success' => true,
                'message' => 'Role user berhasil diupdate',
                'data' => $this->formatUserForResponse($user)
            ], Response::HTTP_OK);

        } catch (ValidationException $e) {
            return response()->json([
                'success' => false,
                'message' => 'Data tidak valid',
                'errors' => $e->errors()
            ], Response::HTTP_UNPROCESSABLE_ENTITY);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Ban user (Admin only)
     */
    public function banUser($id)
    {
        try {
            $user = User::findOrFail($id);

            if ($user->role === 'admin') {
                return response()->json([
                    'success' => false,
                    'message' => 'Tidak dapat memban admin'
                ], Response::HTTP_FORBIDDEN);
            }

            $user->is_banned = true;
            $user->save();

            // Revoke all tokens
            $user->tokens()->delete();
            
            // Load the class relationship with the user to get class name
            $user->load('kelas');

            return response()->json([
                'success' => true,
                'message' => 'User berhasil di-ban',
                'data' => $this->formatUserForResponse($user)
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Unban user (Admin only)
     */
    public function unbanUser($id)
    {
        try {
            $user = User::findOrFail($id);
            $user->is_banned = false;
            $user->save();
            
            // Load the class relationship with the user to get class name
            $user->load('kelas');

            return response()->json([
                'success' => true,
                'message' => 'User berhasil di-unban',
                'data' => $this->formatUserForResponse($user)
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete user (Admin only)
     */
    public function deleteUser($id)
    {
        try {
            $user = User::findOrFail($id);

            if ($user->role === 'admin') {
                return response()->json([
                    'success' => false,
                    'message' => 'Tidak dapat menghapus admin'
                ], Response::HTTP_FORBIDDEN);
            }

            $user->delete();

            return response()->json([
                'success' => true,
                'message' => 'User berhasil dihapus'
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Format user data for API response ensuring kelas is a string
     */
    private function formatUserForResponse($user)
    {
        $userData = $user->toArray();
        
        // If user has a kelas relationship loaded, extract the class name as a string
        if (isset($userData['kelas']) && is_array($userData['kelas']) && isset($userData['kelas']['nama_kelas'])) {
            $userData['kelas'] = $userData['kelas']['nama_kelas'];
        } elseif (!isset($userData['class_id']) || !$userData['class_id']) {
            // Ensure kelas is null for users without class
            $userData['kelas'] = null;
        }
        
        return $userData;
    }
}

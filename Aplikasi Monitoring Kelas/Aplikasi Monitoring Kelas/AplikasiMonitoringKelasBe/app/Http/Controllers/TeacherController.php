<?php

namespace App\Http\Controllers;

use App\Models\Teacher;
use App\Models\User;
use Illuminate\Http\Request;

class TeacherController extends Controller
{
    /**
     * Display a listing of the teachers.
     */
    public function index(Request $request)
    {
        // Check if teachers table is empty
        $teachersCount = Teacher::count();
        
        if ($teachersCount === 0) {
            // If teachers table is empty, query users table with role 'guru'
            $query = User::select('id', 'name', 'email', 'mata_pelajaran', 'created_at', 'updated_at')
                         ->where('role', 'guru')
                         ->where('is_banned', false);
        } else {
            // If teachers table has data, use it and transform to match User model structure for frontend compatibility
            $query = Teacher::query();
        }

        // Optional filtering - implement if needed
        if ($request->has('search')) {
            $search = $request->search;
            $query->where(function($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                  ->orWhere('email', 'like', "%{$search}%")
                  ->orWhere('mata_pelajaran', 'like', "%{$search}%");
            });
        }

        // Optional sorting - implement if needed
        $sortBy = $request->get('sort_by', 'name'); // Default sort by name
        $sortOrder = $request->get('sort_order', 'asc'); // Default ascending
        
        if (in_array($sortBy, ['name', 'email', 'mata_pelajaran', 'created_at'])) {
            $query->orderBy($sortBy, $sortOrder);
        } else {
            $query->orderBy('name', 'asc'); // Default sorting
        }

        // Pagination with default 15 items per page
        $perPage = $request->get('per_page', 15);
        $teachers = $query->paginate($perPage);

        // If teachers table has data, transform the results to match User model structure for frontend compatibility
        if ($teachersCount > 0) {
            $teachers->getCollection()->transform(function ($teacher) {
                return [
                    'id' => $teacher->id,
                    'name' => $teacher->name,
                    'email' => $teacher->email,
                    'mata_pelajaran' => $teacher->mata_pelajaran,
                    'role' => 'guru', // Add role for frontend compatibility
                    'is_banned' => $teacher->is_banned,
                    'created_at' => $teacher->created_at,
                    'updated_at' => $teacher->updated_at
                ];
            });
        }

        // Return response in format that matches frontend expectation
        return response()->json([
            'success' => true,
            'message' => 'Daftar guru berhasil diambil',
            'data' => $teachers->getCollection()->toArray()
        ]);
    }

    /**
     * Display the specified teacher.
     */
    public function show($id)
    {
        // First try to find in the Teacher model
        $teacher = Teacher::find($id);
        
        if ($teacher) {
            // Transform the teacher to match User model structure for frontend compatibility
            $teacherData = [
                'id' => $teacher->id,
                'name' => $teacher->name,
                'email' => $teacher->email,
                'mata_pelajaran' => $teacher->mata_pelajaran,
                'role' => 'guru', // Add role for frontend compatibility
                'is_banned' => $teacher->is_banned,
                'created_at' => $teacher->created_at,
                'updated_at' => $teacher->updated_at
            ];
            return response()->json($teacherData);
        }
        
        // If not found in Teacher model, try to find in User model with role 'guru'
        $user = User::where('role', 'guru')
                    ->where('is_banned', false)
                    ->where('id', $id)
                    ->select('id', 'name', 'email', 'mata_pelajaran', 'created_at', 'updated_at')
                    ->first();
                    
        if ($user) {
            // Add role field for compatibility
            $userData = $user->toArray();
            $userData['role'] = 'guru';
            return response()->json($userData);
        }
        
        return response()->json(['message' => 'Teacher not found'], 404);
    }
}
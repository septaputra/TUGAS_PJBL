<?php

namespace App\Http\Controllers;

use App\Models\GuruPengganti;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Carbon\Carbon;

class GuruPenggantiController extends Controller
{
    /**
     * Menampilkan daftar guru pengganti
     */
    public function index(Request $request)
    {
        try {
            $query = GuruPengganti::with(['guruPengganti:id,name,email,mata_pelajaran', 'guruAsli:id,name,email,mata_pelajaran', 'assignedBy:id,name']);

            // Filter berdasarkan tanggal
            if ($request->has('tanggal') && $request->tanggal) {
                $query->whereDate('tanggal', $request->tanggal);
            }

            // Filter berdasarkan kelas
            if ($request->has('kelas') && $request->kelas) {
                $query->where('kelas', $request->kelas);
            }

            $guruPengganti = $query->orderBy('tanggal', 'desc')
                                  ->orderBy('jam_mulai', 'asc')
                                  ->get();

            return response()->json([
                'success' => true,
                'message' => 'Data guru pengganti berhasil diambil',
                'data' => $guruPengganti
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
     * Menyimpan data guru pengganti baru
     */
    public function store(Request $request)
    {
        try {
            $request->validate([
                'guru_pengganti_id' => 'required|exists:users,id',
                'guru_asli_id' => 'nullable|exists:users,id',
                'kelas' => 'required|string',
                'mata_pelajaran' => 'required|string',
                'tanggal' => 'required|date',
                'jam_mulai' => 'required|date_format:H:i',
                'jam_selesai' => 'required|date_format:H:i|after:jam_mulai',
                'ruang' => 'nullable|string',
                'keterangan' => 'nullable|string'
            ]);

            $guruPengganti = GuruPengganti::create([
                'guru_pengganti_id' => $request->guru_pengganti_id,
                'guru_asli_id' => $request->guru_asli_id,
                'kelas' => $request->kelas,
                'mata_pelajaran' => $request->mata_pelajaran,
                'tanggal' => $request->tanggal,
                'jam_mulai' => $request->jam_mulai,
                'jam_selesai' => $request->jam_selesai,
                'ruang' => $request->ruang,
                'keterangan' => $request->keterangan,
                'assigned_by' => $request->user()->id
            ]);

            $guruPengganti->load(['guruPengganti:id,name,email,mata_pelajaran', 'guruAsli:id,name,email,mata_pelajaran']);

            return response()->json([
                'success' => true,
                'message' => 'Guru pengganti berhasil ditambahkan',
                'data' => $guruPengganti
            ], Response::HTTP_CREATED);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mengupdate data guru pengganti
     */
    public function update(Request $request, $id)
    {
        try {
            $guruPengganti = GuruPengganti::findOrFail($id);

            $request->validate([
                'guru_pengganti_id' => 'sometimes|required|exists:users,id',
                'guru_asli_id' => 'nullable|exists:users,id',
                'kelas' => 'sometimes|required|string',
                'mata_pelajaran' => 'sometimes|required|string',
                'tanggal' => 'sometimes|required|date',
                'jam_mulai' => 'sometimes|required|date_format:H:i',
                'jam_selesai' => 'sometimes|required|date_format:H:i',
                'ruang' => 'nullable|string',
                'keterangan' => 'nullable|string'
            ]);

            $guruPengganti->update($request->all());
            $guruPengganti->load(['guruPengganti:id,name,email,mata_pelajaran', 'guruAsli:id,name,email,mata_pelajaran']);

            return response()->json([
                'success' => true,
                'message' => 'Guru pengganti berhasil diupdate',
                'data' => $guruPengganti
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
     * Menghapus data guru pengganti
     */
    public function destroy($id)
    {
        try {
            $guruPengganti = GuruPengganti::findOrFail($id);
            $guruPengganti->delete();

            return response()->json([
                'success' => true,
                'message' => 'Guru pengganti berhasil dihapus'
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }
}

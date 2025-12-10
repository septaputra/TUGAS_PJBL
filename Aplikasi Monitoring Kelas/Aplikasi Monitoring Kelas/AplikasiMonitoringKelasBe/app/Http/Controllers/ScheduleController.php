<?php

namespace App\Http\Controllers;

use App\Models\Schedule;
use Illuminate\Http\Request;
use Illuminate\Http\Response;

class ScheduleController extends Controller
{
    /**
     * Mengambil semua jadwal pelajaran dengan data guru
     */
    public function index(Request $request)
    {
        try {
            $query = Schedule::with('guru:id,name,email');

            // Filter berdasarkan hari jika ada
            if ($request->has('hari') && $request->hari) {
                $query->where('hari', $request->hari);
            }

            // Filter berdasarkan kelas jika ada
            if ($request->has('kelas') && $request->kelas) {
                $query->where('kelas', 'like', '%' . $request->kelas . '%');
            }

            $schedules = $query->orderBy('hari')
                              ->orderBy('jam_mulai')
                              ->get();

            return response()->json([
                'success' => true,
                'message' => 'Data jadwal berhasil diambil',
                'data' => $schedules
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
     * Menambah jadwal baru
     */
    public function store(Request $request)
    {
        try {
            $request->validate([
                'hari' => 'required|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
                'kelas' => 'required|string|max:10',
                'mata_pelajaran' => 'required|string|max:255',
                'guru_id' => 'required|exists:users,id',
                'jam_mulai' => 'required|date_format:H:i',
                'jam_selesai' => 'required|date_format:H:i|after:jam_mulai',
                'ruang' => 'nullable|string|max:50'
            ]);

            $schedule = Schedule::create($request->all());
            $schedule->load('guru:id,name,email');

            return response()->json([
                'success' => true,
                'message' => 'Jadwal berhasil ditambahkan',
                'data' => $schedule
            ], Response::HTTP_CREATED);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }
}

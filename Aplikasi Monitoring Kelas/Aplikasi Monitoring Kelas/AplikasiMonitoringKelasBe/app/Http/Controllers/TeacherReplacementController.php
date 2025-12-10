<?php

namespace App\Http\Controllers;

use App\Models\TeacherAttendance;
use App\Models\Schedule;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Carbon\Carbon;

class TeacherReplacementController extends Controller
{
    /**
     * Menampilkan daftar guru yang sudah diganti
     */
    public function index(Request $request)
    {
        try {
            $query = TeacherAttendance::with([
                'schedule',
                'guru:id,name,email,mata_pelajaran',
                'guruAsli:id,name,email,mata_pelajaran',
                'assignedBy:id,name'
            ])->where('status', 'diganti');

            // Filter berdasarkan tanggal
            if ($request->has('tanggal') && $request->tanggal) {
                $query->whereDate('tanggal', $request->tanggal);
            }

            $replacements = $query->orderBy('tanggal', 'desc')
                                  ->orderBy('jam_masuk', 'asc')
                                  ->get();

            // Format response
            $data = $replacements->map(function($replacement) {
                $schedule = $replacement->schedule;
                return [
                    'id' => $replacement->id,
                    'schedule_id' => $replacement->schedule_id,
                    'guru_pengganti' => $replacement->guru,
                    'guru_asli' => $replacement->guruAsli,
                    'kelas' => $schedule->kelas,
                    'mata_pelajaran' => $schedule->mata_pelajaran,
                    'tanggal' => $replacement->tanggal,
                    'jam_mulai' => $schedule->jam_mulai,
                    'jam_selesai' => $schedule->jam_selesai,
                    'ruang' => $schedule->ruang,
                    'keterangan' => $replacement->keterangan,
                    'assigned_by' => $replacement->assignedBy,
                    'created_at' => $replacement->created_at,
                    'updated_at' => $replacement->updated_at
                ];
            });

            return response()->json([
                'success' => true,
                'message' => 'Data penggantian guru berhasil diambil',
                'data' => $data
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
     * Menugaskan guru pengganti (update teacher_attendance)
     */
    public function assignReplacement(Request $request)
    {
        try {
            $request->validate([
                'attendance_id' => 'required|exists:teacher_attendances,id',
                'guru_pengganti_id' => 'required|exists:users,id',
                'keterangan' => 'nullable|string'
            ]);

            $attendance = TeacherAttendance::findOrFail($request->attendance_id);

            // Pastikan status saat ini adalah tidak_hadir
            if ($attendance->status !== 'tidak_hadir') {
                return response()->json([
                    'success' => false,
                    'message' => 'Hanya dapat mengganti guru yang statusnya tidak hadir'
                ], Response::HTTP_BAD_REQUEST);
            }

            // Simpan guru asli dan update dengan guru pengganti
            $attendance->guru_asli_id = $attendance->guru_id;
            $attendance->guru_id = $request->guru_pengganti_id;
            $attendance->status = 'diganti';
            $attendance->assigned_by = $request->user()->id;

            // Update atau tambahkan keterangan
            if ($request->has('keterangan') && $request->keterangan) {
                $attendance->keterangan = $request->keterangan;
            }

            $attendance->save();

            $attendance->load([
                'schedule',
                'guru:id,name,email,mata_pelajaran',
                'guruAsli:id,name,email,mata_pelajaran',
                'assignedBy:id,name'
            ]);

            // Format response
            $schedule = $attendance->schedule;
            $data = [
                'id' => $attendance->id,
                'schedule_id' => $attendance->schedule_id,
                'guru_pengganti' => $attendance->guru,
                'guru_asli' => $attendance->guruAsli,
                'kelas' => $schedule->kelas,
                'mata_pelajaran' => $schedule->mata_pelajaran,
                'tanggal' => $attendance->tanggal,
                'jam_mulai' => $schedule->jam_mulai,
                'jam_selesai' => $schedule->jam_selesai,
                'ruang' => $schedule->ruang,
                'keterangan' => $attendance->keterangan,
                'assigned_by' => $attendance->assignedBy,
                'created_at' => $attendance->created_at,
                'updated_at' => $attendance->updated_at
            ];

            return response()->json([
                'success' => true,
                'message' => 'Guru pengganti berhasil ditugaskan',
                'data' => $data
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
     * Membatalkan penggantian guru (kembalikan ke status tidak_hadir)
     */
    public function cancelReplacement(Request $request, $id)
    {
        try {
            $attendance = TeacherAttendance::findOrFail($id);

            // Pastikan status saat ini adalah diganti
            if ($attendance->status !== 'diganti') {
                return response()->json([
                    'success' => false,
                    'message' => 'Hanya dapat membatalkan guru yang statusnya diganti'
                ], Response::HTTP_BAD_REQUEST);
            }

            // Kembalikan ke guru asli
            $attendance->guru_id = $attendance->guru_asli_id;
            $attendance->guru_asli_id = null;
            $attendance->status = 'tidak_hadir';
            $attendance->assigned_by = null;
            $attendance->save();

            return response()->json([
                'success' => true,
                'message' => 'Penggantian guru berhasil dibatalkan'
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

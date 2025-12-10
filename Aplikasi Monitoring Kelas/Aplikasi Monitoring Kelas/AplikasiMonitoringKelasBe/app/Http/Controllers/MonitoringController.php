<?php

namespace App\Http\Controllers;

use App\Models\Monitoring;
use App\Models\Schedule;
use App\Models\TeacherAttendance;
use App\Models\GuruPengganti;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Carbon\Carbon;

class MonitoringController extends Controller
{
    /**
     * Menyimpan data monitoring kehadiran guru
     */
    public function store(Request $request)
    {
        try {
            $request->validate([
                'guru_id' => 'required|exists:users,id',
                'status_hadir' => 'required|in:Hadir,Terlambat,Tidak Hadir',
                'catatan' => 'nullable|string',
                'kelas' => 'required|string|max:10',
                'mata_pelajaran' => 'required|string|max:255',
                'tanggal' => 'nullable|date',
                'jam_laporan' => 'nullable|date_format:H:i'
                ,
                // Optional: siswa dapat mengusulkan guru pengganti
                'guru_pengganti_id' => 'nullable|exists:users,id'
            ]);

            $monitoring = Monitoring::create([
                'guru_id' => $request->guru_id,
                'pelapor_id' => $request->user()->id, // User yang sedang login
                'status_hadir' => $request->status_hadir,
                'catatan' => $request->catatan,
                'kelas' => $request->kelas,
                'mata_pelajaran' => $request->mata_pelajaran,
                'tanggal' => $request->tanggal ?? Carbon::today(),
                'jam_laporan' => $request->jam_laporan ?? Carbon::now()->format('H:i')
            ]);

            // Jika siswa mengusulkan guru pengganti pada saat pelaporan, simpan proposal
            $proposedReplacement = null;
            if ($request->has('guru_pengganti_id') && $request->guru_pengganti_id) {
                $proposedReplacement = GuruPengganti::create([
                    'guru_pengganti_id' => $request->guru_pengganti_id,
                    'guru_asli_id' => $request->guru_id,
                    'kelas' => $request->kelas,
                    'mata_pelajaran' => $request->mata_pelajaran,
                    'tanggal' => $request->tanggal ?? Carbon::today(),
                    // store jam_laporan into jam_mulai to indicate the time
                    'jam_mulai' => $request->jam_laporan ?? Carbon::now()->format('H:i'),
                    'jam_selesai' => null,
                    'ruang' => null,
                    'keterangan' => $request->catatan,
                    'assigned_by' => null
                ]);

                // Jika siswa mengusulkan pengganti, otomatis coba catat attendance sebagai 'diganti'
                try {
                    $tanggalForSearch = $request->tanggal ? Carbon::parse($request->tanggal)->format('Y-m-d') : Carbon::today()->format('Y-m-d');

                    // Map day names like kelasKosong
                    $hariMapping = [
                        'Monday' => 'Senin', 'Tuesday' => 'Selasa', 'Wednesday' => 'Rabu',
                        'Thursday' => 'Kamis', 'Friday' => 'Jumat', 'Saturday' => 'Sabtu', 'Sunday' => 'Minggu'
                    ];
                    $hari = $hariMapping[Carbon::parse($tanggalForSearch)->format('l')] ?? Carbon::parse($tanggalForSearch)->format('l');

                    // Cari jadwal yang cocok
                    $schedule = Schedule::where('kelas', $request->kelas)
                                ->where('mata_pelajaran', $request->mata_pelajaran)
                                ->where('hari', $hari)
                                ->first();

                    // Cari attendance existing untuk guru asli di tanggal tersebut
                    $attendanceQuery = TeacherAttendance::where('guru_id', $request->guru_id)
                                        ->whereDate('tanggal', $tanggalForSearch);
                    if ($schedule) {
                        $attendanceQuery->where('schedule_id', $schedule->id);
                    }
                    $attendance = $attendanceQuery->first();

                    // Cek apakah sudah ada attendance untuk guru pengganti pada jadwal+tanggal yang sama
                    $existingReplacementAttendance = null;
                    if ($schedule) {
                        $existingReplacementAttendance = TeacherAttendance::where('schedule_id', $schedule->id)
                            ->whereDate('tanggal', $tanggalForSearch)
                            ->where('guru_id', $request->guru_pengganti_id)
                            ->first();
                    } else {
                        // jika tidak ada schedule (pencarian jadwal gagal), coba cari berdasarkan guru_id + tanggal
                        $existingReplacementAttendance = TeacherAttendance::where('guru_id', $request->guru_pengganti_id)
                            ->whereDate('tanggal', $tanggalForSearch)
                            ->first();
                    }

                    if ($existingReplacementAttendance) {
                        // Jika sudah ada record untuk guru pengganti, jadikan record itu yang final
                        if (empty($existingReplacementAttendance->guru_asli_id)) {
                            $existingReplacementAttendance->guru_asli_id = $request->guru_id;
                        }
                        $existingReplacementAttendance->status = 'diganti';
                        $existingReplacementAttendance->assigned_by = $request->user()->id;
                        if ($request->has('catatan')) {
                            $existingReplacementAttendance->keterangan = $request->catatan;
                        }
                        $existingReplacementAttendance->save();

                        // Jika ada attendance untuk guru asli, set statusnya menjadi 'tidak_hadir' (tetap simpan)
                        if ($attendance) {
                            $attendance->status = 'tidak_hadir';
                            $attendance->save();
                        }
                    } else {
                        if ($attendance) {
                            // Update attendance menjadi diganti (ganti guru_id ke pengganti)
                            $attendance->guru_asli_id = $attendance->guru_id;
                            $attendance->guru_id = $request->guru_pengganti_id;
                            $attendance->status = 'diganti';
                            $attendance->assigned_by = $request->user()->id;
                            if ($request->has('catatan')) {
                                $attendance->keterangan = $request->catatan;
                            }
                            $attendance->save();
                        } else {
                            // Buat attendance baru sebagai diganti
                            $attendanceData = [
                                'guru_id' => $request->guru_pengganti_id,
                                'guru_asli_id' => $request->guru_id,
                                'tanggal' => $tanggalForSearch,
                                'jam_masuk' => $request->jam_laporan ?? Carbon::now()->format('H:i'),
                                'status' => 'diganti',
                                'keterangan' => $request->catatan,
                                'created_by' => $request->user()->id,
                                'assigned_by' => $request->user()->id
                            ];
                            if ($schedule) {
                                $attendanceData['schedule_id'] = $schedule->id;
                            }
                            try {
                                TeacherAttendance::create($attendanceData);
                            } catch (\Exception $e) {
                                // Jika pembuatan gagal, tetap lanjut — proposal sudah tersimpan
                            }
                        }
                    }
                } catch (\Exception $e) {
                    // Jika ada error saat proses auto-assign, ignore agar tidak mengganggu pembuatan proposal
                }
            }

            $monitoring->load(['guru:id,name,email', 'pelapor:id,name,email']);

            // Sertakan section kehadiran_guru untuk memudahkan UI menampilkan status dan pengganti
            $kehadiranGuru = $this->buildKehadiranGuru($monitoring->guru_id, $monitoring->kelas, $monitoring->mata_pelajaran, $monitoring->tanggal);

            return response()->json([
                'success' => true,
                'message' => 'Monitoring berhasil dicatat',
                'data' => $monitoring,
                'proposed_replacement' => $proposedReplacement,
                'kehadiran_guru' => $kehadiranGuru
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
     * Mengambil data monitoring
     */
    public function index(Request $request)
    {
        try {
            $query = Monitoring::with(['guru:id,name,email', 'pelapor:id,name,email']);

            // Filter berdasarkan tanggal
            if ($request->has('tanggal') && $request->tanggal) {
                $query->whereDate('tanggal', $request->tanggal);
            }

            // Filter berdasarkan kelas
            if ($request->has('kelas') && $request->kelas) {
                $query->where('kelas', $request->kelas);
            }

            // Filter berdasarkan guru_id
            if ($request->has('guru_id') && $request->guru_id) {
                $query->where('guru_id', $request->guru_id);
            }

            $monitoring = $query->orderBy('tanggal', 'desc')
                               ->orderBy('jam_laporan', 'desc')
                               ->get();

            return response()->json([
                'success' => true,
                'message' => 'Data monitoring berhasil diambil',
                'data' => $monitoring
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
     * Mengambil laporan monitoring yang dibuat siswa (untuk siswa)
     */
    public function myReports(Request $request)
    {
        try {
            $query = Monitoring::with(['guru:id,name,email,mata_pelajaran'])
                              ->where('pelapor_id', $request->user()->id);

            // Filter berdasarkan tanggal
            if ($request->has('tanggal') && $request->tanggal) {
                $query->whereDate('tanggal', $request->tanggal);
            }

            $monitoring = $query->orderBy('created_at', 'desc')->get();

            // Attach any proposed replacement and kehadiran_guru (status + pengganti) to each monitoring item
            $data = $monitoring->map(function($m) {
                $replacement = GuruPengganti::where('guru_asli_id', $m->guru_id)
                    ->whereDate('tanggal', $m->tanggal)
                    ->where('kelas', $m->kelas)
                    ->first();

                $mArray = $m->toArray();
                $mArray['proposed_replacement'] = $replacement ? $replacement->toArray() : null;
                // Tambahkan section kehadiran_guru berisi status dan info pengganti jika ada
                $mArray['kehadiran_guru'] = $this->buildKehadiranGuru($m->guru_id, $m->kelas, $m->mata_pelajaran, $m->tanggal);
                return $mArray;
            });

            return response()->json([
                'success' => true,
                'message' => 'Data laporan berhasil diambil',
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
     * Mengecek kelas kosong berdasarkan jadwal hari ini dan teacher attendance
     */
    public function kelasKosong(Request $request)
    {
        try {
            // Ambil tanggal dari request (dari device) atau gunakan tanggal hari ini dari server
            // Jika client mengirim tanggal, gunakan tanggal tersebut
            $tanggal = $request->input('tanggal');

            // Jika tidak ada tanggal dari client, gunakan tanggal server dengan timezone Asia/Jakarta
            if (!$tanggal) {
                $tanggal = Carbon::now('Asia/Jakarta')->format('Y-m-d');
            } else {
                // Validasi format tanggal
                try {
                    $tanggal = Carbon::parse($tanggal)->format('Y-m-d');
                } catch (\Exception $e) {
                    return response()->json([
                        'success' => false,
                        'message' => 'Format tanggal tidak valid. Gunakan format YYYY-MM-DD',
                        'error' => $e->getMessage()
                    ], Response::HTTP_BAD_REQUEST);
                }
            }

            // Mapping hari dari Carbon ke format database
            $hariMapping = [
                'Monday' => 'Senin',
                'Tuesday' => 'Selasa',
                'Wednesday' => 'Rabu',
                'Thursday' => 'Kamis',
                'Friday' => 'Jumat',
                'Saturday' => 'Sabtu',
                'Sunday' => 'Minggu'
            ];

            $hari = $hariMapping[Carbon::parse($tanggal)->format('l')] ?? 'Senin';

            // Ambil semua jadwal untuk hari ini
            $jadwalHariIni = Schedule::with(['guru:id,name,email,mata_pelajaran'])
                ->where('hari', $hari)
                ->get();

            // Ambil teacher attendance untuk tanggal yang diminta dengan status tidak_hadir
            $teacherAttendanceTidakHadir = TeacherAttendance::with(['schedule', 'guru:id,name,email,mata_pelajaran'])
                ->whereDate('tanggal', $tanggal)
                ->where('status', 'tidak_hadir')
                ->get()
                ->keyBy('schedule_id');

            $kelasKosong = [];

            // Cek setiap jadwal apakah guru tidak hadir
            foreach ($jadwalHariIni as $jadwal) {
                // Jika ada teacher attendance dengan status tidak_hadir untuk jadwal ini
                if (isset($teacherAttendanceTidakHadir[$jadwal->id])) {
                    $attendance = $teacherAttendanceTidakHadir[$jadwal->id];

                    $kelasKosong[] = [
                        'jadwal_id' => $jadwal->id,
                        'attendance_id' => $attendance->id,
                        'kelas' => $jadwal->kelas,
                        'mata_pelajaran' => $jadwal->mata_pelajaran,
                        'guru' => $jadwal->guru,
                        'jam_mulai' => $jadwal->jam_mulai,
                        'jam_selesai' => $jadwal->jam_selesai,
                        'ruang' => $jadwal->ruang,
                        'tanggal' => $tanggal,
                        'hari' => $hari,
                        'status' => 'Tidak Hadir',
                        'keterangan' => $attendance->keterangan
                    ];
                }
            }

            return response()->json([
                'success' => true,
                'message' => 'Data kelas kosong berhasil diambil',
                'data' => $kelasKosong,
                'summary' => [
                    'total_jadwal' => $jadwalHariIni->count(),
                    'total_kelas_kosong' => count($kelasKosong),
                    'tanggal' => $tanggal,
                    'hari' => $hari
                ]
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
     * Mengambil semua laporan kehadiran guru (khusus untuk kepala sekolah dan admin)
     */
    public function getAllEmptyClassReports(Request $request)
    {
        try {
            // Get all teacher attendances (all statuses)
            $query = \App\Models\TeacherAttendance::with(['guru:id,name,email,mata_pelajaran', 'schedule:id,kelas,mata_pelajaran,ruang,hari,jam_mulai,jam_selesai']);

            // Filter berdasarkan tanggal
            if ($request->has('tanggal') && $request->tanggal) {
                $query->whereDate('tanggal', $request->tanggal);
            }

            // Filter berdasarkan kelas melalui schedule
            if ($request->has('kelas') && $request->kelas) {
                $query->whereHas('schedule', function($q) use ($request) {
                    $q->where('kelas', $request->kelas);
                });
            }

            // Filter berdasarkan guru_id
            if ($request->has('guru_id') && $request->guru_id) {
                $query->where('guru_id', $request->guru_id);
            }

            // Filter berdasarkan status (opsional)
            if ($request->has('status') && $request->status) {
                $query->where('status', $request->status);
            }

            $attendances = $query->orderBy('tanggal', 'desc')
                                ->orderBy('jam_masuk', 'desc')
                                ->get();

            // Format the data to match the expected monitoring response structure
            $formattedData = $attendances->map(function($attendance) {
                return [
                    'id' => $attendance->id,
                    'guru_id' => $attendance->guru_id,
                    'guru' => $attendance->guru,
                    'pelapor_id' => $attendance->created_by,
                    'pelapor' => $attendance->createdBy, // Using createdBy from the relationship
                    'status_hadir' => $attendance->status,
                    'catatan' => $attendance->keterangan,
                    'kelas' => $attendance->schedule->kelas ?? 'N/A',
                    'mata_pelajaran' => $attendance->schedule->mata_pelajaran ?? 'N/A',
                    'tanggal' => $attendance->tanggal,
                    'jam_laporan' => $attendance->jam_masuk,
                    'created_at' => $attendance->created_at,
                    'updated_at' => $attendance->updated_at
                ];
            });

            return response()->json([
                'success' => true,
                'message' => 'Data laporan kehadiran guru berhasil diambil',
                'data' => $formattedData
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
     * Mengambil laporan kelas kosong (hanya status tidak hadir) dari siswa (khusus untuk kepala sekolah dan admin)
     */
    public function getEmptyClassOnly(Request $request)
    {
        try {
            // Get teacher attendances with status 'tidak_hadir' only (empty classes)
            $query = \App\Models\TeacherAttendance::with(['guru:id,name,email,mata_pelajaran', 'schedule:id,kelas,mata_pelajaran,ruang,hari,jam_mulai,jam_selesai'])
                              ->where('status', 'tidak_hadir');

            // Filter berdasarkan tanggal
            if ($request->has('tanggal') && $request->tanggal) {
                $query->whereDate('tanggal', $request->tanggal);
            }

            // Filter berdasarkan kelas melalui schedule
            if ($request->has('kelas') && $request->kelas) {
                $query->whereHas('schedule', function($q) use ($request) {
                    $q->where('kelas', $request->kelas);
                });
            }

            // Filter berdasarkan guru_id
            if ($request->has('guru_id') && $request->guru_id) {
                $query->where('guru_id', $request->guru_id);
            }

            $attendances = $query->orderBy('tanggal', 'desc')
                                ->orderBy('jam_masuk', 'desc')
                                ->get();

            // Format the data to match the expected monitoring response structure
            $formattedData = $attendances->map(function($attendance) {
                return [
                    'id' => $attendance->id,
                    'guru_id' => $attendance->guru_id,
                    'guru' => $attendance->guru,
                    'pelapor_id' => $attendance->created_by,
                    'pelapor' => $attendance->createdBy, // Using createdBy from the relationship
                    'status_hadir' => $attendance->status,
                    'catatan' => $attendance->keterangan,
                    'kelas' => $attendance->schedule->kelas ?? 'N/A',
                    'mata_pelajaran' => $attendance->schedule->mata_pelajaran ?? 'N/A',
                    'tanggal' => $attendance->tanggal,
                    'jam_laporan' => $attendance->jam_masuk,
                    'created_at' => $attendance->created_at,
                    'updated_at' => $attendance->updated_at
                ];
            });

            return response()->json([
                'success' => true,
                'message' => 'Data laporan kelas kosong berhasil diambil',
                'data' => $formattedData
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
     * Mengambil data kelas kosong berdasarkan teacher attendance (endpoint khusus untuk kelas kosong dari kehadiran guru)
     */
    public function getKelasKosongFromAttendance(Request $request)
    {
        try {
            // Ambil tanggal dari request atau gunakan tanggal hari ini
            $tanggal = $request->input('tanggal');
            
            if (!$tanggal) {
                $tanggal = Carbon::now('Asia/Jakarta')->format('Y-m-d');
            } else {
                // Validasi format tanggal
                try {
                    $tanggal = Carbon::parse($tanggal)->format('Y-m-d');
                } catch (\Exception $e) {
                    return response()->json([
                        'success' => false,
                        'message' => 'Format tanggal tidak valid. Gunakan format YYYY-MM-DD',
                        'error' => $e->getMessage()
                    ], Response::HTTP_BAD_REQUEST);
                }
            }

            // Ambil semua teacher attendance dengan status 'tidak_hadir' pada tanggal tertentu
            $query = TeacherAttendance::with(['guru:id,name,email,mata_pelajaran', 'schedule:id,kelas,mata_pelajaran,ruang,hari,jam_mulai,jam_selesai'])
                      ->where('status', 'tidak_hadir')
                      ->whereDate('tanggal', $tanggal);

            // Filter berdasarkan kelas jika disediakan
            if ($request->has('kelas') && $request->kelas) {
                $query->whereHas('schedule', function($q) use ($request) {
                    $q->where('kelas', $request->kelas);
                });
            }

            // Filter berdasarkan guru_id jika disediakan
            if ($request->has('guru_id') && $request->guru_id) {
                $query->where('guru_id', $request->guru_id);
            }

            $emptyClasses = $query->orderBy('tanggal', 'desc')
                                  ->orderBy('jam_masuk', 'desc')
                                  ->get();

            // Format the data to match the kelas_kosong response structure
            $formattedData = $emptyClasses->map(function($attendance) {
                $schedule = $attendance->schedule;
                
                return [
                    'attendance_id' => $attendance->id,
                    'kelas' => $schedule->kelas ?? 'N/A',
                    'mata_pelajaran' => $schedule->mata_pelajaran ?? 'N/A',
                    'guru' => $attendance->guru,
                    'jam_mulai' => $schedule->jam_mulai,
                    'jam_selesai' => $schedule->jam_selesai,
                    'ruang' => $schedule->ruang,
                    'tanggal' => $attendance->tanggal,
                    'hari' => $schedule->hari,
                    'keterangan' => $attendance->keterangan,
                    'status' => $attendance->status
                ];
            });

            return response()->json([
                'success' => true,
                'message' => 'Data kelas kosong dari kehadiran guru berhasil diambil',
                'data' => $formattedData,
                'summary' => [
                    'total_kelas_kosong' => $formattedData->count(),
                    'tanggal' => $tanggal,
                ]
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
     * Mengambil data untuk form penggantian berdasarkan attendance id
     * Mengembalikan info sesi + daftar guru yang prioritas tersedia (tidak ada jadwal konflik)
     */
    public function replacementForm(Request $request, $attendanceId)
    {
        try {
            $attendance = TeacherAttendance::with(['schedule', 'guru'])->findOrFail($attendanceId);

            $sessionTanggal = $attendance->tanggal;
            $sessionSchedule = $attendance->schedule;
            $sessionJam = $sessionSchedule ? $sessionSchedule->jam_mulai : $attendance->jam_masuk;
            $hari = $sessionSchedule ? $sessionSchedule->hari : null;

            // Ambil daftar guru aktif (role = guru) kecuali guru asli
            $candidates = User::where('role', 'guru')
                            ->where('id', '!=', $attendance->guru_id)
                            ->where(function($q) { $q->whereNull('is_banned')->orWhere('is_banned', false); })
                            ->get();

            $available = [];
            $conflicting = [];

            foreach ($candidates as $cand) {
                $hasConflict = false;
                if ($hari && $sessionJam) {
                    $conflict = Schedule::where('guru_id', $cand->id)
                                ->where('hari', $hari)
                                ->where('jam_mulai', $sessionJam)
                                ->exists();
                    $hasConflict = $conflict;
                }

                $item = [
                    'id' => $cand->id,
                    'name' => $cand->name,
                    'mata_pelajaran' => $cand->mata_pelajaran ?? null
                ];

                if ($hasConflict) {
                    $conflicting[] = $item;
                } else {
                    $available[] = $item;
                }
            }

            return response()->json([
                'success' => true,
                'message' => 'Form penggantian data',
                'data' => [
                    'attendance' => $attendance,
                    'session' => [
                        'tanggal' => $sessionTanggal,
                        'hari' => $hari,
                        'jam_mulai' => $sessionJam,
                        'mata_pelajaran' => $sessionSchedule ? $sessionSchedule->mata_pelajaran : null,
                        'kelas' => $sessionSchedule ? $sessionSchedule->kelas : ($attendance->schedule->kelas ?? null)
                    ],
                    'candidates' => [
                        'available' => $available,
                        'conflicting' => $conflicting
                    ]
                ]
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Gagal mengambil form penggantian',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Melakukan penggantian guru untuk sebuah attendance (dipanggil saat user submit form)
     */
    public function replaceTeacher(Request $request, $attendanceId)
    {
        try {
            $request->validate([
                'guru_pengganti_id' => 'required|exists:users,id',
                'keterangan' => 'nullable|string'
            ]);

            $attendance = TeacherAttendance::with('schedule')->findOrFail($attendanceId);

            $originalGuruId = $attendance->guru_id;
            $tanggalForSearch = Carbon::parse($attendance->tanggal)->format('Y-m-d');

            // Simpan atau update proposal GuruPengganti
            $proposal = GuruPengganti::updateOrCreate(
                [
                    'guru_asli_id' => $originalGuruId,
                    'tanggal' => $tanggalForSearch,
                    'kelas' => $attendance->schedule->kelas ?? $attendance->kelas ?? null,
                ],
                [
                    'guru_pengganti_id' => $request->guru_pengganti_id,
                    'mata_pelajaran' => $attendance->schedule->mata_pelajaran ?? null,
                    'jam_mulai' => $attendance->schedule->jam_mulai ?? $attendance->jam_masuk,
                    'keterangan' => $request->keterangan,
                    'assigned_by' => $request->user()->id
                ]
            );

            // Coba update/create attendance sebagai 'diganti'
            $schedule = $attendance->schedule;

            // Cek apakah sudah ada attendance untuk pengganti pada schedule/tanggal yang sama
            $existingReplacementAttendance = null;
            if ($schedule) {
                $existingReplacementAttendance = TeacherAttendance::where('schedule_id', $schedule->id)
                    ->whereDate('tanggal', $tanggalForSearch)
                    ->where('guru_id', $request->guru_pengganti_id)
                    ->first();
            } else {
                $existingReplacementAttendance = TeacherAttendance::where('guru_id', $request->guru_pengganti_id)
                    ->whereDate('tanggal', $tanggalForSearch)
                    ->first();
            }

            if ($existingReplacementAttendance) {
                // Update the existing replacement attendance
                $existingReplacementAttendance->guru_asli_id = $existingReplacementAttendance->guru_asli_id ?? $originalGuruId;
                $existingReplacementAttendance->status = 'diganti';
                $existingReplacementAttendance->assigned_by = $request->user()->id;
                if ($request->has('keterangan')) {
                    $existingReplacementAttendance->keterangan = $request->keterangan;
                }
                $existingReplacementAttendance->save();

                // Mark original attendance as tidak_hadir
                $attendance->status = 'tidak_hadir';
                $attendance->save();

                $resultAttendance = $existingReplacementAttendance;
            } else {
                try {
                    // Try to update the attendance in-place to set pengganti
                    $attendance->guru_asli_id = $attendance->guru_id;
                    $attendance->guru_id = $request->guru_pengganti_id;
                    $attendance->status = 'diganti';
                    $attendance->assigned_by = $request->user()->id;
                    if ($request->has('keterangan')) {
                        $attendance->keterangan = $request->keterangan;
                    }
                    $attendance->save();

                    $resultAttendance = $attendance;
                } catch (\Exception $e) {
                    // Jika update mengakibatkan constraint duplicate, buat record baru dan tandai original sebagai tidak_hadir
                    try {
                        $newAttendance = TeacherAttendance::create([
                            'guru_id' => $request->guru_pengganti_id,
                            'guru_asli_id' => $originalGuruId,
                            'tanggal' => $tanggalForSearch,
                            'jam_masuk' => $attendance->jam_masuk ?? $attendance->created_at->format('H:i'),
                            'status' => 'diganti',
                            'keterangan' => $request->keterangan,
                            'created_by' => $request->user()->id,
                            'assigned_by' => $request->user()->id,
                            'schedule_id' => $schedule ? $schedule->id : null
                        ]);

                        $attendance->status = 'tidak_hadir';
                        $attendance->save();

                        $resultAttendance = $newAttendance;
                    } catch (\Exception $e2) {
                        // Jika pembuatan juga gagal, kembalikan error
                        return response()->json([
                            'success' => false,
                            'message' => 'Gagal melakukan penggantian guru',
                            'error' => $e2->getMessage()
                        ], Response::HTTP_INTERNAL_SERVER_ERROR);
                    }
                }
            }

            // Kembalikan data hasil dan kehadiran_guru terbaru
            $kehadiranGuru = $this->buildKehadiranGuru($originalGuruId, $attendance->schedule->kelas ?? $attendance->kelas ?? null, $attendance->schedule->mata_pelajaran ?? null, $tanggalForSearch);

            return response()->json([
                'success' => true,
                'message' => 'Penggantian guru berhasil disimpan',
                'data' => [
                    'attendance' => $resultAttendance,
                    'proposal' => $proposal,
                    'kehadiran_guru' => $kehadiranGuru
                ]
            ], Response::HTTP_OK);

        } catch (\Illuminate\Validation\ValidationException $ve) {
            return response()->json([
                'success' => false,
                'message' => 'Validasi gagal',
                'errors' => $ve->errors()
            ], Response::HTTP_UNPROCESSABLE_ENTITY);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan saat penggantian guru',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Helper: membangun struktur kehadiran_guru untuk response
     * Menggabungkan TeacherAttendance dan GuruPengganti jika ada
     */
    private function buildKehadiranGuru($guruId, $kelas, $mataPelajaran, $tanggal)
    {
        try {
            $tanggalForSearch = $tanggal ? Carbon::parse($tanggal)->format('Y-m-d') : Carbon::today()->format('Y-m-d');

            $hariMapping = [
                'Monday' => 'Senin', 'Tuesday' => 'Selasa', 'Wednesday' => 'Rabu',
                'Thursday' => 'Kamis', 'Friday' => 'Jumat', 'Saturday' => 'Sabtu', 'Sunday' => 'Minggu'
            ];
            $hari = $hariMapping[Carbon::parse($tanggalForSearch)->format('l')] ?? Carbon::parse($tanggalForSearch)->format('l');

            // Cari jadwal jika memungkinkan
            $schedule = Schedule::where('kelas', $kelas)
                        ->where('mata_pelajaran', $mataPelajaran)
                        ->where('hari', $hari)
                        ->first();

            // Cari attendance untuk guru asli
            $attendanceQuery = TeacherAttendance::with(['guru', 'schedule'])
                                ->whereDate('tanggal', $tanggalForSearch)
                                ->where(function($q) use ($schedule, $guruId) {
                                    if ($schedule) {
                                        $q->where('schedule_id', $schedule->id)->where('guru_id', $guruId);
                                    } else {
                                        $q->where('guru_id', $guruId);
                                    }
                                });
            $attendance = $attendanceQuery->first();

            // Cari attendance pengganti (status diganti) atau yang mereferensikan guru asli
            $replacementQuery = TeacherAttendance::with('guru')
                                ->whereDate('tanggal', $tanggalForSearch)
                                ->where(function($q) use ($schedule, $guruId) {
                                    if ($schedule) {
                                        $q->where('schedule_id', $schedule->id)->where('status', 'diganti');
                                    } else {
                                        $q->where('guru_asli_id', $guruId)->where('status', 'diganti');
                                    }
                                });
            $replacementAttendance = $replacementQuery->first();

            // Proposals dari siswa
            $proposal = GuruPengganti::where('guru_asli_id', $guruId)
                        ->whereDate('tanggal', $tanggalForSearch)
                        ->where('kelas', $kelas)
                        ->first();

            $status = null;
            if ($attendance) {
                $status = $attendance->status;
            } elseif ($replacementAttendance) {
                $status = $replacementAttendance->status;
            } elseif ($proposal) {
                $status = 'proposal';
            } else {
                $status = 'tidak_tercatat';
            }

            return [
                'tanggal' => $tanggalForSearch,
                'hari' => $hari,
                'schedule' => $schedule ? [
                    'id' => $schedule->id,
                    'kelas' => $schedule->kelas,
                    'mata_pelajaran' => $schedule->mata_pelajaran,
                    'jam_mulai' => $schedule->jam_mulai,
                    'jam_selesai' => $schedule->jam_selesai,
                    'ruang' => $schedule->ruang
                ] : null,
                'status' => $status,
                'guru' => $attendance ? $attendance->guru : null,
                'guru_asli' => $attendance && $attendance->guru_asli_id ? ['id' => $attendance->guru_asli_id] : null,
                'guru_pengganti' => $replacementAttendance ? $replacementAttendance->guru : ($proposal ? (method_exists($proposal, 'guruPengganti') ? $proposal->guruPengganti : null) : null),
                'keterangan' => $attendance->keterangan ?? ($replacementAttendance->keterangan ?? ($proposal->keterangan ?? null)),
                'raw_attendance' => $attendance ? $attendance->toArray() : null,
                'proposal' => $proposal ? $proposal->toArray() : null
            ];

        } catch (\Exception $e) {
            return [
                'error' => true,
                'message' => 'Gagal membangun kehadiran_guru',
                'detail' => $e->getMessage()
            ];
        }
    }
}

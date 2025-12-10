<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Assignment;
use App\Models\User;
use Carbon\Carbon;

class AssignmentSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Get guru user
        $guru = User::where('role', 'guru')->first();

        if (!$guru) {
            $this->command->warn('No guru found. Please run UserSeeder first.');
            return;
        }

        $assignments = [
            [
                'guru_id' => $guru->id,
                'kelas' => 'X IPA 1',
                'mata_pelajaran' => 'Matematika',
                'judul' => 'Tugas Trigonometri',
                'deskripsi' => 'Kerjakan soal-soal trigonometri pada buku halaman 45-50. Kumpulkan jawaban dalam bentuk PDF.',
                'deadline' => Carbon::now()->addDays(7),
                'tipe' => 'tugas',
                'bobot' => 100,
            ],
            [
                'guru_id' => $guru->id,
                'kelas' => 'X IPA 1',
                'mata_pelajaran' => 'Fisika',
                'judul' => 'Laporan Praktikum Gerak Lurus',
                'deskripsi' => 'Buat laporan praktikum lengkap dengan data pengamatan, analisis, dan kesimpulan.',
                'deadline' => Carbon::now()->addDays(5),
                'tipe' => 'tugas',
                'bobot' => 100,
            ],
            [
                'guru_id' => $guru->id,
                'kelas' => 'X IPA 1',
                'mata_pelajaran' => 'Kimia',
                'judul' => 'Ulangan Harian Bab 1',
                'deskripsi' => 'Ulangan harian materi Struktur Atom dan Sistem Periodik Unsur.',
                'deadline' => Carbon::now()->addDays(3),
                'tipe' => 'ulangan',
                'bobot' => 100,
            ],
            [
                'guru_id' => $guru->id,
                'kelas' => 'XI IPA 1',
                'mata_pelajaran' => 'Biologi',
                'judul' => 'Tugas Sistem Pencernaan',
                'deskripsi' => 'Buat mind map tentang sistem pencernaan manusia beserta enzim-enzim yang bekerja.',
                'deadline' => Carbon::now()->addDays(10),
                'tipe' => 'tugas',
                'bobot' => 100,
            ],
            [
                'guru_id' => $guru->id,
                'kelas' => 'XI IPA 1',
                'mata_pelajaran' => 'Matematika',
                'judul' => 'Ujian Tengah Semester',
                'deskripsi' => 'Ujian mencakup materi Limit, Turunan, dan Integral.',
                'deadline' => Carbon::now()->addDays(14),
                'tipe' => 'ujian',
                'bobot' => 100,
            ],
        ];

        foreach ($assignments as $assignment) {
            Assignment::create($assignment);
        }

        $this->command->info('Assignments seeded successfully!');
    }
}

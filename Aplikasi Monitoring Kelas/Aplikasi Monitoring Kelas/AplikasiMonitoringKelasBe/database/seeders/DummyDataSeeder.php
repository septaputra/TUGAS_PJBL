<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\Schedule;
use App\Models\Teacher;
use Illuminate\Support\Facades\Hash;

class DummyDataSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $guru1 = User::create([
            'name' => 'Dr. borak Nurhaliza',
            'email' => 'borak.guru@sekolah.com',
            'password' => Hash::make('password123'),
            'role' => 'guru'
        ]);

        $guru2 = User::create([
            'name' => 'Bapak ahmadsurajar Sudrajat',
            'email' => 'ahmadsurajar.guru@sekolah.com',
            'password' => Hash::make('password123'),
            'role' => 'guru'
        ]);

        $siswa = User::create([
            'name' => 'sumba Pratama',
            'email' => 'sumba.siswa@sekolah.com',
            'password' => Hash::make('password123'),
            'role' => 'siswa'
        ]);

        // Create corresponding teacher records in the teachers table
        Teacher::create([
            'name' => 'Dr. borak Nurhaliza',
            'email' => 'borak.guru@sekolah.com',
            'password' => Hash::make('password123'),
            'mata_pelajaran' => null
        ]);

        Teacher::create([
            'name' => 'Bapak ahmadsurajar Sudrajat',
            'email' => 'ahmadsurajar.guru@sekolah.com',
            'password' => Hash::make('password123'),
            'mata_pelajaran' => null
        ]);

        // Membuat jadwal dummy
        Schedule::create([
            'hari' => 'Jumat',
            'kelas' => 'X IPA 1',
            'mata_pelajaran' => 'Matematika',
            'guru_id' => $guru1->id,
            'jam_mulai' => '02:30',
            'jam_selesai' => '09:00',
            'ruang' => 'Lab Matematika'
        ]);

        Schedule::create([
            'hari' => 'Sabtu',
            'kelas' => 'X IPA 1',
            'mata_pelajaran' => 'Matematika',
            'guru_id' => $guru1->id,
            'jam_mulai' => '02:35',
            'jam_selesai' => '12:00',
            'ruang' => 'Lab Matematika'
        ]);

        Schedule::create([
            'hari' => 'Jumat',
            'kelas' => 'X IPA 1',
            'mata_pelajaran' => 'RPL',
            'guru_id' => $guru1->id,
            'jam_mulai' => '02:59',
            'jam_selesai' => '09:00',
            'ruang' => 'Lab RPL'
        ]);


    }
}

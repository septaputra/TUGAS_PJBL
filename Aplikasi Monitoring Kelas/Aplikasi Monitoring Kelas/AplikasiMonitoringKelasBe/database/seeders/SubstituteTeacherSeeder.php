<?php

namespace Database\Seeders;

use App\Models\SubstituteTeacher;
use Illuminate\Database\Seeder;
use Carbon\Carbon;

class SubstituteTeacherSeeder extends Seeder
{
    public function run(): void
    {
        $rows = [
            ['name' => 'Budi Santoso', 'specialization' => 'Bahasa Inggris', 'phone_number' => '087112233445', 'available_from' => '01/02/2026', 'available_until' => '30/06/2026', 'notes' => 'Bersedia menggantikan untuk kelas 7 dan 8.'],
            ['name' => 'Rina Amelia', 'specialization' => 'Fisika', 'phone_number' => '081554433221', 'available_from' => '10/03/2026', 'available_until' => '10/05/2026', 'notes' => 'Fokus pada pengajaran di tingkat SMA.'],
            ['name' => 'Ahmad Zulkifli', 'specialization' => 'Seni Budaya', 'phone_number' => '089887766554', 'available_from' => '20/01/2026', 'available_until' => '28/02/2026', 'notes' => 'Fleksibel, dapat mengajar musik atau seni rupa.'],
            ['name' => 'Dewi Lestari', 'specialization' => 'Ilmu Pengetahuan Sosial (IPS)', 'phone_number' => '085778899001', 'available_from' => '05/04/2026', 'available_until' => '05/07/2026', 'notes' => 'Pengalaman mengajar Sejarah dan Geografi.'],
            ['name' => 'Eko Prasetyo', 'specialization' => 'Pendidikan Jasmani', 'phone_number' => '082223344556', 'available_from' => '01/03/2026', 'available_until' => '31/03/2026', 'notes' => 'Memiliki sertifikat P3K.'],
        ];

        foreach ($rows as $r) {
            SubstituteTeacher::create([
                'name' => $r['name'],
                'specialization' => $r['specialization'],
                'phone_number' => $r['phone_number'],
                'available_from' => Carbon::createFromFormat('d/m/Y', $r['available_from'])->toDateString(),
                'available_until' => Carbon::createFromFormat('d/m/Y', $r['available_until'])->toDateString(),
                'notes' => $r['notes'],
            ]);
        }
    }
}

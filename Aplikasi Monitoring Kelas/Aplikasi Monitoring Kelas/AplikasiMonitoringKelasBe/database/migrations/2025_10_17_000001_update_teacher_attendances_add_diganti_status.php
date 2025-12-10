<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        // Ubah enum status untuk menambahkan 'diganti'
        DB::statement("ALTER TABLE teacher_attendances MODIFY COLUMN status ENUM('hadir', 'telat', 'tidak_hadir', 'diganti') DEFAULT 'tidak_hadir'");

        // Tambah kolom untuk menyimpan guru asli yang tidak hadir (sebelum diganti)
        Schema::table('teacher_attendances', function (Blueprint $table) {
            $table->foreignId('guru_asli_id')->nullable()->after('guru_id')->constrained('users')->onDelete('set null');
            $table->foreignId('assigned_by')->nullable()->after('created_by')->constrained('users')->onDelete('set null');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('teacher_attendances', function (Blueprint $table) {
            $table->dropForeign(['guru_asli_id']);
            $table->dropForeign(['assigned_by']);
            $table->dropColumn(['guru_asli_id', 'assigned_by']);
        });

        DB::statement("ALTER TABLE teacher_attendances MODIFY COLUMN status ENUM('hadir', 'telat', 'tidak_hadir') DEFAULT 'tidak_hadir'");
    }
};

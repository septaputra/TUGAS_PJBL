<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('monitoring', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('guru_id');
            $table->unsignedBigInteger('pelapor_id'); // User yang melaporkan (siswa/admin)
            $table->enum('status_hadir', ['Hadir', 'Terlambat', 'Tidak Hadir']);
            $table->text('catatan')->nullable();
            $table->string('kelas');
            $table->string('mata_pelajaran');
            $table->date('tanggal');
            $table->time('jam_laporan');
            $table->timestamps();

            $table->foreign('guru_id')->references('id')->on('users')->onDelete('cascade');
            $table->foreign('pelapor_id')->references('id')->on('users')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('monitoring');
    }
};

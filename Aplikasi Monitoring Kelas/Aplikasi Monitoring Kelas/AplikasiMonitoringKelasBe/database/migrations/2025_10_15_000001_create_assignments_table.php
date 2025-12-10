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
        Schema::create('assignments', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('guru_id');
            $table->string('kelas', 10); // Contoh: 'X IPA 1', 'XI IPS 2'
            $table->string('mata_pelajaran');
            $table->string('judul');
            $table->text('deskripsi');
            $table->dateTime('deadline');
            $table->string('file_path')->nullable(); // Path ke file materi tugas
            $table->enum('tipe', ['tugas', 'ulangan', 'ujian'])->default('tugas');
            $table->integer('bobot')->default(100); // Nilai maksimal
            $table->timestamps();

            $table->foreign('guru_id')->references('id')->on('users')->onDelete('cascade');

            // Index untuk pencarian cepat
            $table->index(['kelas', 'mata_pelajaran']);
            $table->index('deadline');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('assignments');
    }
};

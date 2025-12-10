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
        Schema::create('assignment_submissions', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('assignment_id');
            $table->unsignedBigInteger('siswa_id');
            $table->string('file_path')->nullable(); // Path ke file jawaban siswa
            $table->text('keterangan')->nullable(); // Catatan dari siswa
            $table->enum('status', ['pending', 'late', 'graded'])->default('pending');
            $table->dateTime('submitted_at');
            $table->timestamps();

            $table->foreign('assignment_id')->references('id')->on('assignments')->onDelete('cascade');
            $table->foreign('siswa_id')->references('id')->on('users')->onDelete('cascade');

            // Unique constraint: siswa hanya bisa submit 1x per assignment
            $table->unique(['assignment_id', 'siswa_id']);

            // Index
            $table->index('status');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('assignment_submissions');
    }
};

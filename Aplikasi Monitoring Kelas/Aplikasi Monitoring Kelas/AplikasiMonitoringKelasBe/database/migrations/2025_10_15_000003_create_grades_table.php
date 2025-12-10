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
        Schema::create('grades', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('siswa_id');
            $table->unsignedBigInteger('assignment_id');
            $table->unsignedBigInteger('guru_id'); // Guru yang memberi nilai
            $table->decimal('nilai', 5, 2); // Nilai dengan 2 desimal (contoh: 85.50)
            $table->text('catatan')->nullable(); // Feedback dari guru
            $table->timestamps();

            $table->foreign('siswa_id')->references('id')->on('users')->onDelete('cascade');
            $table->foreign('assignment_id')->references('id')->on('assignments')->onDelete('cascade');
            $table->foreign('guru_id')->references('id')->on('users')->onDelete('cascade');

            // Unique constraint: 1 nilai per siswa per assignment
            $table->unique(['siswa_id', 'assignment_id']);

            // Index untuk pencarian
            $table->index('siswa_id');
            $table->index('assignment_id');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('grades');
    }
};

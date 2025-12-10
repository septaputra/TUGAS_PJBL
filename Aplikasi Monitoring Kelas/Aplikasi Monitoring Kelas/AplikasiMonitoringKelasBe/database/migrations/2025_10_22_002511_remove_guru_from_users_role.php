<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        // First, update all users with role 'guru' to another valid role (admin in this case)
        DB::table('users')
            ->where('role', 'guru')
            ->update(['role' => 'admin']);

        // Since MySQL enum modification can be tricky when there are records,
        // let's use a temporary approach by changing to a VARCHAR first, then back to enum
        DB::statement("ALTER TABLE users MODIFY role VARCHAR(255) NOT NULL DEFAULT 'siswa'");
        
        // Update the field to enum without 'guru' but including all other roles
        DB::statement("ALTER TABLE users MODIFY role ENUM('admin', 'siswa', 'kurikulum') NOT NULL DEFAULT 'siswa'");
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // Change to VARCHAR first to allow all values
        DB::statement("ALTER TABLE users MODIFY role VARCHAR(255) NOT NULL DEFAULT 'siswa'");
        
        // Then change back to the original enum values including 'guru'
        DB::statement("ALTER TABLE users MODIFY role ENUM('admin', 'guru', 'siswa', 'kurikulum') NOT NULL DEFAULT 'siswa'");
        
        // Optionally, we might want to migrate 'kurikulum' role users back to 'guru' if needed
    }
};

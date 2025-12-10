<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\Teacher;
use Illuminate\Support\Facades\Hash;

class MigrateUsersToTeachersSeeder extends Seeder
{
    /**
     * Run the database seeds to migrate users with role 'guru' to teachers table.
     */
    public function run(): void
    {
        // Get all users with role 'guru' from the users table
        $guruUsers = User::where('role', 'guru')->where('is_banned', false)->get();
        
        $migratedCount = 0;
        foreach ($guruUsers as $guruUser) {
            // Check if the teacher already exists in the teachers table to avoid duplicates
            $existingTeacher = Teacher::where('email', $guruUser->email)->first();
            
            if (!$existingTeacher) {
                // Create a new teacher record from the user data
                Teacher::create([
                    'name' => $guruUser->name,
                    'email' => $guruUser->email,
                    'password' => $guruUser->password, // Using the already hashed password
                    'mata_pelajaran' => $guruUser->mata_pelajaran ?? null,
                    'is_banned' => $guruUser->is_banned ?? false,
                    'email_verified_at' => $guruUser->email_verified_at,
                    'remember_token' => $guruUser->remember_token,
                    'created_at' => $guruUser->created_at,
                    'updated_at' => $guruUser->updated_at,
                ]);
                
                $migratedCount++;
            }
        }
        
        echo "Teachers migration completed. Migrated $migratedCount teachers from users table to teachers table.\n";
    }
}
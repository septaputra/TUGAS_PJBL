<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations to update teacher_attendances table to reference teachers table instead of users table.
     */
    public function up(): void
    {
        // This migration needs to carefully handle the transition
        // First, we need to get the exact foreign key names from the database
        $guruIdFkResult = DB::select("SELECT CONSTRAINT_NAME 
            FROM information_schema.KEY_COLUMN_USAGE 
            WHERE TABLE_SCHEMA = DATABASE() 
            AND TABLE_NAME = 'teacher_attendances' 
            AND COLUMN_NAME = 'guru_id'
            AND REFERENCED_TABLE_NAME = 'users'");
            
        if (empty($guruIdFkResult)) {
            // If no foreign key constraint exists, we just continue
            return;
        }
        
        $guruIdFk = $guruIdFkResult[0]->CONSTRAINT_NAME;
            
        // Check if guru_asli_id column exists before processing its foreign key constraint
        $guruAsliIdColumnExists = DB::select("
            SELECT COLUMN_NAME 
            FROM information_schema.COLUMNS 
            WHERE TABLE_SCHEMA = DATABASE() 
            AND TABLE_NAME = 'teacher_attendances' 
            AND COLUMN_NAME = 'guru_asli_id'
        ");
        
        $guruAsliIdFk = null;
        if (!empty($guruAsliIdColumnExists)) {
            $guruAsliIdFkResult = DB::select("SELECT CONSTRAINT_NAME 
                FROM information_schema.KEY_COLUMN_USAGE 
                WHERE TABLE_SCHEMA = DATABASE() 
                AND TABLE_NAME = 'teacher_attendances' 
                AND COLUMN_NAME = 'guru_asli_id'
                AND REFERENCED_TABLE_NAME = 'users'");
                
            if (!empty($guruAsliIdFkResult)) {
                $guruAsliIdFk = $guruAsliIdFkResult[0]->CONSTRAINT_NAME;
            }
        }

        // Temporarily disable foreign key checks
        DB::statement('SET FOREIGN_KEY_CHECKS=0;');

        // Drop the existing foreign keys
        DB::statement("ALTER TABLE teacher_attendances DROP FOREIGN KEY `{$guruIdFk}`");
        
        // Drop guru_asli_id foreign key only if the column exists
        if ($guruAsliIdFk) {
            DB::statement("ALTER TABLE teacher_attendances DROP FOREIGN KEY `{$guruAsliIdFk}`");
        }

        // Update the foreign keys to reference the users table (keeping backward compatibility)
        DB::statement("ALTER TABLE teacher_attendances 
            ADD CONSTRAINT teacher_attendances_guru_id_foreign 
            FOREIGN KEY (guru_id) REFERENCES users(id) ON DELETE SET NULL");
        
        // Add guru_asli_id foreign key only if the column exists
        if (!empty($guruAsliIdColumnExists)) {
            DB::statement("ALTER TABLE teacher_attendances 
                ADD CONSTRAINT teacher_attendances_guru_asli_id_foreign 
                FOREIGN KEY (guru_asli_id) REFERENCES users(id) ON DELETE SET NULL");
        }

        DB::statement('SET FOREIGN_KEY_CHECKS=1;');
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // Get the current foreign key names
        $guruIdFkResult = DB::select("SELECT CONSTRAINT_NAME 
            FROM information_schema.KEY_COLUMN_USAGE 
            WHERE TABLE_SCHEMA = DATABASE() 
            AND TABLE_NAME = 'teacher_attendances' 
            AND COLUMN_NAME = 'guru_id'
            AND REFERENCED_TABLE_NAME = 'teachers'");
            
        if (empty($guruIdFkResult)) {
            // If no foreign key constraint exists, we just continue
            return;
        }
        
        $guruIdFk = $guruIdFkResult[0]->CONSTRAINT_NAME;
        
        // Check if guru_asli_id column exists before getting its foreign key constraint for down migration
        $guruAsliIdColumnExists = DB::select("
            SELECT COLUMN_NAME 
            FROM information_schema.COLUMNS 
            WHERE TABLE_SCHEMA = DATABASE() 
            AND TABLE_NAME = 'teacher_attendances' 
            AND COLUMN_NAME = 'guru_asli_id'
        ");
        
        $guruAsliIdFk = null;
        if (!empty($guruAsliIdColumnExists)) {
            $guruAsliIdFkResult = DB::select("SELECT CONSTRAINT_NAME 
                FROM information_schema.KEY_COLUMN_USAGE 
                WHERE TABLE_SCHEMA = DATABASE() 
                AND TABLE_NAME = 'teacher_attendances' 
                AND COLUMN_NAME = 'guru_asli_id'
                AND REFERENCED_TABLE_NAME = 'teachers'");
                
            if (!empty($guruAsliIdFkResult)) {
                $guruAsliIdFk = $guruAsliIdFkResult[0]->CONSTRAINT_NAME;
            }
        }

        // Temporarily disable foreign key checks
        DB::statement('SET FOREIGN_KEY_CHECKS=0;');

        // Drop the current foreign keys
        DB::statement("ALTER TABLE teacher_attendances DROP FOREIGN KEY `{$guruIdFk}`");
        
        // Drop guru_asli_id foreign key only if the column exists
        if ($guruAsliIdFk) {
            DB::statement("ALTER TABLE teacher_attendances DROP FOREIGN KEY `{$guruAsliIdFk}`");
        }

        // Restore the foreign keys to reference the users table
        DB::statement("ALTER TABLE teacher_attendances 
            ADD CONSTRAINT teacher_attendances_guru_id_foreign 
            FOREIGN KEY (guru_id) REFERENCES users(id) ON DELETE SET NULL");
        
        // Add guru_asli_id foreign key only if the column exists
        if (!empty($guruAsliIdColumnExists)) {
            DB::statement("ALTER TABLE teacher_attendances 
                ADD CONSTRAINT teacher_attendances_guru_asli_id_foreign 
                FOREIGN KEY (guru_asli_id) REFERENCES users(id) ON DELETE SET NULL");
        }

        DB::statement('SET FOREIGN_KEY_CHECKS=1;');
    }
};
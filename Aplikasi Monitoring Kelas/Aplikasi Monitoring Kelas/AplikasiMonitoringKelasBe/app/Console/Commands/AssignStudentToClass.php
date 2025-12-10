<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use App\Models\User;
use App\Models\Kelas;

class AssignStudentToClass extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'student:assign-class 
                            {user_id : ID of the student user}
                            {class_id : ID of the class to assign}';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Assign a student user to a class';

    /**
     * Execute the console command.
     */
    public function handle()
    {
        $userId = $this->argument('user_id');
        $classId = $this->argument('class_id');

        // Find the student user
        $user = User::find($userId);
        if (!$user) {
            $this->error("User with ID {$userId} not found.");
            return 1;
        }

        // Verify the user is a student
        if ($user->role !== 'siswa') {
            $this->error("User with ID {$userId} is not a student. Current role: {$user->role}");
            return 1;
        }

        // Find the class
        $class = Kelas::find($classId);
        if (!$class) {
            $this->error("Class with ID {$classId} not found.");
            return 1;
        }

        // Assign the class to the user
        $user->class_id = $classId;
        $user->save();

        $this->info("Successfully assigned student '{$user->name}' to class '{$class->nama_kelas}'");
        return 0;
    }
}
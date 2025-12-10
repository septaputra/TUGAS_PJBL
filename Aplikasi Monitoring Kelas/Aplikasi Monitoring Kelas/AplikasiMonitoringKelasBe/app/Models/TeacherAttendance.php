<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class TeacherAttendance extends Model
{
    use HasFactory;

    protected $fillable = [
        'schedule_id',
        'guru_id',
        'guru_asli_id',
        'tanggal',
        'jam_masuk',
        'status',
        'keterangan',
        'created_by',
        'assigned_by'
    ];

    protected $casts = [
        'tanggal' => 'date',
        'jam_masuk' => 'datetime:H:i',
    ];

    public function schedule()
    {
        return $this->belongsTo(Schedule::class);
    }

    public function guru()
    {
        // The relationship still points to User model for backward compatibility
        // In a complete migration to teachers table, this would reference Teacher model
        return $this->belongsTo(User::class, 'guru_id');
    }

    /**
     * New relation: teacher record from `teachers` table (preferred source)
     */
    public function guruTeacher()
    {
        return $this->belongsTo(Teacher::class, 'guru_id');
    }

    public function guruAsli()
    {
        // The relationship still points to User model for backward compatibility
        // In a complete migration to teachers table, this would reference Teacher model
        return $this->belongsTo(User::class, 'guru_asli_id');
    }

    public function guruAsliTeacher()
    {
        return $this->belongsTo(Teacher::class, 'guru_asli_id');
    }

    public function createdBy()
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function assignedBy()
    {
        return $this->belongsTo(User::class, 'assigned_by');
    }
}

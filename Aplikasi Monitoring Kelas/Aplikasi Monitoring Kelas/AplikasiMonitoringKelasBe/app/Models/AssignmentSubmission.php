<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class AssignmentSubmission extends Model
{
    use HasFactory;

    protected $fillable = [
        'assignment_id',
        'siswa_id',
        'file_path',
        'keterangan',
        'status',
        'submitted_at'
    ];

    protected $casts = [
        'submitted_at' => 'datetime',
    ];

    /**
     * Relasi ke assignment
     */
    public function assignment()
    {
        return $this->belongsTo(Assignment::class);
    }

    /**
     * Relasi ke siswa
     */
    public function siswa()
    {
        return $this->belongsTo(User::class, 'siswa_id');
    }

    /**
     * Relasi ke grade
     */
    public function grade()
    {
        return $this->hasOne(Grade::class, 'assignment_id', 'assignment_id')
                    ->where('siswa_id', $this->siswa_id);
    }

    /**
     * Get full URL for file
     */
    public function getFileUrlAttribute()
    {
        return $this->file_path ? url('storage/' . $this->file_path) : null;
    }

    /**
     * Cek apakah submission terlambat
     */
    public function isLate()
    {
        return $this->submitted_at > $this->assignment->deadline;
    }
}

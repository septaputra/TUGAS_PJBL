<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Assignment extends Model
{
    use HasFactory;

    protected $fillable = [
        'guru_id',
        'kelas',
        'mata_pelajaran',
        'judul',
        'deskripsi',
        'deadline',
        'file_path',
        'tipe',
        'bobot'
    ];

    protected $casts = [
        'deadline' => 'datetime',
        'bobot' => 'integer',
    ];

    /**
     * Relasi ke model User (guru yang membuat tugas)
     */
    public function guru()
    {
        return $this->belongsTo(User::class, 'guru_id');
    }

    /**
     * Relasi ke submissions (pengumpulan tugas)
     */
    public function submissions()
    {
        return $this->hasMany(AssignmentSubmission::class);
    }

    /**
     * Relasi ke grades (nilai)
     */
    public function grades()
    {
        return $this->hasMany(Grade::class);
    }

    /**
     * Cek apakah assignment sudah melewati deadline
     */
    public function isPastDeadline()
    {
        return $this->deadline < now();
    }

    /**
     * Get full URL for file
     */
    public function getFileUrlAttribute()
    {
        return $this->file_path ? url('storage/' . $this->file_path) : null;
    }
}

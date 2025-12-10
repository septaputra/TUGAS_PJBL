<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Grade extends Model
{
    use HasFactory;

    protected $fillable = [
        'siswa_id',
        'assignment_id',
        'guru_id',
        'nilai',
        'catatan'
    ];

    protected $casts = [
        'nilai' => 'decimal:2',
    ];

    /**
     * Relasi ke siswa
     */
    public function siswa()
    {
        return $this->belongsTo(User::class, 'siswa_id');
    }

    /**
     * Relasi ke assignment
     */
    public function assignment()
    {
        return $this->belongsTo(Assignment::class);
    }

    /**
     * Relasi ke guru yang memberi nilai
     */
    public function guru()
    {
        return $this->belongsTo(User::class, 'guru_id');
    }

    /**
     * Get grade letter (A, B, C, D, E)
     */
    public function getGradeLetterAttribute()
    {
        if ($this->nilai >= 85) return 'A';
        if ($this->nilai >= 75) return 'B';
        if ($this->nilai >= 65) return 'C';
        if ($this->nilai >= 55) return 'D';
        return 'E';
    }
}

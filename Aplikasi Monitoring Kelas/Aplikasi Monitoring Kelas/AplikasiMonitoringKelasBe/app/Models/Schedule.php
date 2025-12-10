<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Schedule extends Model
{
    use HasFactory;

    protected $fillable = [
        'hari',
        'kelas',
        'mata_pelajaran',
        'guru_id',
        'jam_mulai',
        'jam_selesai',
        'ruang'
    ];

    protected $casts = [
        'jam_mulai' => 'datetime:H:i',
        'jam_selesai' => 'datetime:H:i',
    ];

    /**
     * Relasi ke model User (guru)
     */
    public function guru()
    {
        return $this->belongsTo(User::class, 'guru_id');
    }

    /**
     * Optional relation to Teacher model (teachers table)
     * Keeps backward compatibility while allowing API to use teachers table
     */
    public function teacher()
    {
        return $this->belongsTo(Teacher::class, 'guru_id');
    }
}

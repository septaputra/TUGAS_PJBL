<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class GuruPengganti extends Model
{
    use HasFactory;

    protected $table = 'guru_pengganti';

    protected $fillable = [
        'guru_pengganti_id',
        'guru_asli_id',
        'kelas',
        'mata_pelajaran',
        'tanggal',
        'jam_mulai',
        'jam_selesai',
        'ruang',
        'keterangan',
        'assigned_by'
    ];

    protected $casts = [
        'tanggal' => 'date',
    ];

    /**
     * Relasi ke guru pengganti
     */
    public function guruPengganti()
    {
        return $this->belongsTo(User::class, 'guru_pengganti_id');
    }

    /**
     * Relasi ke guru asli yang digantikan
     */
    public function guruAsli()
    {
        return $this->belongsTo(User::class, 'guru_asli_id');
    }

    /**
     * Relasi ke user kurikulum yang assign
     */
    public function assignedBy()
    {
        return $this->belongsTo(User::class, 'assigned_by');
    }
}

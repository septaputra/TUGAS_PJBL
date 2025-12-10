<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Monitoring extends Model
{
    use HasFactory;

    protected $table = 'monitoring';

    protected $fillable = [
        'guru_id',
        'pelapor_id',
        'status_hadir',
        'catatan',
        'kelas',
        'mata_pelajaran',
        'tanggal',
        'jam_laporan'
    ];

    protected $casts = [
        'tanggal' => 'date',
    ];

    /**
     * Accessor untuk mendapatkan tanggal dalam format Y-m-d
     */
    public function getTanggalAttribute($value)
    {
        if (!$value) {
            return null;
        }

        // Parse value as date and return in Y-m-d format
        try {
            $date = \Carbon\Carbon::parse($value);
            return $date->format('Y-m-d');
        } catch (\Exception $e) {
            return $value;
        }
    }

    /**
     * Mutator untuk menyimpan tanggal
     */
    public function setTanggalAttribute($value)
    {
        if (!$value) {
            $this->attributes['tanggal'] = null;
            return;
        }

        // Parse and store as date
        try {
            $this->attributes['tanggal'] = \Carbon\Carbon::parse($value)->format('Y-m-d');
        } catch (\Exception $e) {
            $this->attributes['tanggal'] = $value;
        }
    }

    /**
     * Accessor untuk mendapatkan jam laporan dalam format H:i
     */
    public function getJamLaporanAttribute($value)
    {
        if (!$value) {
            return null;
        }

        // Parse value as datetime and return only time in H:i format
        try {
            $datetime = \Carbon\Carbon::parse($value);
            return $datetime->format('H:i');
        } catch (\Exception $e) {
            return $value;
        }
    }

    /**
     * Mutator untuk menyimpan jam laporan
     */
    public function setJamLaporanAttribute($value)
    {
        if (!$value) {
            $this->attributes['jam_laporan'] = null;
            return;
        }

        // If value is just time (H:i format), combine with today's date
        if (preg_match('/^\d{2}:\d{2}$/', $value)) {
            $this->attributes['jam_laporan'] = \Carbon\Carbon::today()->setTimeFromTimeString($value);
        } else {
            // Otherwise, parse as full datetime
            $this->attributes['jam_laporan'] = \Carbon\Carbon::parse($value);
        }
    }

    /**
     * Relasi ke model User (guru)
     */
    public function guru()
    {
        return $this->belongsTo(User::class, 'guru_id');
    }

    /**
     * Relasi ke model User (pelapor)
     */
    public function pelapor()
    {
        return $this->belongsTo(User::class, 'pelapor_id');
    }
}

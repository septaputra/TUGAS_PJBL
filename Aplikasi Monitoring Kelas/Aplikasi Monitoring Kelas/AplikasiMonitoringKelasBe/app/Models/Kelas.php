<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Kelas extends Model
{
    protected $table = 'classes'; // Set the table name to 'classes'
    
    protected $fillable = [
        'nama_kelas',
        'kode_kelas',
    ];
}

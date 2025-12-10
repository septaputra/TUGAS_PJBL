<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class SubstituteTeacher extends Model
{
    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'name',
        'specialization',
        'phone_number',
        'available_from',
        'available_until',
        'notes',
    ];

    /**
     * The attributes that should be cast to native types.
     *
     * @var array<string, string>
     */
    protected $casts = [
        'available_from' => 'date',
        'available_until' => 'date',
    ];
}

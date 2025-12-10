<?php

namespace App\Models;

// use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    /** @use HasFactory<\Database\Factories\UserFactory> */
    use HasFactory, Notifiable, HasApiTokens;

    /**
     * The attributes that are mass assignable.
     *
     * @var list<string>
     */
    protected $fillable = [
        'name',
        'email',
        'password',
        'role',
        'mata_pelajaran',
        'is_banned',
        'class_id',
    ];

    /**
     * The attributes that should be hidden for serialization.
     *
     * @var list<string>
     */
    protected $hidden = [
        'password',
        'remember_token',
    ];

    /**
     * Get the attributes that should be cast.
     *
     * @return array<string, string>
     */
    protected function casts(): array
    {
        return [
            'email_verified_at' => 'datetime',
            'password' => 'hashed',
            'is_banned' => 'boolean',
        ];
    }

    /**
     * Check if user is banned
     *
     * @return bool
     */
    public function isBanned(): bool
    {
        return $this->is_banned ?? false;
    }

    /**
     * Check if user can login (not banned)
     *
     * @return bool
     */
    public function canLogin(): bool
    {
        return !$this->isBanned();
    }

    /**
     * Get the class that the user belongs to.
     */
    public function kelas(): BelongsTo
    {
        return $this->belongsTo(Kelas::class, 'class_id');
    }

    /**
     * Ensure when the User is converted to array/json, the `kelas` key
     * is a simple string (nama_kelas) instead of a nested object.
     */
    public function toArray()
    {
        $array = parent::toArray();

        if (isset($array['kelas']) && is_array($array['kelas']) && isset($array['kelas']['nama_kelas'])) {
            $array['kelas'] = $array['kelas']['nama_kelas'];
        } else {
            // If relation not loaded, try to resolve from relation (without forcing extra query when possible)
            if (!isset($array['kelas']) || is_null($array['kelas'])) {
                $kelasModel = $this->relationLoaded('kelas') ? $this->getRelation('kelas') : $this->kelas()->first();
                $array['kelas'] = $kelasModel ? $kelasModel->nama_kelas : null;
            }
        }

        return $array;
    }


}

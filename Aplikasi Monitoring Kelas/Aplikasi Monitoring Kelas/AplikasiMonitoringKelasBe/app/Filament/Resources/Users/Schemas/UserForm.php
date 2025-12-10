<?php

namespace App\Filament\Resources\Users\Schemas;

use App\Models\Kelas;
use Filament\Forms\Components\DateTimePicker;
use Filament\Forms\Components\Select;
use Filament\Forms\Components\TextInput;
use Filament\Forms\Components\Toggle;
use Filament\Schemas\Schema;

class UserForm
{
    public static function configure(Schema $schema): Schema
    {
        return $schema
            ->components([
                TextInput::make('name')
                    ->required(),
                TextInput::make('email')
                    ->label('Email address')
                    ->email()
                    ->required(),
                DateTimePicker::make('email_verified_at'),
                TextInput::make('password')
                    ->password()
                    ->required(),
                Select::make('role')
                    ->options(['admin' => 'Admin', 'guru' => 'Guru', 'siswa' => 'Siswa', 'kurikulum' => 'Kurikulum', 'kepala_sekolah' => 'Kepala Sekolah'])
                    ->default('siswa')
                    ->required()
                    ->live(), // Add live() to enable reactive form behavior
                Select::make('class_id')
                    ->label('Class')
                    ->relationship('kelas', 'nama_kelas') // Use the relationship defined in the User model
                    ->placeholder('Select a class (for students only)')
                    ->visible(fn ($get) => $get('role') === 'siswa') // Only visible for students
                    ->searchable()
                    ->preload(),
                TextInput::make('mata_pelajaran')
                    ->default(null),
                Toggle::make('is_banned')
                    ->required(),
            ]);
    }
}

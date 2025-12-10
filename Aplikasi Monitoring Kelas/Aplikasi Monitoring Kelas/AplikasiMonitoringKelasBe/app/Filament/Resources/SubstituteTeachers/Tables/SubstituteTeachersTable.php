<?php

namespace App\Filament\Resources\SubstituteTeachers\Tables;

use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Schema;

class SubstituteTeachersTable
{
    public static function configure(Schema $schema): Schema
    {
        return $schema
            ->columns([
                TextColumn::make('name')
                    ->label('Nama Guru Pengganti')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('specialization')
                    ->label('Spesialisasi')
                    ->searchable(),
                TextColumn::make('phone_number')
                    ->label('Nomor Kontak'),
                TextColumn::make('available_from')
                    ->label('Mulai Tersedia')
                    ->date('d M Y')
                    ->sortable(),
                TextColumn::make('available_until')
                    ->label('Akhir Ketersediaan')
                    ->date('d M Y')
                    ->sortable(),
            ]);
    }
}
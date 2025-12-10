<?php

namespace App\Filament\Resources\SubstituteTeachers\Schemas;

use Filament\Tables\Columns\TextColumn;

class SubstituteTeacherTable
{
    // Accept the adapter or the actual Table Schema; when called from
    // `SubstituteTeacherResource::table()` we pass an adapter object that
    // exposes `columns(array $columns)`. So don't type-hint here and just
    // configure the passed object.
    public static function configure($schema): void
    {
        $schema->columns([
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

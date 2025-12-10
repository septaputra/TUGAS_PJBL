<?php

namespace App\Filament\Resources\Users\Tables;

use Filament\Actions\BulkActionGroup;
use Filament\Actions\DeleteBulkAction;
use Filament\Actions\EditAction;
use Filament\Actions\ViewAction;
use Filament\Tables\Columns\IconColumn;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Table;

class UsersTable
{
    public static function configure(Table $table): Table
    {
        return $table
            ->columns([
                TextColumn::make('name')
                    ->searchable(),
                TextColumn::make('email')
                    ->label('Email address')
                    ->searchable(),
                TextColumn::make('email_verified_at')
                    ->dateTime()
                    ->sortable(),
                TextColumn::make('role'),
                TextColumn::make('kelas.nama_kelas')
                    ->label('Class')
                    ->sortable()
                    ->searchable(),
                TextColumn::make('mata_pelajaran')
                    ->searchable(),
                IconColumn::make('is_banned')
                    ->boolean(),
                TextColumn::make('created_at')
                    ->dateTime()
                    ->sortable()
                    ->toggleable(isToggledHiddenByDefault: true),
                TextColumn::make('updated_at')
                    ->dateTime()
                    ->sortable()
                    ->toggleable(isToggledHiddenByDefault: true),
            ])
            ->filters([
                \Filament\Tables\Filters\SelectFilter::make('role')
                    ->options([
                        'admin' => 'Admin',
                        'guru' => 'Guru',
                        'siswa' => 'Siswa',
                        'kurikulum' => 'Kurikulum',
                        'kepala_sekolah' => 'Kepala Sekolah'
                    ]),
                \Filament\Tables\Filters\SelectFilter::make('class_id')
                    ->label('Class')
                    ->relationship('kelas', 'nama_kelas')
                    ->searchable()
                    ->preload()
            ])
            ->recordActions([
                ViewAction::make(),
                EditAction::make(),
            ])
            ->toolbarActions([
                BulkActionGroup::make([
                    DeleteBulkAction::make(),
                ]),
            ]);
    }
}

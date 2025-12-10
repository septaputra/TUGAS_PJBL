<?php

namespace App\Filament\Resources\SubstituteTeachers\Schemas; // PASTIKAN NAMESPACE BENAR

use Filament\Forms\Components\DatePicker;
use Filament\Forms\Components\TextInput;
use Filament\Forms\Components\Textarea;

class SubstituteTeacherForm
{
    // Accept the adapter object produced in the Resource and configure it.
    public static function configure($schema): void
    {
        $schema->columns(2)
            ->components([
                TextInput::make('name')->label('Nama Lengkap')->required()->columnSpanFull(),
                TextInput::make('specialization')->label('Spesialisasi')->required(),
                TextInput::make('phone_number')->label('Nomor Kontak')->tel()->required(),
                DatePicker::make('available_from')->label('Tersedia Dari')->required(),
                DatePicker::make('available_until')->label('Tersedia Hingga')->required(),
                Textarea::make('notes')->label('Catatan Tambahan')->columnSpanFull(),
            ]);
    }
}
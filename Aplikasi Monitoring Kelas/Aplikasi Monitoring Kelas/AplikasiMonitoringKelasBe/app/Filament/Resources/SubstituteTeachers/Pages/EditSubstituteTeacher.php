<?php

namespace App\Filament\Resources\SubstituteTeachers\Pages;

use App\Filament\Resources\SubstituteTeachers\SubstituteTeacherResource;
use Filament\Actions\DeleteAction;
use Filament\Actions\ViewAction;
use Filament\Resources\Pages\EditRecord;

class EditSubstituteTeacher extends EditRecord
{
    protected static string $resource = SubstituteTeacherResource::class;

    protected function getHeaderActions(): array
    {
        return [
            ViewAction::make(),
            DeleteAction::make(),
        ];
    }
}

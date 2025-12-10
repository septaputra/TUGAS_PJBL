<?php

namespace App\Filament\Resources\SubstituteTeachers\Pages;

use App\Filament\Resources\SubstituteTeachers\SubstituteTeacherResource;
use Filament\Actions\EditAction;
use Filament\Resources\Pages\ViewRecord;

class ViewSubstituteTeacher extends ViewRecord
{
    protected static string $resource = SubstituteTeacherResource::class;

    protected function getHeaderActions(): array
    {
        return [
            EditAction::make(),
        ];
    }
}

<?php

namespace App\Filament\Resources\SubstituteTeachers\Pages;

use App\Filament\Resources\SubstituteTeachers\SubstituteTeacherResource;
use Filament\Actions\CreateAction;
use Filament\Resources\Pages\ListRecords;

class ListSubstituteTeachers extends ListRecords
{
    protected static string $resource = SubstituteTeacherResource::class;

    protected function getHeaderActions(): array
    {
        return [
            CreateAction::make(),
        ];
    }

    protected function getHeaderWidgets(): array
    {
        return [
            \App\Filament\Widgets\ManagementHeader::class,
            \App\Filament\Widgets\SummaryCards::class,
        ];
    }
}

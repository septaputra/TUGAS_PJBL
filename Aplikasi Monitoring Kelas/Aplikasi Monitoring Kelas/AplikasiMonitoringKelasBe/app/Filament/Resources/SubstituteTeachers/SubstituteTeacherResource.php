<?php

namespace App\Filament\Resources\SubstituteTeachers;

use App\Models\SubstituteTeacher;
use Filament\Schemas\Schema;
use Filament\Resources\Resource;
use Filament\Tables;
use Filament\Tables\Table;
use Filament\Actions\ViewAction;
use Filament\Actions\EditAction;
use Filament\Actions\BulkActionGroup;
use Filament\Actions\DeleteBulkAction;
use BackedEnum;

// Import Skema Form dan Tabel yang terpisah
use App\Filament\Resources\SubstituteTeachers\Schemas\SubstituteTeacherForm;
use App\Filament\Resources\SubstituteTeachers\Schemas\SubstituteTeacherTable;

use App\Filament\Resources\SubstituteTeachers\Pages;

class SubstituteTeacherResource extends Resource
{
    protected static ?string $model = SubstituteTeacher::class;
    protected static BackedEnum|string|null $navigationIcon = 'heroicon-o-users';

    public static function getModelLabel(): string { return 'Guru Pengganti'; }
    public static function getPluralModelLabel(): string { return 'Guru Pengganti'; }
    public static function getNavigationGroup(): ?string { return 'Manajemen Akademik'; }

    // Lightweight adapters so your external Schema classes can be used
    protected static function makeFormSchemaAdapter(): object
    {
        return new class {
            public array $components = [];
            public int $columns = 1;
            public function columns(int $count) { $this->columns = $count; return $this; }
            public function components(array $components) { $this->components = $components; return $this; }
            public function getComponents(): array { return $this->components; }
        };
    }

    protected static function makeTableSchemaAdapter(): object
    {
        return new class {
            public array $columnsArr = [];
            public function columns(array $columns) { $this->columnsArr = $columns; return $this; }
            public function getColumns(): array { return $this->columnsArr; }
        };
    }

    public static function form(Schema $schema): Schema
    {
        $adapter = self::makeFormSchemaAdapter();
        SubstituteTeacherForm::configure($adapter);

        return $schema->schema($adapter->getComponents());
    }

    public static function table(Table $table): Table
    {
        $adapter = self::makeTableSchemaAdapter();
        SubstituteTeacherTable::configure($adapter);

        return $table
            ->columns($adapter->getColumns())
            ->filters([
                // Tambahkan filter di sini bila perlu
            ])
            ->actions([
                ViewAction::make(),
                EditAction::make(),
            ])
            ->bulkActions([
                BulkActionGroup::make([
                    DeleteBulkAction::make(),
                ]),
            ]);
    }

    public static function getRelations(): array
    {
        return [
            // Tambahkan relasi resource di sini bila diperlukan
        ];
    }

    public static function getPages(): array
    {
        return [
            'index' => Pages\ListSubstituteTeachers::route('/'),
            'create' => Pages\CreateSubstituteTeacher::route('/create'),
            'edit' => Pages\EditSubstituteTeacher::route('/{record}/edit'),
            'view' => Pages\ViewSubstituteTeacher::route('/{record}'),
        ];
    }
}
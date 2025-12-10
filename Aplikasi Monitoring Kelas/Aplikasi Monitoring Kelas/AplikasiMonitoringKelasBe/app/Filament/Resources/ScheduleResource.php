<?php

namespace App\Filament\Resources;

use App\Filament\Resources\ScheduleResource\Pages;
use App\Models\Kelas;
use App\Models\Schedule;
use App\Models\Subject;
use App\Models\Teacher;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Tables;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Table;
use BackedEnum;
use UnitEnum;

class ScheduleResource extends Resource
{
    protected static ?string $model = Schedule::class;

    protected static string|BackedEnum|null $navigationIcon = 'heroicon-o-calendar-days';

    protected static ?string $navigationLabel = 'Jadwal';

    protected static ?string $modelLabel = 'Jadwal';

    protected static UnitEnum|string|null $navigationGroup = 'Manajemen Akademik';

    public static function form(Schema $schema): Schema
    {
        return $schema
            ->schema([
                \Filament\Forms\Components\Select::make('hari')
                    ->label('Day')
                    ->options([
                        'Senin' => 'Senin',
                        'Selasa' => 'Selasa',
                        'Rabu' => 'Rabu',
                        'Kamis' => 'Kamis',
                        'Jumat' => 'Jumat',
                        'Sabtu' => 'Sabtu',
                    ])
                    ->required()
                    ->native(false)
                    ->searchable()
                    ->columnSpanFull(),

                \Filament\Forms\Components\Select::make('kelas')
                    ->label('Class')
                    ->options(function () {
                        return Kelas::pluck('nama_kelas', 'nama_kelas')->toArray();
                    })
                    ->required()
                    ->native(false)
                    ->searchable()
                    ->helperText('Select the class for this schedule'),

                \Filament\Forms\Components\Select::make('mata_pelajaran')
                    ->label('Subject')
                    ->options(function () {
                        return Subject::pluck('nama', 'nama')->toArray();
                    })
                    ->required()
                    ->native(false)
                    ->searchable()
                    ->helperText('Select the subject to be taught'),

                \Filament\Forms\Components\Select::make('guru_id')
                    ->label('Teacher')
                    ->options(function () {
                        return Teacher::pluck('name', 'id')->toArray();
                    })
                    ->required()
                    ->native(false)
                    ->searchable()
                    ->helperText('Select the teacher for this schedule'),

                \Filament\Forms\Components\TimePicker::make('jam_mulai')
                    ->label('Start Time')
                    ->required()
                    ->seconds(false)
                    ->helperText('Schedule start time (HH:MM)'),

                \Filament\Forms\Components\TimePicker::make('jam_selesai')
                    ->label('End Time')
                    ->required()
                    ->seconds(false)
                    ->after('jam_mulai')
                    ->helperText('Schedule end time (HH:MM)'),

                \Filament\Forms\Components\TextInput::make('ruang')
                    ->label('Room')
                    ->maxLength(255)
                    ->nullable()
                    ->helperText('Optional: Room number or name')
                    ->columnSpanFull(),
            ])
            ->columns(2);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                TextColumn::make('hari')
                    ->label('Day')
                    ->sortable()
                    ->searchable()
                    ->badge()
                    ->color(fn (string $state): string => match ($state) {
                        'Senin' => 'info',
                        'Selasa' => 'success',
                        'Rabu' => 'warning',
                        'Kamis' => 'danger',
                        'Jumat' => 'primary',
                        'Sabtu' => 'gray',
                        default => 'gray',
                    }),

                TextColumn::make('kelas')
                    ->label('Class')
                    ->searchable()
                    ->sortable(),

                TextColumn::make('mata_pelajaran')
                    ->label('Subject')
                    ->searchable()
                    ->sortable()
                    ->wrap(),

                TextColumn::make('guru.name')
                    ->label('Teacher')
                    ->searchable()
                    ->sortable(),

                TextColumn::make('jam_mulai')
                    ->label('Start Time')
                    ->time('H:i')
                    ->sortable(),

                TextColumn::make('jam_selesai')
                    ->label('End Time')
                    ->time('H:i')
                    ->sortable(),

                TextColumn::make('ruang')
                    ->label('Room')
                    ->searchable()
                    ->placeholder('—')
                    ->toggleable(isToggledHiddenByDefault: false),

                TextColumn::make('created_at')
                    ->label('Created At')
                    ->dateTime()
                    ->sortable()
                    ->toggleable(isToggledHiddenByDefault: true),

                TextColumn::make('updated_at')
                    ->label('Updated At')
                    ->dateTime()
                    ->sortable()
                    ->toggleable(isToggledHiddenByDefault: true),
            ])
            ->filters([
                Tables\Filters\SelectFilter::make('hari')
                    ->label('Day')
                    ->options([
                        'Senin' => 'Senin',
                        'Selasa' => 'Selasa',
                        'Rabu' => 'Rabu',
                        'Kamis' => 'Kamis',
                        'Jumat' => 'Jumat',
                        'Sabtu' => 'Sabtu',
                    ]),

                Tables\Filters\SelectFilter::make('kelas')
                    ->label('Class')
                    ->options(function () {
                        return Kelas::pluck('nama_kelas', 'nama_kelas')->toArray();
                    }),
            ])
            ->recordActions([
                \Filament\Actions\ViewAction::make(),
                \Filament\Actions\EditAction::make(),
            ])
            ->toolbarActions([
                \Filament\Actions\BulkActionGroup::make([
                    \Filament\Actions\DeleteBulkAction::make(),
                ]),
            ])
            ->defaultSort('hari', 'asc')
            ->defaultSort('jam_mulai', 'asc');
    }

    public static function getRelations(): array
    {
        return [
            //
        ];
    }

    public static function getPages(): array
    {
        return [
            'index' => Pages\ListSchedules::route('/'),
            'create' => Pages\CreateSchedule::route('/create'),
            'view' => Pages\ViewSchedule::route('/{record}'),
            'edit' => Pages\EditSchedule::route('/{record}/edit'),
        ];
    }
}

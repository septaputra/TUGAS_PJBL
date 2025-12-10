<?php

namespace App\Filament\Resources\TeacherResource\Pages;

use App\Filament\Resources\TeacherResource;
use App\Imports\TeacherImport;
use Filament\Actions;
use Filament\Forms\Components\FileUpload;
use Filament\Notifications\Notification;
use Filament\Resources\Pages\ListRecords;

class ListTeachers extends ListRecords
{
    protected static string $resource = TeacherResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\CreateAction::make(),
            Actions\Action::make('import')
                ->label('Import Teachers')
                ->icon('heroicon-o-arrow-down-tray')
                ->form([
                    FileUpload::make('upload')
                        ->label('Spreadsheet File')
                        ->acceptedFileTypes([
                            'text/csv',
                            'text/plain',
                            'application/vnd.ms-excel',
                            'application/csv',
                            'application/x-csv',
                            'text/x-csv',
                            'text/comma-separated-values',
                            'text/x-comma-separated-values',
                            'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
                        ])
                        ->required()
                        ->disk('local')
                        ->directory('imports')
                        ->preserveFilenames()
                        ->helperText('Upload CSV (UTF-8) or XLSX file with columns: name, email, mata_pelajaran, is_banned, password (optional)')
                ])
                ->action(function (array $data) {
                    try {
                        // Get the uploaded file path from the data array
                        $filePath = $data['upload'];

                        // Check if file path exists
                        if (!$filePath) {
                            throw new \Exception('Invalid file upload. Please make sure you\'ve selected a valid file.');
                        }

                        // Try multiple possible paths
                        $possiblePaths = [
                            storage_path('app/' . $filePath),
                            storage_path('app/private/' . $filePath),
                            storage_path('app/public/' . $filePath),
                            storage_path('app/livewire-tmp/' . $filePath),
                            storage_path('app/filament-tmp/' . $filePath),
                            public_path('storage/' . $filePath),
                        ];

                        $fullPath = null;
                        foreach ($possiblePaths as $path) {
                            if (file_exists($path)) {
                                $fullPath = $path;
                                break;
                            }
                        }

                        // Check if file exists
                        if (!$fullPath || !file_exists($fullPath)) {
                            throw new \Exception('Uploaded file not found. Path checked: ' . $filePath . '. Please try uploading again.');
                        }

                        // Determine MIME type
                        $mimeType = mime_content_type($fullPath);
                        if (!$mimeType) {
                            // Fallback based on extension
                            $extension = strtolower(pathinfo($fullPath, PATHINFO_EXTENSION));
                            $mimeType = $extension === 'csv' ? 'text/csv' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
                        }

                        // Create an UploadedFile instance from the stored file
                        $uploadedFile = new \Illuminate\Http\UploadedFile(
                            $fullPath,
                            basename($filePath),
                            $mimeType,
                            null,
                            true
                        );

                        // Import the data
                        $importer = new TeacherImport();
                        $result = $importer->import($uploadedFile);

                        if (count($result['errors']) > 0) {
                            // Format error details
                            $errorDetails = [];
                            foreach ($result['errors'] as $error) {
                                if (isset($error['line'])) {
                                    $lineErrors = is_array($error['errors']) ? implode(', ', array_map(function($field, $messages) {
                                        return "$field: " . (is_array($messages) ? implode('; ', $messages) : $messages);
                                    }, array_keys($error['errors']), $error['errors'])) : json_encode($error['errors']);

                                    $errorDetails[] = "Line {$error['line']}: {$lineErrors}";
                                } elseif (isset($error['message'])) {
                                    $errorDetails[] = $error['message'];
                                }
                            }

                            // Limit to first 5 errors for display
                            $displayErrors = array_slice($errorDetails, 0, 5);
                            $remainingErrors = count($errorDetails) - 5;

                            $errorMessage = "Imported {$result['imported']} teachers. Skipped {$result['skipped']} rows.\n\n";
                            $errorMessage .= "Errors:\n" . implode("\n", $displayErrors);

                            if ($remainingErrors > 0) {
                                $errorMessage .= "\n\n... and {$remainingErrors} more errors.";
                            }

                            // Log full errors to Laravel log
                            \Log::warning('Teacher import completed with errors', [
                                'imported' => $result['imported'],
                                'skipped' => $result['skipped'],
                                'errors' => $result['errors']
                            ]);

                            Notification::make()
                                ->title('Import completed with errors')
                                ->body($errorMessage)
                                ->warning()
                                ->duration(10000) // 10 seconds
                                ->send();
                        } else {
                            Notification::make()
                                ->title('Teachers imported successfully')
                                ->body("Imported {$result['imported']} teachers.")
                                ->success()
                                ->send();
                        }
                    } catch (\Exception $e) {
                        Notification::make()
                            ->title('Import failed')
                            ->body($e->getMessage())
                            ->danger()
                            ->send();
                    }
                }),
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

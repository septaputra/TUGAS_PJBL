<?php

namespace App\Imports;

use App\Models\Teacher;
use App\Utils\SpreadsheetReader;
use Illuminate\Http\UploadedFile;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;

class TeacherImport
{
    /**
     * Import teachers from CSV or XLSX file
     *
     * @param UploadedFile $file
     * @return array
     */
    public function import(UploadedFile $file): array
    {
        $results = [
            'imported' => 0,
            'skipped' => 0,
            'errors' => []
        ];

        try {
            // Define validation rules for teachers
            $rules = [
                'name' => 'required|string|max:255',
                'email' => 'required|email|unique:teachers,email',
                'password' => 'nullable|string|min:8',
                'mata_pelajaran' => 'nullable|string|max:255',
                'is_banned' => 'nullable|boolean',
            ];

            // Parse spreadsheet file (CSV or XLSX)
            $rows = SpreadsheetReader::parse($file->getPathname(), $rules);

            foreach ($rows as $index => $row) {
                // Check for validation errors
                if (isset($row['_errors'])) {
                    $results['errors'][] = [
                        'line' => $row['_line_number'] ?? ($index + 2), // +2 because of header and 0-based index
                        'errors' => $row['_errors'],
                        'data' => $row
                    ];
                    $results['skipped']++;
                    continue;
                }

                // Prepare data for insertion
                $data = [
                    'name' => $row['name'] ?? '',
                    'email' => $row['email'] ?? '',
                    'password' => isset($row['password']) ? Hash::make($row['password']) : Hash::make('password'),
                    'mata_pelajaran' => $row['mata_pelajaran'] ?? null,
                    'is_banned' => isset($row['is_banned']) ? (bool)$row['is_banned'] : false,
                ];

                // Update or create teacher
                Teacher::updateOrCreate(
                    ['email' => $data['email']],
                    $data
                );

                $results['imported']++;
            }
        } catch (\Exception $e) {
            $results['errors'][] = [
                'message' => $e->getMessage(),
                'line' => $e->getLine(),
                'file' => $e->getFile()
            ];
        }

        return $results;
    }
}
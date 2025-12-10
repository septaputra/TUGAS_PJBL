<?php

namespace App\Imports;

use App\Models\Subject;
use App\Utils\SpreadsheetReader;
use Illuminate\Http\UploadedFile;
use Illuminate\Support\Facades\Validator;

class SubjectImport
{
    /**
     * Import subjects from CSV or XLSX file
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
            // Define validation rules for subjects
            $rules = [
                'nama' => 'required|string|max:255',
                'kode' => 'required|string|max:255|unique:subjects,kode',
            ];

            // Parse spreadsheet file (CSV or XLSX)
            $rows = SpreadsheetReader::parse($file->getPathname(), $rules);

            foreach ($rows as $index => $row) {
                // Check for validation errors
                if (isset($row['_errors'])) {
                    $results['errors'][] = [
                        'line' => $row['_line_number'] ?? ($index + 2),
                        'errors' => $row['_errors'],
                        'data' => $row
                    ];
                    $results['skipped']++;
                    continue;
                }

                // Prepare data for insertion
                $data = [
                    'nama' => $row['nama'] ?? '',
                    'kode' => $row['kode'] ?? '',
                ];

                // Update or create subject
                Subject::updateOrCreate(
                    ['kode' => $data['kode']],
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
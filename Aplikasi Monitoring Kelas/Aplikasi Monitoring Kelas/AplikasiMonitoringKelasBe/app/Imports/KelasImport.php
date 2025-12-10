<?php

namespace App\Imports;

use App\Models\Kelas;
use App\Utils\SpreadsheetReader;
use Illuminate\Http\UploadedFile;
use Illuminate\Support\Facades\Validator;

class KelasImport
{
    /**
     * Import classes from CSV or XLSX file
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
            // Define validation rules for classes
            $rules = [
                'nama_kelas' => 'required|string|max:255',
                'kode_kelas' => 'required|string|max:255|unique:classes,kode_kelas',
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
                    'nama_kelas' => $row['nama_kelas'] ?? '',
                    'kode_kelas' => $row['kode_kelas'] ?? '',
                ];

                // Update or create class
                Kelas::updateOrCreate(
                    ['kode_kelas' => $data['kode_kelas']],
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
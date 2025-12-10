<?php

namespace App\Utils;

use Exception;
use Illuminate\Support\Arr;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Str;

class CsvReader
{
    /**
     * Parse a CSV file and return an array of rows
     *
     * @param string $filePath
     * @param array $rules
     * @return array
     */
    public static function parse(string $filePath, array $rules = []): array
    {
        if (!file_exists($filePath)) {
            throw new Exception('File not found: ' . $filePath);
        }

        // Read file content and detect encoding
        $content = file_get_contents($filePath);

        // Remove UTF-8 BOM if present
        if (substr($content, 0, 3) === "\xEF\xBB\xBF") {
            $content = substr($content, 3);
        }

        // Detect encoding (UTF-8, UTF-16, etc.)
        $encoding = mb_detect_encoding($content, ['UTF-8', 'UTF-16', 'UTF-16LE', 'UTF-16BE', 'ISO-8859-1', 'Windows-1252'], true);

        // Convert to UTF-8 if needed
        if ($encoding && $encoding !== 'UTF-8') {
            $content = mb_convert_encoding($content, 'UTF-8', $encoding);
        }

        // Save cleaned content to temp file
        $tempFile = tempnam(sys_get_temp_dir(), 'csv_');
        file_put_contents($tempFile, $content);
        $filePath = $tempFile;

        $file = fopen($filePath, 'r');
        if (!$file) {
            throw new Exception('Unable to open file: ' . $filePath);
        }

        // Detect delimiter by reading first line
        $firstLine = fgets($file);
        rewind($file);

        // Count occurrences of common delimiters
        $delimiters = [',', ';', "\t", '|'];
        $delimiterCounts = [];

        foreach ($delimiters as $delimiter) {
            $delimiterCounts[$delimiter] = substr_count($firstLine, $delimiter);
        }

        // Use the delimiter with the highest count
        arsort($delimiterCounts);
        $detectedDelimiter = array_key_first($delimiterCounts);

        // Read the header row with detected delimiter
        $header = fgetcsv($file, 0, $detectedDelimiter);
        if (!$header) {
            fclose($file);
            throw new Exception('Empty CSV file or unable to read header');
        }

        // Clean BOM (Byte Order Mark) if present in first column
        if (isset($header[0])) {
            // Remove UTF-8 BOM (both Unicode and byte representations)
            $header[0] = preg_replace('/^\x{FEFF}/u', '', $header[0]);
            $header[0] = preg_replace('/^' . pack('H*', 'EFBBBF') . '/', '', $header[0]);
            // Also try removing it as literal characters
            $header[0] = ltrim($header[0], "\xEF\xBB\xBF");
        }

        // Normalize header names to match Laravel conventions
        $normalizedHeader = array_map(function ($column) {
            // First, clean any remaining BOM
            $column = ltrim($column, "\xEF\xBB\xBF");
            $column = preg_replace('/^\x{FEFF}/u', '', $column);

            // Convert to lowercase first
            $column = Str::lower(trim($column));

            // Replace spaces, dots, and dashes with underscores
            $column = str_replace([' ', '-', '.'], '_', $column);

            // Remove multiple consecutive underscores
            $column = preg_replace('/_+/', '_', $column);

            // Remove leading/trailing underscores
            $column = trim($column, '_');

            return $column;
        }, $header);

        $rows = [];
        $lineNumber = 1; // Header is line 1

        while (($data = fgetcsv($file, 0, $detectedDelimiter)) !== false) {
            $lineNumber++;

            // Skip empty rows
            if (count(array_filter($data)) === 0) {
                continue;
            }

            // Ensure we have enough data columns
            if (count($data) < count($normalizedHeader)) {
                // Pad the data array with empty values
                $data = array_pad($data, count($normalizedHeader), '');
            }

            // Create associative array with header as keys
            $row = array_combine($normalizedHeader, array_slice($data, 0, count($normalizedHeader)));

            // Validate if rules are provided
            if (!empty($rules)) {
                $validator = Validator::make($row, $rules);

                if ($validator->fails()) {
                    // Add validation errors to the row
                    $row['_errors'] = $validator->errors()->toArray();
                    $row['_line_number'] = $lineNumber;
                }
            }

            $rows[] = $row;
        }

        fclose($file);

        // Clean up temp file if created
        if (isset($tempFile) && file_exists($tempFile)) {
            unlink($tempFile);
        }

        return $rows;
    }

    /**
     * Convert associative array keys to match model attributes
     *
     * @param array $data
     * @param array $mapping
     * @return array
     */
    public static function mapColumns(array $data, array $mapping): array
    {
        $mappedData = [];

        foreach ($mapping as $csvColumn => $modelAttribute) {
            // Check if the CSV column exists in data
            if (array_key_exists($csvColumn, $data)) {
                $mappedData[$modelAttribute] = $data[$csvColumn];
            } else {
                // Handle case where CSV column doesn't exist
                $mappedData[$modelAttribute] = null;
            }
        }

        return $mappedData;
    }
}

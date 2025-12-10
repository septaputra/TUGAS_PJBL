<?php

namespace App\Utils;

use Exception;
use Illuminate\Support\Arr;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Str;

class SpreadsheetReader
{
    /**
     * Parse a spreadsheet file (CSV or XLSX) and return an array of rows
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

        // Determine file type by extension
        $extension = strtolower(pathinfo($filePath, PATHINFO_EXTENSION));

        if ($extension === 'csv') {
            return self::parseCsv($filePath, $rules);
        } elseif ($extension === 'xlsx' || $extension === 'xls') {
            try {
                return self::parseXlsx($filePath, $rules);
            } catch (Exception $e) {
                // If XLSX parsing fails, throw a more informative error
                throw new Exception($e->getMessage());
            }
        } else {
            throw new Exception('Unsupported file type: ' . $extension);
        }
    }

    /**
     * Parse a CSV file and return an array of rows
     *
     * @param string $filePath
     * @param array $rules
     * @return array
     */
    protected static function parseCsv(string $filePath, array $rules = []): array
    {
        return CsvReader::parse($filePath, $rules);
    }

    /**
     * Parse an XLSX file and return an array of rows
     *
     * @param string $filePath
     * @param array $rules
     * @return array
     */
    protected static function parseXlsx(string $filePath, array $rules = []): array
    {
        // Check if we can handle XLSX files with PhpOffice\PhpSpreadsheet
        if (class_exists('\\PhpOffice\\PhpSpreadsheet\\IOFactory')) {
            // Use PhpOffice\PhpSpreadsheet to read XLSX files
            $spreadsheet = \PhpOffice\PhpSpreadsheet\IOFactory::load($filePath);
            $worksheet = $spreadsheet->getActiveSheet();
            
            // Convert to array
            $rows = [];
            foreach ($worksheet->getRowIterator() as $rowIndex => $row) {
                $rowData = [];
                $cellIterator = $row->getCellIterator();
                $cellIterator->setIterateOnlyExistingCells(FALSE);
                
                foreach ($cellIterator as $cell) {
                    $rowData[] = $cell->getValue();
                }
                
                // Skip empty rows
                if (count(array_filter($rowData)) === 0) {
                    continue;
                }
                
                $rows[] = $rowData;
            }
            
            // If we have rows, convert to associative array like CSV parser
            if (count($rows) > 0) {
                $header = array_shift($rows);
                if (!$header) {
                    throw new Exception('Empty spreadsheet or unable to read header');
                }
                
                // Normalize header names to match Laravel conventions
                $normalizedHeader = array_map(function ($column) {
                    return Str::snake(Str::lower(str_replace([' ', '-', '.', '_'], '_', $column)));
                }, $header);
                
                $assocRows = [];
                foreach ($rows as $row) {
                    // Pad row if necessary
                    if (count($row) < count($normalizedHeader)) {
                        $row = array_pad($row, count($normalizedHeader), '');
                    }
                    
                    // Create associative array with header as keys
                    $assocRow = array_combine($normalizedHeader, array_slice($row, 0, count($normalizedHeader)));
                    
                    // Validate if rules are provided
                    if (!empty($rules)) {
                        $validator = Validator::make($assocRow, $rules);
                        
                        if ($validator->fails()) {
                            // Add validation errors to the row
                            $assocRow['_errors'] = $validator->errors()->toArray();
                        }
                    }
                    
                    $assocRows[] = $assocRow;
                }
                
                return $assocRows;
            }
            
            return [];
        } else {
            // Try to use our simple XLSX reader
            try {
                return \App\Utils\SimpleXlsxReader::parse($filePath);
            } catch (Exception $e) {
                // If our simple reader fails, throw a more informative exception
                throw new Exception('XLSX support requires PhpOffice\PhpSpreadsheet package. Please install it with: composer require phpoffice/phpspreadsheet. Alternatively, convert your file to CSV format.');
            }
        }
    }
}
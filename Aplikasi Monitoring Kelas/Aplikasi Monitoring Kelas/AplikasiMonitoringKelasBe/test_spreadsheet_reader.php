<?php

require_once 'vendor/autoload.php';

use App\Utils\SpreadsheetReader;

// Test with CSV file
try {
    $csvFile = 'test_classes.csv';
    $rules = [
        'nama_kelas' => 'required|string|max:255',
        'kode_kelas' => 'required|string|max:255|unique:classes,kode_kelas',
    ];
    
    $rows = SpreadsheetReader::parse($csvFile, $rules);
    
    echo "CSV Parsing Results:\n";
    echo "==================\n";
    foreach ($rows as $index => $row) {
        echo "Row " . ($index + 1) . ": ";
        if (isset($row['_errors'])) {
            echo "ERRORS: " . json_encode($row['_errors']) . "\n";
        } else {
            echo "nama_kelas: " . $row['nama_kelas'] . ", kode_kelas: " . $row['kode_kelas'] . "\n";
        }
    }
} catch (Exception $e) {
    echo "Error parsing CSV: " . $e->getMessage() . "\n";
}

echo "\n";

// Test with XLSX file (this should show an error message)
try {
    $xlsxFile = 'test_classes.xlsx';
    $rules = [
        'nama_kelas' => 'required|string|max:255',
        'kode_kelas' => 'required|string|max:255|unique:classes,kode_kelas',
    ];
    
    $rows = SpreadsheetReader::parse($xlsxFile, $rules);
    
    echo "XLSX Parsing Results:\n";
    echo "====================\n";
    foreach ($rows as $index => $row) {
        echo "Row " . ($index + 1) . ": ";
        if (isset($row['_errors'])) {
            echo "ERRORS: " . json_encode($row['_errors']) . "\n";
        } else {
            echo "nama_kelas: " . $row['nama_kelas'] . ", kode_kelas: " . $row['kode_kelas'] . "\n";
        }
    }
} catch (Exception $e) {
    echo "Expected error for XLSX (since we don't have PhpOffice\PhpSpreadsheet installed): " . $e->getMessage() . "\n";
}
# XLSX Import Support

This application supports importing data from both CSV and XLSX files. While CSV support is built-in, XLSX support requires the PhpOffice\PhpSpreadsheet package.

## Current Status

- CSV import: Fully functional
- XLSX import: Partial support with clear error messages when dependencies are missing

## Enabling Full XLSX Support

To enable full XLSX import functionality, you need to install the PhpOffice\PhpSpreadsheet package:

```bash
composer require phpoffice/phpspreadsheet
```

If you encounter installation issues, you may need to:

1. Install the GD extension for PHP:
   ```bash
   # For Ubuntu/Debian
   sudo apt-get install php-gd
   
   # For CentOS/RHEL
   sudo yum install php-gd
   
   # For Windows, uncomment the following line in php.ini:
   ;extension=gd
   ```

2. Ensure you're using a compatible PHP version (PhpSpreadsheet requires PHP 7.2 or higher)

## Alternative Approach

If you cannot install PhpOffice\PhpSpreadsheet, you can still import XLSX files by converting them to CSV format first:

1. Open your XLSX file in Excel, LibreOffice Calc, or Google Sheets
2. Save/Export as CSV format
3. Import the CSV file using the existing import functionality

## Supported File Types

The import functionality now accepts the following file types:
- `.csv` - Comma-separated values
- `.txt` - Plain text (CSV format)
- `.xls` - Excel 97-2003 format
- `.xlsx` - Excel 2007+ format

Note: For XLS files (.xls), full support also requires PhpOffice\PhpSpreadsheet.
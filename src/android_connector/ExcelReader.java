/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android_connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
/**
 * Verwendet das POI-Set von Apache, zum Download unter
 * http://poi.apache.org/download.html
 */
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

/**
 * Klasse, die Excel-Dateien (XLS oder XLSX) einlesen kann.
 *
 * @author Quelle:
 * http://animet-blog.blogspot.de/2013/08/java-excel-dokumente-auslesen-apache-poi.html
 */
public class ExcelReader {

    /**
     * Repräsentation der gesamten Datei
     */
    Workbook wb;
    /**
     * gewünschte Tabelle
     */
    Sheet sheet;
    /**
     * Version der Excel-Datei (97 oder 2007)
     */
    SpreadsheetVersion version;

    /**
     * Konstruktor der Klasse. Lädt eine Excel-Datei mit dem übergebenen Pfad
     * und speichert diese zur weiteren Verarbeitung.
     *
     * @param pathToExcelFile Pfad zur Excel-Datei
     * @throws IOException Exception, wenn Datei nicht gelesen werden kann
     */
    public ExcelReader(String pathToExcelFile) throws IOException {
        
        //this.sheet = wb.getSheet(sheetName);
        if (pathToExcelFile.endsWith("xls")) {
        FileInputStream fileInput = new FileInputStream(new File(pathToExcelFile));
        this.wb = new HSSFWorkbook(fileInput); //oder XSSFWorkbook(fileInput);
            version = SpreadsheetVersion.EXCEL97;
        } else {
            FileInputStream fileInput = new FileInputStream(new File(pathToExcelFile));
        this.wb = new HSSFWorkbook(fileInput);
            version = SpreadsheetVersion.EXCEL2007;
        }
    }
    /**
     * Gibt die Datei zurück.
     * @return Datei
     */
    public Workbook getWorkBook(){
        return wb;
    }
    /**
     * Lädt die Tabelle mit der Nummer zur Auswertung.
     * @param number Zahl der Tabelle
     */
    public void setSheet(int number){
        sheet = wb.getSheetAt(number);
    }
    //In einem Exceldokument können mehrere Zellen einen Namen haben
    /**
     * Gibt den Wert einer Zelle zurück.
     * @param cellName Name der Zelle
     * @return alle Zellen dieses Namens
     */
    public String[] getCellValue(String cellName) {
        Name cellsName = wb.getName(cellName);
        AreaReference areaRef = new AreaReference(cellsName.getRefersToFormula(), version);
        CellReference[] cellRef = areaRef.getAllReferencedCells();
        String[] returnValue = new String[cellRef.length];
        for (int i = 0; i < cellRef.length; i++) {
            Row row = this.sheet.getRow(cellRef[i].getRow());
            Cell cell = row.getCell(cellRef[i].getCol());
            returnValue[i] = differCellType(cell);
        }

        return returnValue;
    }
    /**
     * Gibt den Wert einer Zelle an einer bestimmten Zelle zurück.
     * @param rowIndex Zeile der Zelle
     * @param columnIndex SPalte der Zelle
     * @return Wert der Zelle
     */
    public String getCellValueAt(int rowIndex, int columnIndex) {
        Row row = this.sheet.getRow(rowIndex);
        Cell cell = row.getCell(columnIndex);
        return differCellType(cell);
    }
    /**
     * Gibt eine vernünftige Darstellung einer Zelle als String zurück.
     * @param cell die Zelle
     * @return z.B. bei Zelle, die eine Gleichung enthält, deren Ergebnis
     */
    private String differCellType(Cell cell) {
        String returnValue = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                returnValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                returnValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING:
                returnValue = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                FormulaEvaluator evaluator = this.wb.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);
                returnValue = cellValue.getStringValue();
                break;
            case Cell.CELL_TYPE_ERROR:
                returnValue = String.valueOf(cell.getErrorCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                returnValue = "";
                break;
            default:
                returnValue = "default value at (" + cell.getRowIndex() + ";" + cell.getColumnIndex() + ") !";
                break;
        }
        return returnValue;
    }
    /**
     * Gibt die Tabelle zurück.
     * @return Tabelle
     */
    public Sheet getSheet() {
        return this.sheet;
    }
    /**
     * Überschreibt die Tabelle
     * @param sheet die neue Tabelle
     */
    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }
}

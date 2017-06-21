package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by liuhang on 2017/6/8.
 * 处理.xlsx文件读取，写入
 */
public class XlsxFileUtil {
    public static void readXLSXFile(String filePath, int sheetIndex) throws IOException {
        InputStream excelFileToRead = new FileInputStream(filePath);

        XSSFWorkbook workbook = new XSSFWorkbook(excelFileToRead);//工作簿
        XSSFSheet sheet = workbook.getSheetAt(sheetIndex);//表
        XSSFRow row;//行
        XSSFCell cell;//单元格

        Iterator rowIterator = sheet.rowIterator();//行迭代器

        while (rowIterator.hasNext()) {
            row = (XSSFRow) rowIterator.next();

            Iterator cellIterator = row.cellIterator();//单元格迭代器
            while (cellIterator.hasNext()) {
                cell = (XSSFCell) cellIterator.next();

                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                    System.out.print(cell.getStringCellValue() + " ");
                } else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                    System.out.print(cell.getNumericCellValue() + " ");
                } else {
                    //U Can Handel Boolean, Formula, Errors
                }
            }
            System.out.println();
        }
    }


    public static void writeXLSXFile(String filePath, String sheetName) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);

        //iterating r number of rows
        for (int r = 0; r < 5; r++) {
            XSSFRow row = sheet.createRow(r);

            //iterating c number of columns
            for (int c = 0; c < 5; c++) {
                XSSFCell cell = row.createCell(c);

                cell.setCellValue("Cell " + r + " " + c);
            }
        }

        FileOutputStream fileOut = new FileOutputStream(filePath);

        //write this workbook to an Outputstream.
        workbook.write(fileOut);
        fileOut.flush();
        fileOut.close();
    }

    public static void main(String[] args) throws IOException {


        writeXLSXFile("/Users/liuhang/Desktop/zte-final/data.xlsx","1");
        readXLSXFile("/Users/liuhang/Desktop/zte-final/data.xlsx", 0);

    }

}

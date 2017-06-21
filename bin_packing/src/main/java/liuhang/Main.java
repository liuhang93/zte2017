package liuhang;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liuhang on 2017/6/10.
 */
public class Main {
    public static List<Item> in = new ArrayList<>();
    public static List<Integer> attribute1 = new ArrayList<>();
    public static List<Integer> attribute2 = new ArrayList<>();
    public static Item binSize;

    public static void main(String[] args) throws IOException {
        readXLSXFile("/Users/liuhang/Desktop/ztef/决赛验证数据集.xlsx", 3);
        binSize = in.get(0);
        in.remove(0);
        attribute1.remove(0);
        attribute2.remove(0);

        List<Item> inFFD = new ArrayList<>();
        inFFD.addAll(in);
        FirstFitDecreasing ffd = new FirstFitDecreasing(inFFD, binSize);
        ffd.executeFFD(1);
        ffd.printBestBins();

        List<Item> bbItems = new ArrayList<>();
        bbItems.addAll(in);
        BranchAndBound branchAndBound = new BranchAndBound(bbItems, binSize);
        branchAndBound.executeBranchBound();
        branchAndBound.printBestBins();

//        List<Item> branchBound = new ArrayList<>();
//        branchBound.addAll(in);
//        BranchBound bb = new BranchBound(branchBound, binSize);
//        bb.executeBranchBound();
//        bb.printBestBins();

////        FirstFitDecreasing ffd = new FirstFitDecreasing(in, binSize);
//        List<Item> inHeuristic = new ArrayList<>();
//        inHeuristic.addAll(in);

//        Heuristic heuristic = new Heuristic(inHeuristic, binSize);
//        heuristic.executeHeuristic();
////        int res = ffd.executeFFD();
//        heuristic.printBestBins();
////        System.out.println(res);
//        List<Item> inFFD = new ArrayList<>();
//        inFFD.addAll(in);
//        FirstFitDecreasing ffd = new FirstFitDecreasing(inFFD, binSize);
//        ffd.executeFFD(1);
//        ffd.printBestBins();
//
//        FirstFitDecreasing ffd1 = new FirstFitDecreasing(inFFD, binSize);
//        ffd1.executeFFD(-1);
//        ffd1.printBestBins();


    }

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
            int[] a = new int[2];
            int index = 0;
            while (cellIterator.hasNext()) {
                cell = (XSSFCell) cellIterator.next();

                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                    System.out.print(cell.getStringCellValue() + " ");
                } else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                    a[index] = (int) cell.getNumericCellValue();
//                    System.out.print(cell.getNumericCellValue() + " ");
                } else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
                    //U Can Handel Boolean, Formula, Errors
                    a[index] = (int) cell.getNumericCellValue();
                }
                index++;
            }
//            System.out.println();
            in.add(new Item(a[0], a[1]));
            attribute1.add(a[0]);
            attribute2.add(a[1]);
        }
    }
}

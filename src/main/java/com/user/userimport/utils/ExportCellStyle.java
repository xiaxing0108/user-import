package com.user.userimport.utils;

import org.apache.poi.ss.usermodel.*;

public class ExportCellStyle {

    /**
     * 垂直水平居中
     * @param workbook
     * @return
     */
    public static CellStyle center(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    /**
     * 右对齐
     * @param workbook
     * @return
     */
    public static CellStyle alignRight(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        return cellStyle;
    }

    /**
     * 右对齐填充颜色
     * @param workbook
     * @return
     */
    public static CellStyle alignRightAndColor(Workbook workbook,short color) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setFillForegroundColor(color);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    /**
     * 居中填充颜色
     * @param workbook
     * @return
     */
    public static CellStyle centerAndColor(Workbook workbook,short color) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFillForegroundColor(color);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }


    /**
     * 填充颜色
     * @param workbook
     * @Param short IndexedColors.RED.getIndex()
     * @return
     */
    public static CellStyle colour(Workbook workbook,short colour) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(colour);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    /**
     * 单元格边框
     * @param workbook
     * @return
     */
    public static CellStyle borderThin(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        return cellStyle;
    }

    /**
     * 居中加粗
     * @param workbook
     * @return
     */
    public static CellStyle centerAndBold(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);
        return cellStyle;
    }
}

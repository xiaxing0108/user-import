package com.user.userimport.service;

import com.user.userimport.dao.ExportDao;
import com.user.userimport.enums.ExcelTypeEnum;
import com.user.userimport.pojo.MainExcelData;
import com.user.userimport.utils.ExportCellStyle;
import constant.ExportConstant;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 报表统计类
 */
@Service
public class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    private static final float RED_PERCENT = 25.00f;

    private static final float YELLOW_PERCENT = 40.00f;

    @Autowired
    private ExportDao exportDao;

    public Workbook export(String queryDate,Integer type) {

        logger.info("开始统计");
        XSSFWorkbook workbook = new XSSFWorkbook();
        //获取站点数据
        List<Map<String, Object>> airPort = exportDao.getAirPort();
        Map<String,String> airPortMap = new HashMap<>();
        for (Map<String, Object> map : airPort) {
            airPortMap.put(((String)map.get("airPortCode")).trim(),((String)map.get("airPortCodeNote")).trim());
        }
        //各种单元格样式
        Map<String,CellStyle> cellStyleMap = new HashMap<>();
        CellStyle center = ExportCellStyle.center(workbook);
        CellStyle alignRight = ExportCellStyle.alignRight(workbook);
        CellStyle yellow = ExportCellStyle.colour(workbook,IndexedColors.YELLOW.getIndex());
        cellStyleMap.put("center",center);
        cellStyleMap.put("alignRight",alignRight);
        cellStyleMap.put("yellow",yellow);
        cellStyleMap.put("borderThin",ExportCellStyle.borderThin(workbook));
        cellStyleMap.put("blue",ExportCellStyle.colour(workbook,IndexedColors.SKY_BLUE.getIndex()));
        cellStyleMap.put("alignRightAndYellow",ExportCellStyle.alignRightAndColor(workbook,IndexedColors.YELLOW.getIndex()));
        cellStyleMap.put("centerAndBold",ExportCellStyle.centerAndBold(workbook));
        cellStyleMap.put("centerAndRed",ExportCellStyle.centerAndColor(workbook,IndexedColors.RED.getIndex()));

        //if(type== ExcelTypeEnum.QU_ALL.getCode()) {
            buildQuXccxSheet(workbook,cellStyleMap,queryDate,"QU","去哪儿总表",airPortMap);
        //}
        //if(type==ExcelTypeEnum.XCCX_ALL.getCode()) {
            buildQuXccxSheet(workbook,cellStyleMap,queryDate,"XCCX","携程出行总表",airPortMap);
        //}
        //if(type==ExcelTypeEnum.TC_ALL.getCode()){
            buildTcSheet(workbook,cellStyleMap,queryDate,airPortMap);
        //}
        //if(type==ExcelTypeEnum.UNCHECK_DETAIL.getCode()) {
            buildUncheck(workbook,cellStyleMap,queryDate);
        //}
        //if(type==ExcelTypeEnum.QU_PST.getCode()) {
            buildPST(workbook,cellStyleMap,queryDate,"QU","去哪儿PST",airPortMap);
        //}
        //if(type==ExcelTypeEnum.TC_PST.getCode()) {
            buildPST(workbook,cellStyleMap,queryDate,"TCYL","同程PST",airPortMap);
        //}
        //if(type==ExcelTypeEnum.XCCX_PST.getCode()) {
            buildPST(workbook,cellStyleMap,queryDate,"XCCX","携程出行PST",airPortMap);
        //}
        //if(type==ExcelTypeEnum.XC_PST.getCode()) {
            buildPST(workbook,cellStyleMap,queryDate,"XC","携程PST",airPortMap);
        //}

        logger.info("导出统计表格成功");

        return workbook;
    }

    /**
     * 去哪儿，携程出行总表总表
     * @param workbook 表格主体
     * @param cellStyleMap 单元格样式
     * @param queryDate 统计日期
     */
    private void buildQuXccxSheet(XSSFWorkbook workbook,Map<String,CellStyle> cellStyleMap,String queryDate,String supplyCode,String sheetName,Map<String,String> airPortMap) {
        XSSFSheet quSheet = workbook.createSheet(sheetName);

        //构建动态表头
        Row head = quSheet.createRow(0);
        Row second = quSheet.createRow(1);
        head.createCell(0).setCellValue("日期");
        //这里根据始发机场合并单元格，每四个单元格合并一个
        int startCol = 1,endCol = 4;
        //Map<String,String> airPortMap = ExportConstant.airPortMap;
        for(String k :airPortMap.keySet()) {
            Cell sign = second.createCell(startCol);
            quSheet.setColumnWidth(startCol,5*256);
            quSheet.setColumnWidth(startCol+1,8*256);
            quSheet.setColumnWidth(startCol+2,10*256);
            quSheet.setColumnWidth(startCol+3,8*256);
            CellStyle headStyle = ExportCellStyle.center(workbook);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short)8);
            headStyle.setFont(font);
            sign.setCellStyle(headStyle);
            sign.setCellValue("总量");
            sign = second.createCell(startCol+1);
            sign.setCellStyle(headStyle);
            sign.setCellValue("已核销");
            sign = second.createCell(startCol+2);
            sign.setCellStyle(headStyle);
            sign.setCellValue("未核销");
            sign = second.createCell(startCol+3);
            sign.setCellStyle(headStyle);
            sign.setCellValue("日使用率");
            Cell airPortName = head.createCell(startCol);
            airPortName.setCellStyle(cellStyleMap.get("center"));
            airPortName.setCellValue(airPortMap.get(k));
            CellRangeAddress region = new CellRangeAddress(0,0,startCol,endCol);
            quSheet.addMergedRegion(region);
            startCol+=4;
            endCol+=4;
        }
        Cell back = head.createCell(startCol);
        back.setCellStyle(cellStyleMap.get("center"));
        back.setCellValue("日总量");
        back = head.createCell(startCol+1);
        back.setCellStyle(cellStyleMap.get("center"));
        back.setCellValue("日核销");
        back = head.createCell(startCol+2);
        back.setCellStyle(cellStyleMap.get("center"));
        back.setCellValue("日未核销");
        back = head.createCell(startCol+3);
        back.setCellStyle(cellStyleMap.get("center"));
        back.setCellValue("日完成量");
        back = head.createCell(startCol+4);
        back.setCellStyle(cellStyleMap.get("center"));
        back.setCellValue("日完成率");

        //查询数据库，获取每天的数据
        List<Map<String,List>> dataList = new ArrayList<>();
        for(String k: airPortMap.keySet()) {
            //总量
            List<String> total = exportDao.getCensusData(queryDate, supplyCode, k, null,null);
            //已核销
            List<String> checkSuccess = exportDao.getCensusData(queryDate, supplyCode, k, 1,3);
            //未核销
            List<String> checkFail = exportDao.getCensusData(queryDate, supplyCode, k, 0,3);
            Map<String,List> map = new HashMap<>();
            map.put("total",listToMapList(total));
            map.put("checkSuccess",listToMapList(checkSuccess));
            map.put("checkFail",listToMapList(checkFail));
            dataList.add(map);
        }

        //数据部分
        List<String> dateList = getDateList(queryDate);
        int rowNum = 2;
        for (int i = 0; i < dateList.size(); i++) {
            Row dataRow = quSheet.createRow(rowNum);
            String thisTime = dateList.get(i);
            dataRow.createCell(0).setCellValue(thisTime);//行头
            startCol = 1;
            Integer dayTotal = 0;
            Integer dayCheckSuccess = 0;
            Integer dayCheckFail = 0;
            for (Map<String, List> listMap : dataList) {
                List<Map<String, Object>> total = listMap.get("total");
                List<Map<String, Object>> checkSuccess = listMap.get("checkSuccess");
                List<Map<String, Object>> checkFail = listMap.get("checkFail");
                Integer totalNumber = 0,checkSuccessNumber = 0,checkFailNumber = 0;
                Optional<Map<String, Object>> totalNumberMap = total.stream().filter(m -> thisTime.equals(m.get("addTime")))
                        .findAny();
                Optional<Map<String, Object>> checkSuccessNumberMap = checkSuccess.stream().filter(m -> thisTime.equals(m.get("addTime")))
                        .findAny();
                Optional<Map<String, Object>> checkFailNumberMap = checkFail.stream().filter(m -> thisTime.equals(m.get("addTime")))
                        .findAny();
                if(totalNumberMap.isPresent()) {
                    totalNumber = (Integer)totalNumberMap.get().get("total");
                }
                if(checkSuccessNumberMap.isPresent()) {
                    checkSuccessNumber = (Integer)checkSuccessNumberMap.get().get("total");
                }
                if(checkFailNumberMap.isPresent()) {
                    checkFailNumber = (Integer)checkFailNumberMap.get().get("total");
                }
                Cell dataCell = dataRow.createCell(startCol);
                dataCell.setCellStyle(cellStyleMap.get("alignRight"));
                dataCell.setCellValue(totalNumber==0?"":String.valueOf(totalNumber));//总量
                dataCell = dataRow.createCell(startCol+1);
                dataCell.setCellStyle(cellStyleMap.get("alignRight"));
                dataCell.setCellValue(checkSuccessNumber==0?"":String.valueOf(checkSuccessNumber));//核销成功
                dataCell = dataRow.createCell(startCol+2);
                dataCell.setCellStyle(cellStyleMap.get("alignRight"));
                dataCell.setCellValue(checkFailNumber==0?"":String.valueOf(checkFailNumber));//核销未成功
                dataCell = dataRow.createCell(startCol+3);
                CellStyle alignRight = ExportCellStyle.alignRight(workbook);
                Integer son = checkFailNumber+checkSuccessNumber;
                if(totalNumber!=0) {
                    float percent = (float)son/totalNumber*100;
                    if(percent<RED_PERCENT) {
                        alignRight.setFillForegroundColor(IndexedColors.RED.getIndex());
                        alignRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        setBorder(alignRight);
                    }
                    if(percent>YELLOW_PERCENT) {
                        alignRight.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                        alignRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        setBorder(alignRight);
                    }
                    BigDecimal b = new BigDecimal(percent);
                    dataCell.setCellStyle(alignRight);
                    dataCell.setCellValue(b.setScale(2,b.ROUND_DOWN)+"%");
                }
                dayTotal+=totalNumber;
                dayCheckSuccess+=checkSuccessNumber;
                dayCheckFail+=checkFailNumber;
                startCol+=4;
            }
            Cell tailCell = dataRow.createCell(startCol);
            tailCell.setCellStyle(cellStyleMap.get("alignRight"));
            tailCell.setCellValue(dayTotal==0?"":String.valueOf(dayTotal));
            tailCell = dataRow.createCell(startCol+1);
            tailCell.setCellStyle(cellStyleMap.get("alignRight"));
            tailCell.setCellValue(dayCheckSuccess==0?"":String.valueOf(dayCheckSuccess));
            tailCell = dataRow.createCell(startCol+2);
            tailCell.setCellStyle(cellStyleMap.get("alignRight"));
            tailCell.setCellValue(dayCheckFail==0?"":String.valueOf(dayCheckFail));
            tailCell = dataRow.createCell(startCol+3);
            tailCell.setCellStyle(cellStyleMap.get("alignRight"));
            Integer son = dayCheckFail+dayCheckSuccess;
            tailCell.setCellValue(son==0?"":String.valueOf(son));
            tailCell = dataRow.createCell(startCol+4);
            CellStyle alignRight = ExportCellStyle.alignRight(workbook);
            if(dayTotal!=0) {
                float percent = (float)son/dayTotal*100;
                if(percent<RED_PERCENT) {
                    alignRight.setFillForegroundColor(IndexedColors.RED.getIndex());
                    alignRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    setBorder(alignRight);
                }
                if(percent>YELLOW_PERCENT) {
                    alignRight.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                    alignRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    setBorder(alignRight);
                }
                BigDecimal b = new BigDecimal(percent);
                tailCell.setCellStyle(alignRight);
                tailCell.setCellValue(b.setScale(2,b.ROUND_DOWN)+"%");
            }

            rowNum ++;
        }

        //最后统计行
        Row lastRow = quSheet.createRow(rowNum);
        lastRow.createCell(0).setCellValue("小计");
        //最后统计行数据填充
        startCol = 1;
        for (Map<String, List> listMap : dataList) {
            List<Map<String, Object>> total = listMap.get("total");
            List<Map<String, Object>> checkSuccess = listMap.get("checkSuccess");
            List<Map<String, Object>> checkFail = listMap.get("checkFail");
            Integer totalCount = 0,checkSuccessCount = 0,checkFailCount = 0;
            for (Map<String, Object> map : total) {
                totalCount+=(Integer) map.get("total");
            }
            for (Map<String, Object> map : checkSuccess) {
                checkSuccessCount+=(Integer) map.get("total");
            }
            for (Map<String, Object> map : checkFail) {
                checkFailCount+=(Integer) map.get("total");
            }
            Cell cell = lastRow.createCell(startCol);
            cell.setCellStyle(cellStyleMap.get("alignRight"));
            cell.setCellValue(totalCount==0?"":String.valueOf(totalCount));
            cell = lastRow.createCell(startCol+1);
            cell.setCellStyle(cellStyleMap.get("alignRight"));
            cell.setCellValue(checkSuccessCount==0?"":String.valueOf(checkSuccessCount));
            cell = lastRow.createCell(startCol+2);
            cell.setCellStyle(cellStyleMap.get("alignRight"));
            cell.setCellValue(checkFailCount==0?"":String.valueOf(checkFailCount));
            cell = lastRow.createCell(startCol+3);
            CellStyle alignRight = ExportCellStyle.alignRight(workbook);
            Integer son = checkSuccessCount+checkFailCount;
            if(totalCount!=0) {
                float percent = (float)son/totalCount*100;
                if(percent<RED_PERCENT) {
                    alignRight.setFillForegroundColor(IndexedColors.RED.getIndex());
                    alignRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    setBorder(alignRight);
                }
                if(percent>YELLOW_PERCENT) {
                    alignRight.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                    alignRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    setBorder(alignRight);
                }
                BigDecimal b = new BigDecimal(percent);
                cell.setCellStyle(alignRight);
                cell.setCellValue(b.setScale(2,b.ROUND_DOWN)+"%");
            }

            startCol+=4;
        }

        //总的统计
        Integer totalCount = 0,checkSuccessCount = 0,checkFailCount = 0;
        for (Map<String, List> listMap : dataList) {
            List<Map<String, Object>> total = listMap.get("total");
            List<Map<String, Object>> checkSuccess = listMap.get("checkSuccess");
            List<Map<String, Object>> checkFail = listMap.get("checkFail");
            for (Map<String, Object> map : total) {
                totalCount+=(Integer) map.get("total");
            }
            for (Map<String, Object> map : checkSuccess) {
                checkSuccessCount+=(Integer) map.get("total");
            }
            for (Map<String, Object> map : checkFail) {
                checkFailCount+=(Integer) map.get("total");
            }
        }
        Cell lastCell = lastRow.createCell(startCol);
        lastCell.setCellStyle(cellStyleMap.get("alignRight"));
        lastCell.setCellValue(totalCount==0?"":String.valueOf(totalCount));//日总量总计
        lastCell = lastRow.createCell(startCol+1);
        lastCell.setCellStyle(cellStyleMap.get("alignRight"));
        lastCell.setCellValue(checkSuccessCount==0?"":String.valueOf(checkSuccessCount));//日核销成功总计
        lastCell = lastRow.createCell(startCol+2);
        lastCell.setCellStyle(cellStyleMap.get("alignRight"));
        lastCell.setCellValue(checkFailCount==0?"":String.valueOf(checkFailCount));//日未核销总计
        lastCell = lastRow.createCell(startCol+3);
        Integer son = checkFailCount+checkSuccessCount;
        lastCell.setCellStyle(cellStyleMap.get("alignRight"));
        lastCell.setCellValue(son==0?"":String.valueOf(son));//日完成量总计
        lastCell = lastRow.createCell(startCol+4);
        CellStyle alignRight = ExportCellStyle.alignRight(workbook);
        if(totalCount>0) {
            float percent = (float)son/totalCount*100;
            if(percent<RED_PERCENT) {
                alignRight.setFillForegroundColor(IndexedColors.RED.getIndex());
                alignRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                setBorder(alignRight);
            }
            if(percent>YELLOW_PERCENT) {
                alignRight.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                alignRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                setBorder(alignRight);
            }
            BigDecimal b = new BigDecimal(percent);
            lastCell.setCellStyle(alignRight);
            lastCell.setCellValue(b.setScale(2,b.ROUND_DOWN)+"%");
        }


    }

    /**
     * 同程总表
     * @param workbook 表格主体
     * @param cellStyleMap 单元格样式
     * @param queryDate 统计日期
     */
    private void buildTcSheet(XSSFWorkbook workbook,Map<String,CellStyle> cellStyleMap,String queryDate,Map<String,String> airPortMap) {
        List<String> dateList = getDateList(queryDate);
        List<String> formatDateList = formatDate(dateList);
        Sheet tcSheet = workbook.createSheet("同程总表");

        //各种表格样式
        CellStyle centerAndYellow = cellStyleMap.get("center");
        centerAndYellow.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        centerAndYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(centerAndYellow);

        CellStyle alignRightAndYellow = cellStyleMap.get("alignRightAndYellow");
        alignRightAndYellow.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        alignRightAndYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(alignRightAndYellow);

        //Map<String,String> airPortMap = ExportConstant.airPortMapTC;
        //表头
        int rowNum = 1;
        for(String key : airPortMap.keySet()) {
            Row row = tcSheet.createRow(rowNum);
            Cell cell = row.createCell(0);
            cell.setCellStyle(cellStyleMap.get("blue"));
            cell.setCellValue(airPortMap.get(key));
            row = tcSheet.createRow(rowNum+1);
            cell = row.createCell(0);
            cell.setCellValue("派发人数");
            row = tcSheet.createRow(rowNum+2);
            cell = row.createCell(0);
            cell.setCellValue("完成人数");
            row = tcSheet.createRow(rowNum+3);
            row.createCell(0).setCellValue("完成率");
            rowNum += 4;
        }

        Row row = tcSheet.createRow(rowNum+1);
        Cell headCell = row.createCell(0);
        headCell.setCellStyle(centerAndYellow);
        headCell.setCellValue("派发人数");
        row = tcSheet.createRow(rowNum+2);
        headCell = row.createCell(0);
        headCell.setCellStyle(centerAndYellow);
        headCell.setCellValue("完成人数");
        row = tcSheet.createRow(rowNum+3);
        headCell = row.createCell(0);
        headCell.setCellStyle(centerAndYellow);
        headCell.setCellValue("完成率");

        //数据部分
        //查询数据库，获取每天的数据
        List<Map<String,List>> dataList = new ArrayList<>();
        for(String k: airPortMap.keySet()) {
            //派单人数
            List<String> total = exportDao.getCensusData(queryDate, "TCYL", k, null,null);
            //完成人数
            List<String> done = exportDao.getCensusData(queryDate, "TCYL", k, null,3);
            Map<String,List> map = new HashMap<>();
            map.put("total",listToMapList(total));
            map.put("done",listToMapList(done));
            dataList.add(map);
        }
        Row head = tcSheet.createRow(0);
        Cell firstCell = head.createCell(0);
        firstCell.setCellStyle(cellStyleMap.get("yellow"));
        firstCell.setCellValue("航站楼");
        int cellNum = 1;
        for (int i = 0; i < formatDateList.size(); i++) {
            Cell cell = head.createCell(cellNum);
            cell.setCellStyle(centerAndYellow);
            cell.setCellValue(formatDateList.get(i));
            rowNum = 1;
            int allTotal = 0;
            int allDone = 0;
            for (int j = 0; j < dataList.size(); j++) {
                Map<String,List> map = dataList.get(j);
                List<Map<String, Object>> total = map.get("total");
                List<Map<String, Object>> done = map.get("done");
                Row dataRow = tcSheet.getRow(rowNum+1);
                Cell dataCell = dataRow.createCell(cellNum);
                dataCell.setCellStyle(cellStyleMap.get("alignRight"));
                Integer totalCount = 0,doneCount = 0;
                if(total.size()<=0) {
                    dataCell.setCellValue(0);
                }else{
                    for (Map<String, Object> totalMap : total) {
                        String addTime = (String)totalMap.get("addTime");
                        if(addTime.equals(dateList.get(i))) {
                            dataCell.setCellValue((Integer)totalMap.get("total"));
                            totalCount = (Integer)totalMap.get("total");
                            break;
                        }else{
                            dataCell.setCellValue(0);
                        }
                    }
                }

                dataRow = tcSheet.getRow(rowNum+2);
                dataCell = dataRow.createCell(cellNum);
                dataCell.setCellStyle(cellStyleMap.get("alignRight"));
                if(done.size()<=0) {
                    dataCell.setCellValue(0);
                }else{
                    for (Map<String, Object> doneMap : done) {
                        String addTime = (String)doneMap.get("addTime");
                        if(addTime.equals(dateList.get(i))) {
                            dataCell.setCellValue((Integer)doneMap.get("total"));
                            doneCount = (Integer)doneMap.get("total");
                            break;
                        }else{
                            dataCell.setCellValue(0);
                        }
                    }
                }

                dataRow = tcSheet.getRow(rowNum+3);
                dataCell = dataRow.createCell(cellNum);
                dataCell.setCellStyle(cellStyleMap.get("alignRight"));
                if(totalCount>0) {
                    float percent = (float)doneCount/totalCount*100;
                    BigDecimal bigDecimal = new BigDecimal(percent);
                    dataCell.setCellValue(bigDecimal.setScale(2,bigDecimal.ROUND_DOWN)+"%");
                }else{
                    dataCell.setCellValue(0);
                }
                allTotal += totalCount;
                allDone += doneCount;
                rowNum +=4;
            }

            Row lastRow = tcSheet.getRow(rowNum+1);
            Cell lastCell = lastRow.createCell(cellNum);
            lastCell.setCellStyle(alignRightAndYellow);
            lastCell.setCellValue(allTotal);
            lastRow = tcSheet.getRow(rowNum+2);
            lastCell = lastRow.createCell(cellNum);
            lastCell.setCellStyle(alignRightAndYellow);
            lastCell.setCellValue(allDone);
            lastRow = tcSheet.getRow(rowNum+3);
            lastCell = lastRow.createCell(cellNum);
            lastCell.setCellStyle(alignRightAndYellow);
            if(allTotal>0) {
                float percent = (float)allDone/allTotal*100;
                BigDecimal bigDecimal = new BigDecimal(percent);
                lastCell.setCellValue(bigDecimal.setScale(2,bigDecimal.ROUND_DOWN)+"%");
            }else{
                lastCell.setCellValue(0);
            }

            cellNum++;
        }

        Cell lastCell = head.createCell(cellNum);
        lastCell.setCellStyle(centerAndYellow);
        lastCell.setCellValue("汇总");
        rowNum = 1;
        Integer allTotal = 0,allDone = 0;
        for (Map<String, List> lastMap : dataList) {
            List<Map<String, Object>> total = lastMap.get("total");
            List<Map<String, Object>> done = lastMap.get("done");
            int totalCount = 0,doneCount=0;
            for (Map<String, Object> totalMap : total) {
                Integer totalNum = (Integer)totalMap.get("total");
                if(totalNum!=null) {
                    totalCount+=totalNum;
                }
            }
            for (Map<String, Object> doneMap : done) {
                Integer doneNum = (Integer)doneMap.get("total");
                if(doneNum!=null) {
                    doneCount+=doneNum;
                }
            }
            Row lastRow = tcSheet.getRow(rowNum+1);
            Cell lastDataCell = lastRow.createCell(cellNum);
            lastDataCell.setCellStyle(cellStyleMap.get("alignRight"));
            lastDataCell.setCellValue(String.valueOf(totalCount));
            lastRow = tcSheet.getRow(rowNum+2);
            lastDataCell = lastRow.createCell(cellNum);
            lastDataCell.setCellStyle(cellStyleMap.get("alignRight"));
            lastDataCell.setCellValue(String.valueOf(doneCount));
            lastRow = tcSheet.getRow(rowNum+3);
            lastDataCell = lastRow.createCell(cellNum);
            lastDataCell.setCellStyle(cellStyleMap.get("alignRight"));
            if(totalCount>0) {
                float percent = (float)doneCount/totalCount*100;
                BigDecimal bigDecimal = new BigDecimal(percent);
                lastDataCell.setCellValue(bigDecimal.setScale(2,bigDecimal.ROUND_DOWN)+"%");
            }else{
                lastDataCell.setCellValue(0);
            }
            allTotal += totalCount;
            allDone += doneCount;
            rowNum+=4;

        }

        Row finalRow = tcSheet.getRow(rowNum+1);
        Cell finalCell = finalRow.createCell(cellNum);
        finalCell.setCellStyle(alignRightAndYellow);
        finalCell.setCellValue(allTotal);
        finalRow = tcSheet.getRow(rowNum+2);
        finalCell = finalRow.createCell(cellNum);
        finalCell.setCellStyle(alignRightAndYellow);
        finalCell.setCellValue(allDone);
        finalRow = tcSheet.getRow(rowNum+3);
        finalCell = finalRow.createCell(cellNum);
        finalCell.setCellStyle(alignRightAndYellow);
        if(allTotal>0) {
            float percent = (float)allDone/allTotal*100;
            BigDecimal bigDecimal = new BigDecimal(percent);
            finalCell.setCellValue(bigDecimal.setScale(2,bigDecimal.ROUND_DOWN)+"%");
        }else {
            finalCell.setCellValue(0);
        }

    }

    /**
     * 未核销明细表
     * @param workbook
     * @param cellStyleMap
     * @param queryDate
     */
    private void buildUncheck(XSSFWorkbook workbook,Map<String,CellStyle> cellStyleMap,String queryDate) {
        Sheet uncheckSheet = workbook.createSheet("未核销明细");
        //表头
        Row head = uncheckSheet.createRow(0);
        head.createCell(0).setCellValue("日期");
        head.createCell(1).setCellValue("订单号");
        head.createCell(2).setCellValue("航站楼");
        head.createCell(3).setCellValue("姓名");
        head.createCell(4).setCellValue("原因");

        uncheckSheet.setColumnWidth(1,35*256);
        uncheckSheet.setColumnWidth(4,50*256);


        List<Map<String, Object>> list = exportDao.uncheckList(queryDate);
        list.forEach(m->{
            Timestamp timestamp = (Timestamp)m.get("airStartTime");
            if(timestamp!=null) {
                m.put("airStartTime",formatDate(timestamp));
            }
        });

        for (int i = 0; i < list.size(); i++) {
            Map<String,Object> map = list.get(i);
            Row row = uncheckSheet.createRow(i+1);
            row.createCell(0).setCellValue(map.get("airStartTime")==null?"":(String)map.get("airStartTime"));
            row.createCell(1).setCellValue(map.get("orderSaleNo")==null?"":(String)map.get("orderSaleNo"));
            row.createCell(2).setCellValue(map.get("airPortCodeNote")==null?"":(String)map.get("airPortCodeNote"));
            row.createCell(3).setCellValue(map.get("customName")==null?"":(String)map.get("customName"));
            row.createCell(4).setCellValue(map.get("orderMark")==null?"":(String)map.get("orderMark"));
        }


    }

    /**
     * 各个供应商的PST报表
     * @param workbook
     * @param cellStyleMap
     * @param queryDate
     * @param supplierCode 供应商代码
     */
    private void buildPST(XSSFWorkbook workbook,Map<String,CellStyle> cellStyleMap,String queryDate,String supplierCode,String sheetName,Map<String,String> airPortMap){
        Sheet sheet = workbook.createSheet(sheetName);
        List<String> dateList = getDateList(queryDate);
        List<String> formatDateList = formatDateList(dateList);

        //默认设置
        sheet.setDefaultColumnWidth(11*256);
        sheet.setZoom(115);

        //单元格属性
        CellStyle centerAndRed = cellStyleMap.get("centerAndRed");
        setBorder(centerAndRed);
        CellStyle center = ExportCellStyle.center(workbook);

        Row row = sheet.createRow(1);
        row.setHeight((short)400);
        row.createCell(0).setCellValue("日期");

        //日期列
        for (int i = 0; i < formatDateList.size(); i++) {
            String date = formatDateList.get(i);
            row = sheet.createRow(i+2);
            row.setHeight((short)400);
            Cell cell = row.createCell(0);
            cell.setCellStyle(center);
            cell.setCellValue(date);
        }

        //数据部分,一个机场一个机场的处理
        int partCellNum = 1;
        int cellNum = 1;
        int allEmptyList = 0;
        for(String k:airPortMap.keySet()) {
            //机场名称
            Row airportRow = sheet.getRow(0)==null?sheet.createRow(0):sheet.getRow(0);
            airportRow.setHeight((short)450);

            //某个机场的所有数据
            List<Map<String, Object>> list = exportDao.PSTList(queryDate, supplierCode, k);
            if(list==null||list.size()==0) {
                allEmptyList ++;
                if(allEmptyList==airPortMap.size()) {
                    Cell nonCell = airportRow.createCell(0);
                    nonCell.setCellStyle(cellStyleMap.get("centerAndBold"));
                    nonCell.setCellValue("无数据");
                    return;
                }
                continue;
            }else{
                Cell airportCell = airportRow.createCell(partCellNum);
                airportCell.setCellValue(airPortMap.get(k));
                airportCell.setCellStyle(centerAndRed);
            }
            Map<String,Integer> workMap = new HashMap<>();
            for (Map<String, Object> map : list) {
                String worker = (String)map.get("adminRealName");
                String workerDate = formatDateMD((String)map.get("formatTime"));
                Integer total = (Integer) map.get("total");

                //数据
                if(workMap.containsKey(worker)) {
                    for (int i = 0; i < formatDateList.size(); i++) {
                        String date = formatDateList.get(i);
                        if(workerDate.equals(date)) {
                            Row countRow = sheet.getRow(i+2);
                            Cell countCell = countRow.createCell(workMap.get(worker));
                            countCell.setCellStyle(cellStyleMap.get("centerAndBold"));
                            countCell.setCellValue(total);
                        }
                    }
                }else{
                    //工作人员数据
                    Row workerRow = sheet.getRow(1);
                    Cell workerCell = workerRow.createCell(cellNum);
                    workerCell.setCellStyle(cellStyleMap.get("centerAndBold"));
                    workerCell.setCellValue(worker);
                    for (int i = 0; i < formatDateList.size(); i++) {
                        String date = formatDateList.get(i);
                        if(workerDate.equals(date)) {
                            Row countRow = sheet.getRow(i+2);
                            Cell countCell = countRow.createCell(cellNum);
                            countCell.setCellStyle(cellStyleMap.get("centerAndBold"));
                            countCell.setCellValue(total);
                        }
                    }

                    workMap.put(worker,cellNum);
                    cellNum++;
                }

            }
            if(workMap.size()>1) {
                CellRangeAddress region = new CellRangeAddress(0,0,partCellNum,partCellNum+workMap.size()-1);
                sheet.addMergedRegion(region);
            }
            partCellNum += workMap.size();
            workMap.clear();

        }
    }

    /**
     * 格式化单个日期，返回几月几日例如：5月1日
     * @param timestamp
     * @return
     */
    private String formatDate(Timestamp timestamp) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.of("+8"));
        return localDateTime.getMonth().getValue()+"月"+localDateTime.getDayOfMonth()+"日";
    }

    public Workbook export(String queryDate) {
        logger.info("=>开始导出");

        File template = new File("D:\\template.xlsx");
        XSSFWorkbook workbook = null;
        try {
            workbook = (XSSFWorkbook) WorkbookFactory.create(template);
        } catch (Exception e) {
            logger.error("创建表格失败");
            throw new RuntimeException("创建表格失败");
        }

        //获取站点数据
        List<Map<String, Object>> airPort = exportDao.getAirPort();
        Map<String,String> airPortMap = new HashMap<>();
        for (Map<String, Object> map : airPort) {
            airPortMap.put(((String)map.get("airPortCode")).trim(),((String)map.get("airPortCodeNote")).trim());
        }

        Map<String,CellStyle> cellStyleMap = new HashMap<>();
        CellStyle center = ExportCellStyle.center(workbook);
        CellStyle alignRight = ExportCellStyle.alignRight(workbook);
        CellStyle yellow = ExportCellStyle.colour(workbook,IndexedColors.YELLOW.getIndex());
        cellStyleMap.put("center",center);
        cellStyleMap.put("alignRight",alignRight);
        cellStyleMap.put("yellow",yellow);
        cellStyleMap.put("borderThin",ExportCellStyle.borderThin(workbook));
        cellStyleMap.put("blue",ExportCellStyle.colour(workbook,IndexedColors.SKY_BLUE.getIndex()));
        cellStyleMap.put("alignRightAndYellow",ExportCellStyle.alignRightAndColor(workbook,IndexedColors.YELLOW.getIndex()));
        cellStyleMap.put("centerAndBold",ExportCellStyle.centerAndBold(workbook));
        cellStyleMap.put("centerAndRed",ExportCellStyle.centerAndColor(workbook,IndexedColors.RED.getIndex()));

        buildQuOrTccxAll(workbook,queryDate,"QU","去哪儿总表",airPortMap);
        buildQuOrTccxAll(workbook,queryDate,"XCCX","携程出行总表",airPortMap);
        buildTcSheet(workbook,cellStyleMap,queryDate,airPortMap);
        buildUncheck(workbook,cellStyleMap,queryDate);
        buildPST(workbook,cellStyleMap,queryDate,"QU","去哪儿PST",airPortMap);
        buildPST(workbook,cellStyleMap,queryDate,"TCYL","同程PST",airPortMap);
        buildPST(workbook,cellStyleMap,queryDate,"XCCX","携程出行PST",airPortMap);
        buildPST(workbook,cellStyleMap,queryDate,"XC","携程PST",airPortMap);


        logger.info("<==导出结束");
        return workbook;
    }

    private void buildQuOrTccxAll(XSSFWorkbook workbook,String queryDate,String supplyCode,String sheetName,Map<String,String> airPortMap) {

        logger.info("==>buildQuOrTccxAll开始准备数据");
        XSSFSheet sheet = workbook.getSheet(sheetName);

        Map<String,Map> dataMap = new HashMap<>();
        logger.info("第一个for循环开始");
        for(String k: airPortMap.keySet()) {
            //总量
            List<String> totalS = exportDao.getCensusData(queryDate, supplyCode, k, null,null);
            //已核销
            List<String> checkSuccessS = exportDao.getCensusData(queryDate, supplyCode, k, 1,3);
            //未核销
            List<String> checkFailS = exportDao.getCensusData(queryDate, supplyCode, k, 0,3);
            /*Map<String,List> map = new HashMap<>();
            map.put("total",total);
            map.put("checkSuccess",checkSuccess);
            map.put("checkFail",checkFail);
            dataList.add(map);*/
            List<Map<String,Object>> total = listToMapList(totalS);
            List<Map<String,Object>> checkSuccess = listToMapList(checkSuccessS);
            List<Map<String,Object>> checkFail = listToMapList(checkFailS);
            Map<String,Object> resultMap = new HashMap<>();
            Map<String,Object> totalMap = new HashMap<>();
            Map<String,Object> successMap = new HashMap<>();
            Map<String,Object> failMap = new HashMap<>();
            for (Map<String, Object> map : total) {
                totalMap.put((String)map.get("addTime"),map.get("total"));
            }
            for (Map<String, Object> map : checkSuccess) {
                successMap.put((String)map.get("addTime"),map.get("total"));
            }
            for (Map<String, Object> map : checkFail) {
                failMap.put((String)map.get("addTime"),map.get("total"));
            }
            resultMap.put("total",totalMap);
            resultMap.put("success",successMap);
            resultMap.put("fail",failMap);
            dataMap.put(k,resultMap);
        }

        logger.info("第一轮循环结束，开始第二轮循环");

        List<String> dateList = getDateList(queryDate);
        List<MainExcelData> mainDataList = new ArrayList<>();
        for (String date : dateList) {
            MainExcelData mainExcelData = new MainExcelData();
            mainExcelData.setDate(date);
            for(String s :dataMap.keySet()) {
                Map<String,Object> resultMap = dataMap.get(s);
                Map<String,Object> totalMap = (Map<String,Object>)resultMap.get("total");
                Map<String,Object> successMap = (Map<String,Object>)resultMap.get("success");
                Map<String,Object> failMap = (Map<String,Object>)resultMap.get("fail");
                int totalCount = totalMap.get(date)==null?0:(int)totalMap.get(date);
                int successCount = successMap.get(date)==null?0:(int)successMap.get(date);
                int failCount = failMap.get(date)==null?0:(int)failMap.get(date);
                String p = "";
                if(totalCount!=0) {
                    float percent = (float) (successCount+failCount)/totalCount*100;
                    BigDecimal b = new BigDecimal(percent);
                    p = b.setScale(2,b.ROUND_DOWN)+"%";
                }
                //此处的s为各个站点
                if("PVG".equals(s)) {
                    //上海浦东
                    mainExcelData.setShpdAll(String.valueOf(totalCount));
                    mainExcelData.setShpdCheck(String.valueOf(successCount));
                    mainExcelData.setShpdNoCheck(String.valueOf(failCount));
                    mainExcelData.setShpdPercent(p);
                    continue;
                }
                if("SZX".equals(s)) {
                    //深圳
                    mainExcelData.setSzAll(String.valueOf(totalCount));
                    mainExcelData.setSzCheck(String.valueOf(successCount));
                    mainExcelData.setSzNoCheck(String.valueOf(failCount));
                    mainExcelData.setSzPercent(p);
                    continue;
                }
                if("CAN".equals(s)) {
                    //广州
                    mainExcelData.setGzAll(String.valueOf(totalCount));
                    mainExcelData.setGzCheck(String.valueOf(successCount));
                    mainExcelData.setGzNoCheck(String.valueOf(failCount));
                    mainExcelData.setGzPercent(p);
                    continue;
                }
                if("TAO".equals(s)) {
                    //青岛
                    mainExcelData.setQdAll(String.valueOf(totalCount));
                    mainExcelData.setQdCheck(String.valueOf(successCount));
                    mainExcelData.setQdNoCheck(String.valueOf(failCount));
                    mainExcelData.setQdPercent(p);
                    continue;
                }
                if("KMG".equals(s)) {
                    //昆明
                    mainExcelData.setKmAll(String.valueOf(totalCount));
                    mainExcelData.setKmCheck(String.valueOf(successCount));
                    mainExcelData.setKmNoCheck(String.valueOf(failCount));
                    mainExcelData.setKmPercent(p);
                    continue;
                }
                if("CTU".equals(s)) {
                    //成都
                    mainExcelData.setCdAll(String.valueOf(totalCount));
                    mainExcelData.setCdCheck(String.valueOf(successCount));
                    mainExcelData.setCdNoCheck(String.valueOf(failCount));
                    mainExcelData.setCdPercent(p);
                    continue;
                }
                if("PEK".equals(s)) {
                    //北京
                    mainExcelData.setBjAll(String.valueOf(totalCount));
                    mainExcelData.setBjCheck(String.valueOf(successCount));
                    mainExcelData.setBjNoCheck(String.valueOf(failCount));
                    mainExcelData.setBjPercent(p);
                    continue;
                }
                if("XIY".equals(s)) {
                    //西安
                    mainExcelData.setXaAll(String.valueOf(totalCount));
                    mainExcelData.setXaCheck(String.valueOf(successCount));
                    mainExcelData.setXaNoCheck(String.valueOf(failCount));
                    mainExcelData.setXaPercent(p);
                    continue;
                }
                if("CGO".equals(s)) {
                    //郑州
                    mainExcelData.setZzAll(String.valueOf(totalCount));
                    mainExcelData.setZzCheck(String.valueOf(successCount));
                    mainExcelData.setZzNoCheck(String.valueOf(failCount));
                    mainExcelData.setZzPercent(p);
                    continue;
                }
                if("SHA".equals(s)) {
                    //上海虹桥
                    mainExcelData.setShhqAll(String.valueOf(totalCount));
                    mainExcelData.setShhqCheck(String.valueOf(successCount));
                    mainExcelData.setShhqNoCheck(String.valueOf(failCount));
                    mainExcelData.setShhqPercent(p);
                    continue;
                }
                if("CKG".equals(s)) {
                    //重庆
                    mainExcelData.setCqAll(String.valueOf(totalCount));
                    mainExcelData.setCqCheck(String.valueOf(successCount));
                    mainExcelData.setCqNoCheck(String.valueOf(failCount));
                    mainExcelData.setCqPercent(p);
                    continue;
                }

            }
            mainDataList.add(mainExcelData);
        }

        logger.info("==>buildQuOrTccxAll数据准备结束，开始填充数据");
        int rowNum = 2;
        CellStyle center = ExportCellStyle.center(workbook);
        CellStyle alignRightRed = ExportCellStyle.alignRight(workbook);
        alignRightRed.setFillForegroundColor(IndexedColors.RED.getIndex());
        alignRightRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(alignRightRed);
        CellStyle alignRightYellow = ExportCellStyle.alignRight(workbook);
        alignRightYellow.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        alignRightYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(alignRightYellow);

        for (MainExcelData data : mainDataList) {
            XSSFRow row = sheet.getRow(rowNum)==null?sheet.createRow(rowNum):sheet.getRow(rowNum);
            XSSFCell cell = row.getCell(0)==null?row.createCell(0):row.getCell(0);

            cell.setCellValue(data.getDate());

            //上海浦东
            cell = row.getCell(1)==null?row.createCell(1):row.getCell(1);
            cell.setCellStyle(center);
            cell.setCellValue(data.getShpdAll());
            cell = row.getCell(2)==null?row.createCell(2):row.getCell(2);
            cell.setCellStyle(center);
            cell.setCellValue(data.getShpdCheck());
            cell = row.getCell(3)==null?row.createCell(3):row.getCell(3);
            cell.setCellStyle(center);
            cell.setCellValue(data.getShpdNoCheck());
            cell = row.getCell(4)==null?row.createCell(4):row.getCell(4);
            if(!"".equals(data.getShpdPercent())) {
                if(Float.parseFloat(data.getShpdPercent().substring(0,data.getShpdPercent().length()-1))<RED_PERCENT) {
                    cell.setCellStyle(alignRightRed);
                }
                if(Float.parseFloat(data.getShpdPercent().substring(0,data.getShpdPercent().length()-1))>YELLOW_PERCENT) {
                    cell.setCellStyle(alignRightYellow);
                }
            }
            cell.setCellValue(data.getShpdPercent());

            //深圳
            cell = row.getCell(5)==null?row.createCell(5):row.getCell(5);
            cell.setCellStyle(center);
            cell.setCellValue(data.getSzAll());
            cell = row.getCell(6)==null?row.createCell(6):row.getCell(6);
            cell.setCellStyle(center);
            cell.setCellValue(data.getSzCheck());
            cell = row.getCell(7)==null?row.createCell(7):row.getCell(7);
            cell.setCellStyle(center);
            cell.setCellValue(data.getSzNoCheck());
            cell = row.getCell(8)==null?row.createCell(8):row.getCell(8);
            if(!"".equals(data.getSzPercent())) {
                if(Float.parseFloat(data.getSzPercent().substring(0,data.getSzPercent().length()-1))<RED_PERCENT) {
                    cell.setCellStyle(alignRightRed);
                }
                if(Float.parseFloat(data.getSzPercent().substring(0,data.getSzPercent().length()-1))>YELLOW_PERCENT) {
                    cell.setCellStyle(alignRightYellow);
                }
            }

            cell.setCellValue(data.getSzPercent());

            //广州
            cell = row.getCell(9)==null?row.createCell(9):row.getCell(9);
            cell.setCellStyle(center);
            cell.setCellValue(data.getGzAll());
            cell = row.getCell(10)==null?row.createCell(10):row.getCell(10);
            cell.setCellStyle(center);
            cell.setCellValue(data.getGzCheck());
            cell = row.getCell(11)==null?row.createCell(11):row.getCell(11);
            cell.setCellStyle(center);
            cell.setCellValue(data.getGzNoCheck());
            cell = row.getCell(12)==null?row.createCell(12):row.getCell(12);
            if(!"".equals(data.getGzPercent())) {
                if(Float.parseFloat(data.getGzPercent().substring(0,data.getGzPercent().length()-1))<RED_PERCENT) {
                    cell.setCellStyle(alignRightRed);
                }
                if(Float.parseFloat(data.getGzPercent().substring(0,data.getGzPercent().length()-1))>YELLOW_PERCENT) {
                    cell.setCellStyle(alignRightYellow);
                }
            }

            cell.setCellValue(data.getGzPercent());

            //青岛
            cell = row.getCell(13)==null?row.createCell(13):row.getCell(13);
            cell.setCellStyle(center);
            cell.setCellValue(data.getQdAll());
            cell = row.getCell(14)==null?row.createCell(14):row.getCell(14);
            cell.setCellStyle(center);
            cell.setCellValue(data.getQdCheck());
            cell = row.getCell(15)==null?row.createCell(15):row.getCell(15);
            cell.setCellStyle(center);
            cell.setCellValue(data.getQdNoCheck());
            cell = row.getCell(16)==null?row.createCell(16):row.getCell(16);

            if(!"".equals(data.getQdPercent())) {
                if(Float.parseFloat(data.getQdPercent().substring(0,data.getQdPercent().length()-1))<RED_PERCENT) {
                    cell.setCellStyle(alignRightRed);
                }
                if(Float.parseFloat(data.getQdPercent().substring(0,data.getQdPercent().length()-1))>YELLOW_PERCENT) {
                    cell.setCellStyle(alignRightYellow);
                }
            }
            cell.setCellValue(data.getQdPercent());

            //昆明
            cell = row.getCell(17)==null?row.createCell(17):row.getCell(17);
            cell.setCellStyle(center);
            cell.setCellValue(data.getKmAll());
            cell = row.getCell(18)==null?row.createCell(18):row.getCell(18);
            cell.setCellStyle(center);
            cell.setCellValue(data.getKmCheck());
            cell = row.getCell(19)==null?row.createCell(19):row.getCell(19);
            cell.setCellStyle(center);
            cell.setCellValue(data.getKmNoCheck());
            cell = row.getCell(20)==null?row.createCell(20):row.getCell(20);

            if(!"".equals(data.getKmPercent())) {
                if(Float.parseFloat(data.getKmPercent().substring(0,data.getKmPercent().length()-1))<RED_PERCENT) {
                    cell.setCellStyle(alignRightRed);
                }
                if(Float.parseFloat(data.getKmPercent().substring(0,data.getKmPercent().length()-1))>YELLOW_PERCENT) {
                    cell.setCellStyle(alignRightYellow);
                }
            }
            cell.setCellValue(data.getKmPercent());

            //成都
            cell = row.getCell(21)==null?row.createCell(21):row.getCell(21);
            cell.setCellStyle(center);
            cell.setCellValue(data.getCdAll());
            cell = row.getCell(22)==null?row.createCell(22):row.getCell(22);
            cell.setCellStyle(center);
            cell.setCellValue(data.getCdCheck());
            cell = row.getCell(23)==null?row.createCell(23):row.getCell(23);
            cell.setCellStyle(center);
            cell.setCellValue(data.getCdNoCheck());
            cell = row.getCell(24)==null?row.createCell(24):row.getCell(24);

            if(!"".equals(data.getCdPercent())) {
                if(Float.parseFloat(data.getCdPercent().substring(0,data.getCdPercent().length()-1))<RED_PERCENT) {
                    cell.setCellStyle(alignRightRed);
                }
                if(Float.parseFloat(data.getCdPercent().substring(0,data.getCdPercent().length()-1))>YELLOW_PERCENT) {
                    cell.setCellStyle(alignRightYellow);
                }
            }
            cell.setCellValue(data.getCdPercent());

            //北京
            cell = row.getCell(25)==null?row.createCell(25):row.getCell(25);
            cell.setCellStyle(center);
            cell.setCellValue(data.getBjAll());
            cell = row.getCell(26)==null?row.createCell(26):row.getCell(26);
            cell.setCellStyle(center);
            cell.setCellValue(data.getBjCheck());
            cell = row.getCell(27)==null?row.createCell(27):row.getCell(27);
            cell.setCellStyle(center);
            cell.setCellValue(data.getBjNoCheck());
            cell = row.getCell(28)==null?row.createCell(28):row.getCell(28);

            if(!"".equals(data.getBjPercent())) {
                if(Float.parseFloat(data.getBjPercent().substring(0,data.getBjPercent().length()-1))<RED_PERCENT) {
                    cell.setCellStyle(alignRightRed);
                }
                if(Float.parseFloat(data.getBjPercent().substring(0,data.getBjPercent().length()-1))>YELLOW_PERCENT) {
                    cell.setCellStyle(alignRightYellow);
                }
            }
            cell.setCellValue(data.getBjPercent());

            //西安
            cell = row.getCell(29)==null?row.createCell(29):row.getCell(29);
            cell.setCellStyle(center);
            cell.setCellValue(data.getXaAll());
            cell = row.getCell(30)==null?row.createCell(30):row.getCell(30);
            cell.setCellStyle(center);
            cell.setCellValue(data.getXaCheck());
            cell = row.getCell(31)==null?row.createCell(31):row.getCell(31);
            cell.setCellStyle(center);
            cell.setCellValue(data.getXaNoCheck());
            cell = row.getCell(32)==null?row.createCell(32):row.getCell(32);

            if(!"".equals(data.getXaPercent())) {
                if(Float.parseFloat(data.getXaPercent().substring(0,data.getXaPercent().length()-1))<RED_PERCENT) {
                    cell.setCellStyle(alignRightRed);
                }
                if(Float.parseFloat(data.getXaPercent().substring(0,data.getXaPercent().length()-1))>YELLOW_PERCENT) {
                    cell.setCellStyle(alignRightYellow);
                }
            }
            cell.setCellValue(data.getXaPercent());

            //郑州
            cell = row.getCell(33)==null?row.createCell(33):row.getCell(33);
            cell.setCellStyle(center);
            cell.setCellValue(data.getZzAll());
            cell = row.getCell(34)==null?row.createCell(34):row.getCell(34);
            cell.setCellStyle(center);
            cell.setCellValue(data.getZzCheck());
            cell = row.getCell(35)==null?row.createCell(35):row.getCell(35);
            cell.setCellStyle(center);
            cell.setCellValue(data.getZzNoCheck());
            cell = row.getCell(36)==null?row.createCell(36):row.getCell(36);

            if(!"".equals(data.getZzPercent())) {
                if(Float.parseFloat(data.getZzPercent().substring(0,data.getZzPercent().length()-1))<RED_PERCENT) {
                    cell.setCellStyle(alignRightRed);
                }
                if(Float.parseFloat(data.getZzPercent().substring(0,data.getZzPercent().length()-1))>YELLOW_PERCENT) {
                    cell.setCellStyle(alignRightYellow);
                }
            }
            cell.setCellValue(data.getZzPercent());

            //上海虹桥
            cell = row.getCell(37)==null?row.createCell(37):row.getCell(37);
            cell.setCellStyle(center);
            cell.setCellValue(data.getShhqAll());
            cell = row.getCell(38)==null?row.createCell(38):row.getCell(38);
            cell.setCellStyle(center);
            cell.setCellValue(data.getShhqCheck());
            cell = row.getCell(39)==null?row.createCell(39):row.getCell(39);
            cell.setCellStyle(center);
            cell.setCellValue(data.getShhqNoCheck());
            cell = row.getCell(40)==null?row.createCell(40):row.getCell(40);

            if(!"".equals(data.getShhqPercent())) {
                if(Float.parseFloat(data.getShhqPercent().substring(0,data.getShhqPercent().length()-1))<RED_PERCENT) {
                    cell.setCellStyle(alignRightRed);
                }
                if(Float.parseFloat(data.getShhqPercent().substring(0,data.getShhqPercent().length()-1))>YELLOW_PERCENT) {
                    cell.setCellStyle(alignRightYellow);
                }
            }
            cell.setCellValue(data.getShhqPercent());

            //重庆
            cell = row.getCell(41)==null?row.createCell(41):row.getCell(41);
            cell.setCellStyle(center);
            cell.setCellValue(data.getCqAll());
            cell = row.getCell(42)==null?row.createCell(42):row.getCell(42);
            cell.setCellStyle(center);
            cell.setCellValue(data.getCqCheck());
            cell = row.getCell(43)==null?row.createCell(43):row.getCell(43);
            cell.setCellStyle(center);
            cell.setCellValue(data.getCqNoCheck());
            cell = row.getCell(44)==null?row.createCell(44):row.getCell(44);

            if(!"".equals(data.getCqPercent())) {
                if(Float.parseFloat(data.getCqPercent().substring(0,data.getCqPercent().length()-1))<RED_PERCENT) {
                    cell.setCellStyle(alignRightRed);
                }
                if(Float.parseFloat(data.getCqPercent().substring(0,data.getCqPercent().length()-1))>YELLOW_PERCENT) {
                    cell.setCellStyle(alignRightYellow);
                }
            }
            cell.setCellValue(data.getCqPercent());

            rowNum++;
        }

        logger.info("==>buildQuOrTccxAll数据填充结束");
    }

    /**
     * 返回单个日期的日月格式
     * @param date
     * @return
     */
    private String formatDateMD(String date) {
        LocalDate localDate = LocalDate.parse(date,DateTimeFormatter.ofPattern("yyyyMMdd"));
        return localDate.getMonth().getValue()+"月"+localDate.getDayOfMonth()+"日";
    }

    /**
     * 获取日期列表 如：20190501，20190502.。。
     * @param queryDate
     * @return
     */
    private List<String> getDateList(String queryDate) {
        LocalDate start = LocalDate.parse(queryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = start.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        List<String> dates = Stream.iterate(start,date->date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start,end))
                .map(d-> d.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .collect(Collectors.toList());
        return dates;
    }

    /**
     * 批量格式化日期 返回例：5月1日，5月2日。。。
     * @param dateList
     * @return
     */
    private List<String> formatDateList(List<String> dateList) {
        List<String> result = new ArrayList<>();
        dateList.forEach(m->{
            LocalDate time = LocalDate.parse(m,DateTimeFormatter.ofPattern("yyyyMMdd"));
            result.add(time.getMonth().getValue()+"月"+time.getDayOfMonth()+"日");
        });
        return result;
    }

    /**
     * 批量格式化日期 月份.日期，例：5.1
     * @param dateList:日期格式yyyyMMdd
     */
    private List<String> formatDate(List<String> dateList) {
        List<String> mdList = dateList.stream()
                .map(date -> date.substring(4))
                .collect(Collectors.toList());
        List<String> formatList = new ArrayList<>();
        mdList.forEach(date->{
            String front = date.substring(0,2);
            String behind = date.substring(2);
            if(front.indexOf("0")==0) {
                front = front.replaceAll("0","");
            }

            if(behind.indexOf("0")==0) {
                behind = behind.replaceAll("0","");
            }
            formatList.add(front+"."+behind);
        });
        return formatList;
    }

    public static void main(String[] args) {
        Integer a=1,b=2,c=5;
        float d = (a+b)/c*100;
        float e = (float) (a+b)/c*100;
        System.out.println(d);
        System.out.println(e);
    }

    /**
     * 月份.日期，例：5.1
     * @param date:日期格式yyyyMMdd
     */
    private String formatDate(String date) {
        String result = date.substring(4);
        String front = result.substring(0,2);
        String behind = result.substring(2);
        if(front.indexOf("0")==0) {
            front = front.replaceAll("0","");
        }

        if(behind.indexOf("0")==0) {
            behind = behind.replaceAll("0","");
        }

        return front+"."+behind;
    }

    /**
     * 设置单元格的边框线
     * @param cellStyle
     */
    private void setBorder(CellStyle cellStyle) {
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    }

    public Map getUnContactList(String queryDate) {
        List<Map<String, Object>> unContactList = exportDao.getUnContactList(queryDate);
        Map<String,Object> resultMap = new HashMap<>();
        for (Map<String, Object> map : unContactList) {
            String customTel = (String) map.get("customTel");
            String supplierCode = (String) map.get("supplierCode");
            String airPortCode = (String) map.get("airPortCode");
            if(resultMap.containsKey(supplierCode)) {
                Map<String,Object> airPortMap = (java.util.HashMap)resultMap.get(supplierCode);
                if(airPortMap.containsKey(airPortCode)) {
                    airPortMap.put(airPortCode,airPortMap.get(airPortCode)+","+customTel);
                }else {
                    airPortMap.put(airPortCode,customTel);
                }
            }else{
                Map<String,Object> airPortMap = new HashMap<>();
                airPortMap.put(airPortCode,customTel);
                resultMap.put(supplierCode,airPortMap);
            }
        }

        return resultMap;
    }

    private List<Map<String,Object>> listToMapList(List<String> list) {
        List<Map<String,Object>> resultList = new ArrayList<>();
        Set<String> set = new HashSet<>(list);
        set.forEach(s->{
            Map<String,Object> map = new HashMap<>();
            map.put("addTime",s);
            map.put("total",Collections.frequency(list,s));
            resultList.add(map);
        });

        return resultList;
    }
}

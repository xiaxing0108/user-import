package com.user.userimport.service;

import com.alibaba.fastjson.JSONObject;
import com.user.userimport.dao.UserImportDao;
import com.user.userimport.pojo.UrlParams;
import com.user.userimport.utils.BaseDao;
import com.user.userimport.utils.DateUtils;
import com.user.userimport.utils.HttpUtil;
import com.user.userimport.utils.ValidateUtils;
import constant.UserImportConstant;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserImportService {

    private static final Logger logger = LoggerFactory.getLogger(UserImportService.class);
    @Autowired
    private UserImportDao userImportDao;
    @Autowired
    private BaseDao baseDao;

    private static final String[] CODE_ARRAY = {
            "A","B","C","D","E","F","G","H","I","J","K",
            "L","M","N","O","P","Q","R","S","T","U","V",
            "W","X","Y","Z","1","2","3","4","5","6","7",
            "8","9","0"
    };

    @Transactional
    public Map importUser(MultipartFile file) throws UnsupportedEncodingException {
        if(file.isEmpty()) {
            throw new RuntimeException("文件错误-文件不能为空!");
        }

        XSSFWorkbook workbook = null;
        try {
            workbook = (XSSFWorkbook)WorkbookFactory.create(file.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException("解析错误-请查看文件是否被加密");
        }
        XSSFSheet sheet = workbook.getSheetAt(0);
        int orderSaleNoIndex=-1,serviceIdIndex=-1,airStartTimeIndex=-1,airNoIndex=-1,airPortCodeIndex=-1,
                customNameIndex=-1,certNoIndex=-1,orderMarkIndex=-1,siteNoIndex=-1,statusIndex=-1,customTelIndex=-1,tCTimeIndex = -1,terminalIndex = -1;
        String supplierCode = "";
        String name = file.getOriginalFilename();
        //获取列名行
        XSSFRow head = sheet.getRow(0);
        if(name.contains("携程")) {
            if(name.contains("携程出行")) {
                supplierCode = "XCCX";
                Iterator<Cell> cellIterator = head.cellIterator();
                int index = 0;
                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String cellValue = cell.getStringCellValue().trim();
                    if(cellValue.equals("dport")) {
                        airPortCodeIndex = index;
                    }
                    if(cellValue.equals("takeofftime")) {
                        airStartTimeIndex = index;
                    }
                    if(cellValue.equals("passengername")) {
                        customNameIndex = index;
                    }
                    if(cellValue.equals("flight")) {
                        airNoIndex = index;
                    }
                    if(cellValue.equals("contacttel")) {
                        customTelIndex = index;
                    }
                    if(cellValue.equals("cardno")) {
                        certNoIndex = index;
                    }

                    index ++;

                }
            }else{
                supplierCode = "XC";
                Iterator<Cell> cellIterator = head.cellIterator();
                int index = 0;
                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String cellValue = cell.getStringCellValue().trim();
                    if(cellValue.equals("操作单号")) {
                        orderSaleNoIndex = index;
                    }
                    if(cellValue.equals("dport")) {
                        airPortCodeIndex = index;
                    }
                    if(cellValue.equals("takeofftime")) {
                        airStartTimeIndex = index;
                    }
                    if(cellValue.equals("出行人姓名")) {
                        customNameIndex = index;
                    }
                    if(cellValue.equals("航班号")) {
                        airNoIndex = index;
                    }
                    if(cellValue.equals("联系人手机")) {
                        customTelIndex = index;
                    }
                    if(cellValue.equals("备注")) {
                        orderMarkIndex = index;
                    }
                    if(cellValue.equals("出行人证件号")) {
                        certNoIndex = index;
                    }

                    index ++;

                }
            }
        }
        if(name.contains("艺龙")||name.contains("同程")) {
            supplierCode = "TCYL";
            Iterator<Cell> cellIterator = head.cellIterator();
            int index = 0;
            while(cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                String cellValue = cell.getStringCellValue().trim();
                if(cellValue.equals("机票订单号")) {
                    orderSaleNoIndex = index;
                }
                if(cellValue.equals("起飞时间")) {
                    airStartTimeIndex = index;
                }
                if(cellValue.equals("航班时间")) {
                    tCTimeIndex = index;
                }
                if(cellValue.equals("航班号")) {
                    airNoIndex = index;
                }
                if(cellValue.equals("出港地")) {
                    airPortCodeIndex = index;
                }
                if(cellValue.equals("乘客姓名")) {
                    customNameIndex = index;
                }
                if(cellValue.equals("联系方式")) {
                    customTelIndex = index;
                }
                if(cellValue.equals("备注")) {
                    orderMarkIndex = index;
                }
                index++;
            }
        }

        if(name.contains("去哪")) {
            supplierCode = "QU";
            Iterator<Cell> cellIterator = head.cellIterator();
            int index = 0;
            while(cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                String cellValue = cell.getStringCellValue().trim();
                if(cellValue.equals("订单号")) {
                    orderSaleNoIndex = index;
                }
                if(cellValue.equals("航站楼")) {
                    terminalIndex = index;
                }
                if(cellValue.equals("出发时间")) {
                    airStartTimeIndex = index;
                }
                if(cellValue.equals("航班号")) {
                    airNoIndex = index;
                }
                if(cellValue.equals("出发机场三字码")) {
                    airPortCodeIndex = index;
                }
                if(cellValue.equals("乘机人姓名")) {
                    customNameIndex = index;
                }
                if(cellValue.equals("联系人电话")) {
                    customTelIndex = index;
                }
                if(cellValue.equals("证件号")) {
                    certNoIndex = index;
                }
                if(cellValue.equals("劵状态")) {
                    statusIndex = index;
                }
                index ++;
            }
        }

        Map<String,UrlParams> map = new HashMap<>();
        List<Map<String,Object>> errorList = new ArrayList<>();
        if(name.contains("携程")) {
            if(name.contains("携程出行")) {
                int index = 1;
                while(true) {
                    XSSFRow row = sheet.getRow(index);
                    if(row==null||row.getCell(customTelIndex)==null) {
                        break;
                    }
                    String phone = row.getCell(customTelIndex).getStringCellValue().trim()+row.getCell(airNoIndex).getStringCellValue().trim();
                    if(StringUtils.isEmpty(phone)) {
                        break;
                    }
                    String customName = row.getCell(customNameIndex).getStringCellValue().trim();
                    String cardNo = row.getCell(certNoIndex).getStringCellValue().trim();
                    if(map.containsKey(phone)) {
                        UrlParams urlParams = map.get(phone);
                        urlParams.setCustomName(urlParams.getCustomName()+","+customName);
                        urlParams.setCertNo(urlParams.getCertNo()+";"+customName+":"+cardNo);
                    }else{
                        UrlParams urlParams = new UrlParams();
                        try {
                            urlParams.setAirStartTime(row.getCell(airStartTimeIndex).getStringCellValue());
                            urlParams.setAirNo(row.getCell(airNoIndex).getStringCellValue());
                            urlParams.setAirPortCode(row.getCell(airPortCodeIndex).getStringCellValue());
                            urlParams.setCustomTel(row.getCell(customTelIndex).getStringCellValue().trim());
                            urlParams.setCustomName(customName);
                            urlParams.setCertNo(customName+":"+cardNo);
                            urlParams.setSupplierCode(supplierCode);
                            urlParams.setOrderSaleNo(phone);
                            urlParams.setServiceId("1");
                        } catch (Exception e) {
                            logger.error("航班信息错误{}",e);
                            Map<String,Object> errorMap = new HashMap<>();
                            errorMap.put(phone,"航班信息错误");
                            errorList.add(errorMap);
                            index++;
                            continue;
                        }
                        map.put(phone,urlParams);
                    }
                    index++;

                }
            }else{
                int index = 1;
                while(true) {
                    XSSFRow row = sheet.getRow(index);
                    if(row==null) {
                        break;
                    }
                    if(row.getCell(orderSaleNoIndex)==null) {
                        break;
                    }
                    String orderNo = row.getCell(orderSaleNoIndex).getStringCellValue().trim();
                    if(StringUtils.isEmpty(orderNo)) {
                        break;
                    }
                    if(map.containsKey(orderNo)){
                        UrlParams urlParams = map.get(orderNo);
                        urlParams.setCustomName(urlParams.getCustomName()+","+row.getCell(customNameIndex).getStringCellValue().trim());
                    }else{
                        UrlParams urlParams = new UrlParams();
                        urlParams.setOrderSaleNo(orderNo);
                        urlParams.setSupplierCode(supplierCode);
                        urlParams.setServiceId("1");
                        Cell orderDateCell = row.getCell(airStartTimeIndex);
                        try {
                            String orderDate = orderDateCell.getStringCellValue();
                            urlParams.setAirStartTime(orderDate);
                            if(airNoIndex>=0) {
                                urlParams.setAirNo(row.getCell(airNoIndex).getStringCellValue().trim());
                            }
                            if(customNameIndex>=0) {
                                urlParams.setCustomName(row.getCell(customNameIndex).getStringCellValue().trim());
                            }
                            urlParams.setAirPortCode(row.getCell(airPortCodeIndex).getStringCellValue().trim());
                            urlParams.setCustomTel(row.getCell(customTelIndex).getStringCellValue().trim());
                            String certNo = row.getCell(certNoIndex).getStringCellValue().trim();
                            if(!certNo.contains("*")) {
                                urlParams.setCertNo(row.getCell(customNameIndex).getStringCellValue().trim()+":"+certNo);
                            }
                            if(orderMarkIndex>=0) {
                                String orderMark = row.getCell(orderMarkIndex).getStringCellValue().trim();
                                urlParams.setOrderMark(orderMark);
                            }
                        } catch (Exception e) {
                            logger.error("航班信息错误{}",e);
                            Map<String,Object> errorMap = new HashMap<>();
                            errorMap.put(orderNo,"航班信息错误");
                            errorList.add(errorMap);
                            index++;
                            continue;
                        }
                        map.put(orderNo,urlParams);
                    }
                    index ++;
                }
            }
        }
        if(name.contains("去哪")) {
            int index = 1;
            while(true) {
                XSSFRow row = sheet.getRow(index);
                if(row==null||row.getCell(orderSaleNoIndex)==null) {
                    break;
                }
                String orderNo = row.getCell(orderSaleNoIndex).getStringCellValue().trim();
                String status = row.getCell(statusIndex).getStringCellValue().trim();
                if("已注销".equals(status)) {
                    index ++;
                    continue;
                }
                if(StringUtils.isEmpty(orderNo)) {
                    break;
                }
                if(map.containsKey(orderNo)){
                    UrlParams urlParams = map.get(orderNo);
                    urlParams.setCustomName(urlParams.getCustomName()+","+row.getCell(customNameIndex).getStringCellValue().trim());
                    String certNo = row.getCell(certNoIndex).getStringCellValue().trim();
                    if(!certNo.contains("*")) {
                        urlParams.setCertNo(urlParams.getCertNo()+";"+row.getCell(customNameIndex).getStringCellValue().trim()+":"+certNo);
                    }
                    String remark = urlParams.getOrderMark();

                    int count = Integer.parseInt(remark.split("人")[0])+1;
                    String newCus = remark+","+row.getCell(customNameIndex).getStringCellValue().trim();
                    urlParams.setOrderMark(count+newCus.substring(newCus.indexOf("人"),newCus.length()));
                    map.put(orderNo,urlParams);
                }else{
                    UrlParams urlParams = new UrlParams();
                    String terminal = row.getCell(terminalIndex).getStringCellValue();
                    if(!StringUtils.isEmpty(terminal)&&!" ".equals(terminal)) {
                        urlParams.setTerminal(terminal);
                    }
                    urlParams.setOrderSaleNo(orderNo);
                    urlParams.setSupplierCode(supplierCode);
                    urlParams.setServiceId("1");
                    Cell orderDateCell = row.getCell(airStartTimeIndex);
                    String orderDate = "" ;
                    try {
                        if(orderDateCell.getCellTypeEnum()==CellType.NUMERIC) {
                            if(DateUtil.isCellDateFormatted(orderDateCell)) {
                                Date date = orderDateCell.getDateCellValue();
                                orderDate = DateUtils.dateToString(date,"yyyy/MM/dd HH:mm:ss");
                            }
                        }else{
                            orderDate = orderDateCell.getStringCellValue().trim().substring(0,16).replaceAll("-","/");
                        }
                        urlParams.setAirStartTime(orderDate);
                        if(airNoIndex>=0) {
                            urlParams.setAirNo(row.getCell(airNoIndex).getStringCellValue().trim());
                        }
                        if(customNameIndex>=0) {
                            urlParams.setCustomName(row.getCell(customNameIndex).getStringCellValue().trim());
                        }
                        urlParams.setAirPortCode(row.getCell(airPortCodeIndex).getStringCellValue().trim());
                        urlParams.setCustomTel(row.getCell(customTelIndex).getStringCellValue().trim());
                        String certNo = row.getCell(certNoIndex).getStringCellValue().trim();
                        if(!certNo.contains("*")) {
                            urlParams.setCertNo(row.getCell(customNameIndex).getStringCellValue().trim()+":"+certNo);
                        }
                        String orderMark = "1人："+row.getCell(customNameIndex).getStringCellValue().trim();
                        urlParams.setOrderMark(orderMark);
                    } catch (Exception e) {
                        logger.error("航班信息错误{}",e);
                        Map<String,Object> errorMap = new HashMap();
                        errorMap.put(orderNo,"航班信息错误");
                        errorList.add(errorMap);
                        index++;
                        continue;
                    }
                    map.put(orderNo,urlParams);
                }
                index ++;

            }
            logger.info(map.toString());
        }

        if(name.contains("同程")||name.contains("艺龙")) {
            int index = 1;
            while(true) {
                XSSFRow row = sheet.getRow(index);
                if(row==null||row.getCell(orderSaleNoIndex)==null) {
                    break;
                }
                String orderNo = row.getCell(orderSaleNoIndex).getStringCellValue().trim();
                if(StringUtils.isEmpty(orderNo)) {
                    break;
                }
                if(map.containsKey(orderNo)) {
                    UrlParams urlParams = map.get(orderNo);
                    urlParams.setCustomName(urlParams.getCustomName()+","+row.getCell(customNameIndex).getStringCellValue().trim());
                    /*String remark = urlParams.getOrderMark();
                    int count = Integer.parseInt(remark.split("人")[0])+1;
                    String before = remark.substring(0,remark.indexOf(";"));
                    String after = remark.substring(remark.indexOf(";"),remark.length());
                    urlParams.setOrderMark(count+before.substring(before.indexOf("人"),before.length())+after);*/
                    map.put(orderNo,urlParams);
                }else{
                    UrlParams urlParams = new UrlParams();
                    urlParams.setOrderSaleNo(orderNo);
                    urlParams.setSupplierCode(supplierCode);
                    urlParams.setServiceId("1");
                    Cell startDateCell = row.getCell(airStartTimeIndex);
                    startDateCell.setCellType(CellType.NUMERIC);
                    Cell startTimeCell = row.getCell(tCTimeIndex);
                    startTimeCell.setCellType(CellType.NUMERIC);
                    try {
                        String startDate = DateUtils.dateToString(startDateCell.getDateCellValue(),"yyyy/MM/dd")+" "+DateUtils.dateToString(startTimeCell.getDateCellValue(),"HH:mm");
                        urlParams.setAirStartTime(startDate);
                        if(airNoIndex>=0) {
                            urlParams.setAirNo(row.getCell(airNoIndex).getStringCellValue().trim());
                        }
                        urlParams.setAirPortCode(row.getCell(airPortCodeIndex).getStringCellValue().trim());
                        if(customNameIndex>=0) {
                            urlParams.setCustomName(row.getCell(customNameIndex).getStringCellValue().trim());
                        }
                        urlParams.setCustomTel(row.getCell(customTelIndex).getStringCellValue().trim());
                        //String orderMark = "1人"+row.getCell(customNameIndex).getStringCellValue().trim()+";"+row.getCell(orderMarkIndex).getStringCellValue().trim();
                        if(orderMarkIndex>=0) {
                            String orderMark = row.getCell(orderMarkIndex).getStringCellValue().trim();
                            urlParams.setOrderMark(orderMark);
                        }
                    } catch (Exception e) {
                        logger.error("航班信息错误{}",e);
                        Map<String,Object> errorMp = new HashMap<>();
                        errorMp.put(orderNo,"航班信息错误");
                        errorList.add(errorMp);
                        index++;
                        continue;
                    }
                    map.put(orderNo,urlParams);
                }
                index ++;
            }
            logger.info(map.toString());
        }

        //将map中数据的保存到数据库中
        Map<String,Object> resultMap = new HashMap<>();
        List<UrlParams> orderList = new ArrayList<>();
        List<Map<String,Object>> paramsMapList = new ArrayList<>();

        if(map.size()>0) {
            if(name.contains("携程出行")) {
                for(String s :map.keySet()) {
                    UrlParams urlParams = map.get(s);
                    urlParams.setOrderNo(getOrderNo());
                    Map<String,Object> errorMap = new HashMap<>();

                    // 参数验证跟默认值赋予
                    String phone = urlParams.getCustomTel();
                    if(StringUtils.isEmpty(urlParams.getAirPortCode())) {
                        urlParams.setOrderStatus(0);
                    }else{
                        urlParams.setOrderStatus(1);
                    }
                    if(!StringUtils.isEmpty(urlParams.getCertNo())) {
                        urlParams.setInputCertNoTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                    if(!StringUtils.isEmpty(urlParams.getSiteNo())) {
                        urlParams.setInputSiteNoTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                    //必填项校验
                    /*if(StringUtils.isEmpty(urlParams.getOrderSaleNo())) {
                        errorMap.put("空销售单号","销售单号不能为空!");
                        errorList.add(errorMap);
                        continue;
                    }*/
                    if(StringUtils.isEmpty(phone)) {
                        errorMap.put(urlParams.getCustomTel(),"手机号不能为空!");
                        errorList.add(errorMap);
                        continue;
                    }
                    if(StringUtils.isEmpty(urlParams.getAirStartTime())||" ".equals(urlParams.getAirStartTime())) {
                        errorMap.put(urlParams.getCustomTel(),"航班日期不能为空!");
                        errorList.add(errorMap);
                        continue;
                    }
                /*if(StringUtils.isEmpty(urlParams.getAirPortCode())) {
                    errorMap.put(urlParams.getOrderSaleNo(),"航站站点编码不能为空!");
                    errorList.add(errorMap);
                    continue;
                }*/
                    if(!ValidateUtils.checkPhone(phone)) {
                        errorMap.put(urlParams.getCustomTel(),"手机号码格式不正确");
                        errorList.add(errorMap);
                        continue;
                    }

                    if(userImportDao.existsXccxOrder(urlParams.getAirStartTime(),s)!=null) {
                        errorMap.put(urlParams.getCustomTel(),"订单已经存在，不可重复添加!");
                        errorList.add(errorMap);
                        continue;
                    }

                    //插入数据
                    orderList.add(urlParams);
                    //userImportDao.insertOrder(urlParams);
                    String certNo = urlParams.getCertNo();
                    if (certNo!=null) {
                        String[] certs = certNo.split(";");
                        if(certs.length>0) {
                            for (int i = 0; i < certs.length; i++) {
                                Map<String,Object> paramsMap = new HashMap<>();
                                String[] certInfo = certs[i].split(":");
                                paramsMap.put("orderNo",urlParams.getOrderNo());
                                if(certInfo.length==2) {
                                    paramsMap.put("customName",certInfo[0]);
                                    paramsMap.put("certNo",certInfo[1]);
                                }
                                //userImportDao.insertCertInfo(paramsMap);
                                paramsMapList.add(paramsMap);
                            }
                        }
                    }else{
                        String certInfo = urlParams.getCustomName();
                        if(!StringUtils.isEmpty(certInfo)) {
                            String[] certInfoArr = certInfo.split(",");
                            for (int i = 0; i < certInfoArr.length; i++) {
                                Map<String,Object> paramsMap = new HashMap<>();
                                paramsMap.put("orderNo",urlParams.getOrderNo());
                                paramsMap.put("customName",certInfoArr[i]);
                                paramsMapList.add(paramsMap);
                            }
                        }
                    }

                }
            }else{
                for(String s:map.keySet()) {
                    UrlParams urlParams = map.get(s);
                    urlParams.setOrderNo(getOrderNo());
                    Map<String,Object> errorMap = new HashMap<>();
                    // 参数验证跟默认值赋予
                    String phone = urlParams.getCustomTel();
                    if(StringUtils.isEmpty(urlParams.getAirPortCode())) {
                        urlParams.setOrderStatus(0);
                    }else{
                        urlParams.setOrderStatus(1);
                    }
                    if(!StringUtils.isEmpty(urlParams.getCertNo())) {
                        urlParams.setInputCertNoTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                    if(!StringUtils.isEmpty(urlParams.getSiteNo())) {
                        urlParams.setInputSiteNoTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                    //必填项校验
                    if(StringUtils.isEmpty(urlParams.getOrderSaleNo())) {
                        errorMap.put("空销售单号","销售单号不能为空!");
                        errorList.add(errorMap);
                        continue;
                    }
                    if(StringUtils.isEmpty(phone)) {
                        errorMap.put(urlParams.getOrderSaleNo(),"手机号不能为空!");
                        errorList.add(errorMap);
                        continue;
                    }
                    if(StringUtils.isEmpty(urlParams.getAirStartTime())||" ".equals(urlParams.getAirStartTime())) {
                        errorMap.put(urlParams.getOrderSaleNo(),"航班日期不能为空!");
                        errorList.add(errorMap);
                        continue;
                    }
                /*if(StringUtils.isEmpty(urlParams.getAirPortCode())) {
                    errorMap.put(urlParams.getOrderSaleNo(),"航站站点编码不能为空!");
                    errorList.add(errorMap);
                    continue;
                }*/
                    if(!ValidateUtils.checkPhone(phone)) {
                        errorMap.put(urlParams.getOrderSaleNo(),"手机号码格式不正确");
                        errorList.add(errorMap);
                        continue;
                    }

                    if(userImportDao.existsOrder(urlParams.getOrderSaleNo())!=null) {
                        errorMap.put(urlParams.getOrderSaleNo(),"订单已经存在，不可重复添加!");
                        errorList.add(errorMap);
                        continue;
                    }
                    //插入数据
                    orderList.add(urlParams);
                    //userImportDao.insertOrder(urlParams);
                    String certNo = urlParams.getCertNo();
                    if (certNo!=null) {
                        String[] certs = certNo.split(";");
                        if(certs.length>0) {
                            for (int i = 0; i < certs.length; i++) {
                                Map<String,Object> paramsMap = new HashMap<>();
                                String[] certInfo = certs[i].split(":");
                                paramsMap.put("orderNo",urlParams.getOrderNo());
                                if(certInfo.length==2) {
                                    paramsMap.put("customName",certInfo[0]);
                                    paramsMap.put("certNo",certInfo[1]);
                                }
                                //userImportDao.insertCertInfo(paramsMap);
                                paramsMapList.add(paramsMap);
                            }
                        }
                    }else{
                        String certInfo = urlParams.getCustomName();
                        if(!StringUtils.isEmpty(certInfo)) {
                            String[] certInfoArr = certInfo.split(",");
                            for (int i = 0; i < certInfoArr.length; i++) {
                                Map<String,Object> paramsMap = new HashMap<>();
                                paramsMap.put("orderNo",urlParams.getOrderNo());
                                paramsMap.put("customName",certInfoArr[i]);
                                paramsMapList.add(paramsMap);
                            }
                        }
                    }
                }
            }
            //批量插入数据
            baseDao.batchInsertOrder(orderList);
            baseDao.batchInsertCertInfo(paramsMapList);


            resultMap.put("total",map.size());
            resultMap.put("error",errorList);

        }

        return resultMap;

    }

    public Map<String,Object> getList(Map paramsMap) {
        //根据条件查询出所有数据
        List<Map<String,Object>> list = userImportDao.getList(paramsMap);
        logger.info("符合条件的乘客信息数量:"+list.size()+"详情："+list.toString());

        //第一层筛选：权限筛选，根据用户的权限筛选符合条件的乘客信息；
        if(paramsMap.containsKey("adminName")) {
            String adminName = (String)paramsMap.get("adminName");
            Map<String, Object> userRight = userImportDao.getUserRight(adminName);
            //当groupLevel为2的时候，需要在订单筛选时增加用户airPortCode=订单的airPortCode
            if(userRight.get("groupLevel")!=null) {
                if(2==(Integer)userRight.get("groupLevel")) {
                    list = list.stream().filter(m->m.get("airPortCode").equals(userRight.get("airPortCode")))
                            .collect(Collectors.toList());
                }
                //当groupLevel=3 or groupLevel=4的时候，需要在订单筛选时增加[accountBelong]=adminName
                if(3==(Integer)userRight.get("groupLevel") || 4==(Integer)userRight.get("groupLevel")) {
                    list = list.stream().filter(m->adminName.equals(m.get("accountBelong")))
                            .collect(Collectors.toList());
                }
            }
        }

        logger.info("权限筛选之后的乘客信息数量:"+list.size()+"详情："+list.toString());

        //第二层筛选：置顶筛选，置顶相关数据，并且重新排序
        Integer step1 = (Integer) paramsMap.get("timeIntervalStep1");
        Integer step2 = (Integer) paramsMap.get("timeIntervalStep2");
        Integer step3 = (Integer) paramsMap.get("timeIntervalStep3");
        List<Map<String,Object>> upList = null;
        try {
            upList = new ArrayList<>();
            Iterator<Map<String, Object>> iterator = list.iterator();
            while(iterator.hasNext()) {
                Map<String,Object> passenger = iterator.next();
                //获取航班出发日期与当前日期做对比，判断是否需要置顶
                Timestamp timeStamp = (Timestamp)passenger.get("airStartTime");
                LocalDateTime airStartTime = LocalDateTime.ofInstant(timeStamp.toInstant(), ZoneId.of("+8"));
                Long nowSec = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
                Long airStartTimeSec = airStartTime
                        .toEpochSecond(ZoneOffset.of("+8"));
                Long divide = airStartTimeSec - nowSec;
                if(divide<=step1*60*60&&passenger.get("inputCertNoTime")==null) {
                    passenger.put("upReason","cert");
                    upList.add(passenger);
                    iterator.remove();
                    continue;
                }
                if(divide<=step2*60*60&&passenger.get("finishContactTime")==null) {
                    passenger.put("upReason","contact");
                    upList.add(passenger);
                    iterator.remove();
                    continue;
                }
                if(divide<=step3*60*60&&passenger.get("inputSiteNoTime")==null) {
                    passenger.put("upReason","site");
                    upList.add(passenger);
                    iterator.remove();
                    continue;
                }
            }
        } catch (Exception e) {
            logger.error("置顶筛选失败:{}",e);
            throw new RuntimeException("获取乘客信息失败!");
        }

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("upList",upList);
        resultMap.put("normalList",list);


        return resultMap;
    }


    /**
     * 生成订单号 N + yyyymmddHHMMSS + 3个随机字符（字符+数字） +  00
     * @return
     */
    private synchronized String getOrderNo() {
        Random random = new Random();

        return "N"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+
                CODE_ARRAY[random.nextInt(CODE_ARRAY.length)]+
                CODE_ARRAY[random.nextInt(CODE_ARRAY.length)]+
                CODE_ARRAY[random.nextInt(CODE_ARRAY.length)]+
                CODE_ARRAY[random.nextInt(CODE_ARRAY.length)]+
                CODE_ARRAY[random.nextInt(CODE_ARRAY.length)]
                ;
    }
}

package com.user.userimport.controller;

import com.user.userimport.service.ExportService;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/census")
@CrossOrigin
public class ExportController {

    private static final Logger logger = LoggerFactory.getLogger(ExportController.class);

    @Autowired
    private ExportService exportService;

    @RequestMapping("/export")
    public void export(String queryDate, HttpServletResponse response) {
        Workbook workbook = exportService.export(queryDate);
        LocalDate date = LocalDate.parse(queryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fileName = date.getMonth().getValue()+"月统计报表.xlsx";
        setResponseHeader(response,fileName);
        try(OutputStream os = response.getOutputStream()){
            workbook.write(os);
            os.flush();
        }catch(Exception e){
            logger.error("导出统计报表失败{}",e);
        }
    }


    /**
     * 设置响应信息
     * @param response
     * @param fileName
     */
    private void setResponseHeader(HttpServletResponse response,String fileName) {
        try {
            fileName = new String(fileName.getBytes(),"ISO8859-1");

            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition","attachment;filename="+fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception e) {
            logger.error("响应信息设置失败!");
        }
    }




}

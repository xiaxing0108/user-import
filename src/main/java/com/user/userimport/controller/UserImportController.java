package com.user.userimport.controller;

import com.user.userimport.service.UserImportService;
import com.user.userimport.utils.WebResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserImportController {

    private static final Logger logger = LoggerFactory.getLogger(UserImportService.class);

    @Autowired
    private UserImportService userImportService;

    @RequestMapping("/userImport")
    public Map userImport(@RequestParam(name="file") MultipartFile file) {

        try {
            Map strings = userImportService.importUser(file);
            logger.info(strings.toString());
            return WebResult.success("批量导入成功!",strings);
        } catch (Exception e) {
            logger.error("批量导入失败{}",e);
            return WebResult.error("批量导入失败!"+e.getMessage());
        }

    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

    @RequestMapping("/getList")
    public Map getList(@RequestBody Map<String,Object> params) {
        try {
            Map<String, Object> list = userImportService.getList(params);
            return WebResult.success("获取乘客信息列表成功",list);
        } catch (Exception e) {
            logger.error("获取乘客信息列表失败");
            return WebResult.error("获取乘客信息列表失败");
        }
    }

}

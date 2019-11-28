package com.user.userimport.service;

import com.alibaba.fastjson.JSONObject;
import com.user.userimport.utils.HttpUtil;
import com.user.userimport.utils.WebAppMd5;
import constant.UserImportConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: xiaxing
 * @create: 2019-11-28 10:19
 **/
@Service
@Slf4j
public class PassengerService {

    public JSONObject query(Map<String,String> paramsMap) {
        paramsMap.put("timestamp",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        paramsMap.put("md5", WebAppMd5.getMd5Parm(paramsMap));
        String response = HttpUtil.doPost(UserImportConstant.P_URL, JSONObject.toJSONString(paramsMap));

        if(response==null) {
            throw new RuntimeException("第三方接口调用失败");
        }

        return JSONObject.parseObject(response);
    }

}

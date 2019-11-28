package com.user.userimport.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.user.userimport.service.PassengerService;
import com.user.userimport.utils.WebResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @description: 乘客信息第三方接口
 * @author: xiaxing
 * @create: 2019-11-28 10:15
 **/
@RestController
@CrossOrigin
@RequestMapping("/api")
@Slf4j
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    /**
     * 查询乘客信息接口
     * 第一种参数：
     * mode	        字符串	功能名称	  queryPrBySurname
     * flightDate	字符串	航班日期	  2019-09-29
     * flightNo	    字符串	航班号	  1152
     * carrier	    字符串	承运人	  HO
     * deptAirport	字符串	始发站	  SZX
     * surname	    字符串	旅客姓	  ZHANG
     *
     * 第二种，根据index查询乘客信息
     * mode	    字符串	功能名称	  queryPrByIndex
     * index	字符串	序号	  1
     *
     *
     * @param paramsMap
     * @return
     */
    @PostMapping("/query")
    public Map query(@RequestParam Map<String,String> paramsMap) {

        try{
            return WebResult.success("success",passengerService.query(paramsMap));
        }catch(Exception e){
            log.error("调用失败{}",e);
            return WebResult.error("接口调用失败");
        }
    }


}

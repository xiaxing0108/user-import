package com.user.userimport.utils;

import java.util.HashMap;
import java.util.Map;

public class WebResult {

    public static Map success(String msg,Object data) {
        Map map = new HashMap();
        map.put("code",20000);
        map.put("message",msg);
        map.put("data",data);
        return map;
    }

    public static Map error(String msg) {
        Map map = new HashMap();
        map.put("code",40000);
        map.put("message",msg);
        map.put("data",null);
        return map;
    }
}

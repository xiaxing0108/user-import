package com.user.userimport.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtils {

    /**
     * 手机号码格式校验
     * @param phoneNum
     * @return
     */
    public static boolean checkPhone(String phoneNum) {
        String reg = "^((13[0-9])|(14[5,7,9])|(15[^4])|(16[0-9])|(18[0-9])|(19[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";
        return match(reg,phoneNum);
    }

    /**
     * 校验工具
     * @param reg 正则表达式
     * @param str 待校验的字符串
     * @return
     */
    private static boolean match(String reg,String str) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static void main(String[] args) {
        System.out.println(ValidateUtils.checkPhone("16689031222"));
    }
}

package com.user.userimport.enums;

/**
 * @description: excel类型
 * @author: xiaxing
 * @create: 2019-09-27 11:27
 **/
public enum ExcelTypeEnum {

    QU_ALL(0,"去哪儿总表"),
    XCCX_ALL(1,"携程出行总表"),
    TC_ALL(2,"同程总表"),
    UNCHECK_DETAIL(3,"未核销明细"),
    QU_PST(4,"去哪儿PST"),
    XCCX_PST(5,"携程出行PST"),
    XC_PST(6,"携程PST"),
    TC_PST(7,"同程PST")
    ;
    private Integer code;
    private String msg;

    ExcelTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static String getMsgByCode(Integer code) {
        for (ExcelTypeEnum excelTypeEnum : ExcelTypeEnum.values()) {
            if(excelTypeEnum.code==code) {
                return excelTypeEnum.msg;
            }
        }
        return null;
    }
}

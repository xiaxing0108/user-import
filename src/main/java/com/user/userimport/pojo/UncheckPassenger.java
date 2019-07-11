package com.user.userimport.pojo;

import java.time.LocalDate;

public class UncheckPassenger {

    private String customName;
    private LocalDate addTime;
    private String orderSaleNo;
    private String airPortCode;
    private String customTel;
    private String orderMark;

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public LocalDate getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDate addTime) {
        this.addTime = addTime;
    }

    public String getOrderSaleNo() {
        return orderSaleNo;
    }

    public void setOrderSaleNo(String orderSaleNo) {
        this.orderSaleNo = orderSaleNo;
    }

    public String getAirPortCode() {
        return airPortCode;
    }

    public void setAirPortCode(String airPortCode) {
        this.airPortCode = airPortCode;
    }

    public String getCustomTel() {
        return customTel;
    }

    public void setCustomTel(String customTel) {
        this.customTel = customTel;
    }

    public String getOrderMark() {
        return orderMark;
    }

    public void setOrderMark(String orderMark) {
        this.orderMark = orderMark;
    }
}

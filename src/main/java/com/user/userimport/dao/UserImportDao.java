package com.user.userimport.dao;

import com.user.userimport.pojo.PassengerInfo;
import com.user.userimport.pojo.UrlParams;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface UserImportDao {
    /**
     * 获取乘客信息列表的list
     * @param paramsMap
     * @return
     * @throws DataAccessException
     */
    List<Map<String,Object>> getList(Map paramsMap)throws DataAccessException;

    /**
     * 根据用户名称获取用户权限信息
     * @param adminName
     * @return
     * @throws DataAccessException
     */
    Map<String,Object> getUserRight(String adminName)throws DataAccessException;

    /**
     * 插入订单信息
     * @param urlParams
     * @throws DataAccessException
     */
    void insertOrder(UrlParams urlParams)throws DataAccessException;

    /**
     * 插入乘客信息
     * @param paramsMap
     * @throws DataAccessException
     */
    void insertCertInfo(Map<String,Object> paramsMap)throws DataAccessException;

    /**
     * 判断订单是否已经存在
     * @param orderSaleNo
     * @return
     * @throws DataAccessException
     */
    Integer existsOrder(String orderSaleNo)throws DataAccessException;

    /**
     * 判断携程出行订单是否存在
     * @return
     */
    Integer existsXccxOrder(@Param("airStartTime")String airStartTime,
                            @Param("phone") String phone);

}

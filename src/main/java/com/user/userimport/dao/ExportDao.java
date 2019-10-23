package com.user.userimport.dao;

import com.user.userimport.pojo.CensusData;
import com.user.userimport.pojo.UncheckPassenger;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ExportDao {
    /**
     * 获取统计数据
     * @param queryDate 需要查询的月份的任意一天
     * @param supplierCode 供应商代码
     * @param airPortCode 出发机场三字码
     * @return
     */
    List<Map<String,Object>> getCensusData(@Param("queryDate") String queryDate,
                                           @Param("supplierCode") String supplierCode,
                                           @Param("airPortCode") String airPortCode,
                                           @Param("checkOrderStatus") Integer checkOrderStatus,
                                           @Param("orderStatus") Integer orderStatus)throws DataAccessException;

    /**
     * 获取统计数据
     * @param queryDate 需要查询的月份的任意一天
     * @param supplierCode 供应商代码
     * @param airPortCode 出发机场三字码
     * @return
     */
    List<CensusData> getCensusDataTest(@Param("queryDate") String queryDate,
                                       @Param("supplierCode") String supplierCode,
                                       @Param("airPortCode") String airPortCode,
                                       @Param("checkOrderStatus") Integer checkOrderStatus,
                                       @Param("orderStatus") Integer orderStatus)throws DataAccessException;

    /**
     * 获取未核销的乘客列表
     * @return
     * @throws DataAccessException
     */
    List<Map<String,Object>> uncheckList(String queryDate)throws DataAccessException;

    /**
     * 查询PST数据
     * @return
     * @throws DataAccessException
     */
    List<Map<String,Object>> PSTList(@Param("queryDate") String queryDate,
                                     @Param("supplierCode") String supplierCode,
                                     @Param("airPortCode") String airPortCode)throws DataAccessException;

    /**
     * 查询站点数据
     * @return
     * @throws DataAccessException
     */
    List<Map<String,Object>> getAirPort()throws DataAccessException;

    /**
     * 查询某一天待联系的用户手机号码
     * @param queryDate
     * @return
     * @throws DataAccessException
     */
    List<Map<String,Object>> getUnContactList(@Param("queryDate")String queryDate)throws DataAccessException;
}

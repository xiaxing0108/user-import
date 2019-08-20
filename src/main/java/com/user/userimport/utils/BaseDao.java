package com.user.userimport.utils;

import com.user.userimport.pojo.UrlParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@PropertySource(value = "classpath:application.properties")
public class BaseDao {
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.driver-class-name}")
    private String jdbcDriver;

    private Integer batchSize = 2000;

    /**
     * 获取数据库连接
     * @return
     */
    public Connection getConnection() {
        try {
            //加载驱动
            Class.forName(jdbcDriver);
            //创建连接
            return DriverManager.getConnection(jdbcUrl,username,password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("驱动类未找到，获取连接失败{}",e);
        } catch (SQLException e) {
            throw new RuntimeException("获取连接失败{}",e);
        }
    }

    /**
     * 关闭连接失败
     * @param connection
     */
    private void close(Connection connection) {
        if(connection!=null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("关闭数据库连接失败{}",e);
            }
        }
    }

    /**
     * 批量插入订单信息
     * @param list
     */
    public void batchInsertOrder(List<UrlParams> list) {
        try{
            Connection connection = getConnection();
            StringBuilder sql = new StringBuilder();
                sql.append("insert into tb_order ");
                sql.append("(orderNo,orderSaleNo,supplierCode,airPortCode,airNo,siteNo,serviceId,certNo,");
                sql.append("customName,customTel,airStartTime,orderMark,addType,orderSort,orderStatus,addTime,inputCertNoTime,inputSiteNoTime) ");
                sql.append("values ");
                sql.append("(?,?,?,?,?,?,?,?,?,?,?,?,2,100,?,getDate(),?,?)");
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < list.size(); i++) {
                UrlParams urlParams = list.get(i);
                ps.setString(1,urlParams.getOrderNo());
                ps.setString(2,urlParams.getOrderSaleNo());
                ps.setString(3,urlParams.getSupplierCode());
                ps.setString(4,urlParams.getAirPortCode());
                ps.setString(5,urlParams.getAirNo());
                ps.setString(6,urlParams.getSiteNo());
                ps.setString(7,urlParams.getServiceId());
                ps.setString(8,urlParams.getCertNo());
                ps.setString(9,urlParams.getCustomName());
                ps.setString(10,urlParams.getCustomTel());
                ps.setString(11,urlParams.getAirStartTime());
                ps.setString(12,urlParams.getOrderMark());
                ps.setInt(13,urlParams.getOrderStatus());
                ps.setString(14,urlParams.getInputCertNoTime());
                ps.setString(15,urlParams.getInputSiteNoTime());

                if(i%batchSize==0) {
                    ps.executeBatch();
                }
                ps.addBatch();
            }

            ps.executeBatch();
            close(connection);

        }catch(Exception e){
            throw new RuntimeException("批量插入订单信息失败{}",e);
        }
    }

    /**
     * 批量插入乘客信息
     * @param list
     */
    public void batchInsertCertInfo(List<Map<String,Object>> list) {
        try{
            Connection connection = getConnection();
            StringBuilder sql = new StringBuilder();
                sql.append("insert into tb_certInfo ");
                sql.append("(orderNo,customName,certNo) ");
                sql.append("values(?,?,?)");
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < list.size(); i++) {
                Map<String,Object> paramsMap = list.get(i);
                ps.setString(1,(String) paramsMap.get("orderNo"));
                ps.setString(2,(String)paramsMap.get("customName"));
                ps.setString(3,(String)paramsMap.get("certNo"));
                if(i%batchSize==0) {
                    ps.executeBatch();
                }
                ps.addBatch();
            }
            ps.executeBatch();
            close(connection);
        }catch(Exception e){
            throw new RuntimeException("批量插入乘客信息失败{}",e);
        }
    }

}

package com.user.userimport.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String JSON_CONTENT_VALUE = "application/json;charset=UTF-8";
    private static final String FORMDATA_CONTENT_VALUE = "application/x-www-form-urlencoded";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
    private static final String UTF_8 = "utf-8";

    public static String doPost(String url)  {
        RequestConfig requestConfig = RequestConfig.
                custom().
                setConnectTimeout(4000).
                setSocketTimeout(4000).
                build();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse httpResponse = null;

        try {
            httpResponse = httpClient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
                HttpEntity httpEntity = httpResponse.getEntity();
                return EntityUtils.toString(httpEntity,"utf-8");
            }
        } catch (IOException e) {
            logger.error("request error:{}",e);
        }

        return null;

    }

    /**
     * 发送json入参的httpPost请求，对请求体进行utf-8编码
     * @param url
     * @param jsonParams
     * @return
     */
    public static String doPost(String url,String jsonParams)  {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(CONTENT_TYPE_HEADER_NAME,JSON_CONTENT_VALUE);
        CloseableHttpResponse httpResponse = null;
        try {
            //对中文进行utf-8编码处理
            StringEntity se = new StringEntity(jsonParams, Charset.forName("UTF-8"));
            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            se.setContentEncoding("UTF-8");
            httpPost.setEntity(se);
            httpResponse = httpClient.execute(httpPost);
            logger.info(httpResponse.toString());
            if(httpResponse.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
                HttpEntity httpEntity = httpResponse.getEntity();
                return EntityUtils.toString(httpEntity,UTF_8);
            }
        } catch (Exception e) {
            logger.error("request error:{}",e);
        }finally {
            if(httpClient!=null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error("关闭http连接失败");
                }
            }
        }

        return null;

    }

}

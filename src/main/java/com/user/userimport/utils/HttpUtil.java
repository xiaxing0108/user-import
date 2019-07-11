package com.user.userimport.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

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

}

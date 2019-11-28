package com.user.userimport.utils;

import constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static constant.CommonConstant.CODE_ARRAY;

@Slf4j
public class WebAppMd5 {

	private final static String key1 = "www!weixiaogu@com*wzj)code(2'chRo,$jir(Ejm9x@DVoo-wF8eRasgKT@>R$eQ32a5'9~)WsMZDJbBd_.cC;4EW2/7kUDi]@Y4mfH4__mwGW6Dd%}u>89~)w^Ax";

	private final static String key2 = "-&$,shenzhen.@^linghanglexing_com(C#PYo.pPR:/_XmsSf6FrF$tc:.VJ'i$+h.i#YxPE-593c_S,GRffP<4ckXs@Bstp[i8{iU[J24rp#e(p/x)G@kTSZ588B8$p+y";

	public static void main(String[] agr) {
		//组装请求参数调用接口获取航班信息
		/*Map paramsMap = new HashMap();
		paramsMap.put("mode","queryFlightContainBag");
		paramsMap.put("idCard","431026198306056228");
		paramsMap.put("timestamp",
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		paramsMap.put("md5", WebAppMd5.getMd5Parm(paramsMap));

		String response = HttpUtil.doPost(OutsideInterfaces.CHECK_IN_AIRLINE_QUERY, JSONObject.toJSONString(paramsMap));
        *//*if(response==null||!JSONObject.parseObject(response).containsKey("translate")) {
            throw new HttpResponseException("远端接口调用失败--返回参数格式错误或者无返回");
        }*//*
		log.info("<==返回值:"+response);*/

		String url = "http://47.107.226.75:8020/amc/app/eterm/passager/cmd/query";
		/*Map<String,String> paramsMap = new HashMap<>();
		paramsMap.put("mode","queryPrBySurname");
		paramsMap.put("flightDate","2019-11-27");
		paramsMap.put("flightNo","1152");
		paramsMap.put("carrier","HO");
		paramsMap.put("deptAirport","SZX");
		paramsMap.put("surname","WANG");
		paramsMap.put("timestamp",
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		paramsMap.put("md5",WebAppMd5.getMd5Parm(paramsMap));*/

		Map<String,String> paramsMap = new HashMap<>();
		paramsMap.put("mode","queryPrByIndex");
		paramsMap.put("index","1");
		paramsMap.put("timestamp",
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		paramsMap.put("md5",WebAppMd5.getMd5Parm(paramsMap));

		System.out.println(paramsMap.toString());




	}
	
	public static String getMd5Parm(Map<String, String> commandData) {
		List<String> lstKey = new ArrayList<>();
		lstKey.addAll(commandData.keySet());
		Collections.sort(lstKey);
		StringBuilder builder = new StringBuilder();
		builder.append(key1);
		for (String strKey : lstKey) {
			if ("md5".equals(strKey)) {
				continue;
			}
			builder.append("#").append(strKey).append("#").append(commandData.get(strKey)).append("#");
		}
		builder.append(key2);
		return encryptMd5(builder.toString(),"UTF-8").toUpperCase();
	}
	public static String encryptMd5(String pSrc, String characterSet){
		String ret = "";
		try{
			MessageDigest mdInst = MessageDigest.getInstance("MD5");

			if (isEmpty(characterSet)) {
				mdInst.update(pSrc.getBytes());
			} else {
				mdInst.update(pSrc.getBytes(characterSet));
			}
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = CommonConstant.HEX_DIGITS[byte0 >>> 4 & 0xf];
				str[k++] = CommonConstant.HEX_DIGITS[byte0 & 0xf];
			}
			ret = new String(str);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return ret;
	}

	public static boolean isEmpty(String... strValues) {
		for (String strVal : strValues) {
			if (strVal == null || "".equals(strVal)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 生成订单号，规则：PA+毫秒时间戳+六位随机字母数字组合
	 * @return
	 */
	private static synchronized String getOrderNo() {
		Random random = new Random();

		return "PA"+ System.currentTimeMillis()+
				CODE_ARRAY[random.nextInt(CODE_ARRAY.length)]+
				CODE_ARRAY[random.nextInt(CODE_ARRAY.length)]+
				CODE_ARRAY[random.nextInt(CODE_ARRAY.length)]+
				CODE_ARRAY[random.nextInt(CODE_ARRAY.length)]+
				CODE_ARRAY[random.nextInt(CODE_ARRAY.length)]+
				CODE_ARRAY[random.nextInt(CODE_ARRAY.length)];
	}
}

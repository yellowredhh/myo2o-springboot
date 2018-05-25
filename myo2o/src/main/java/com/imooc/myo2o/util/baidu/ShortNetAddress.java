package com.imooc.myo2o.util.baidu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShortNetAddress {
	private static Logger log = LoggerFactory.getLogger(ShortNetAddress.class);

	public static int TIMEOUT = 30 * 1000;
	public static String ENCODING = "UTF-8";

	/**
	 * JSON get value by key,通过传入的key获取value
	 * 
	 * @param replyText
	 * @param key
	 * @return
	 */
	private static String getValueByKey_JSON(String replyText, String key) {
		ObjectMapper mapper = new ObjectMapper();
		// 定义JSON节点
		JsonNode node;
		String tinyUrl = null;
		try {
			// 把调用返回的消息串转化为JSON对象
			node = mapper.readTree(replyText);
			// 依据key从json对象中获取对应的值
			tinyUrl = node.get(key).asText();
		} catch (JsonProcessingException e) {
			log.error("getValueByKey_JSON error:" + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("getValueByKey_JSON error:" + e.toString());
		}
		return tinyUrl;
	}

	/**
	 * 通过HttpConnection 获取返回的字符串
	 * 
	 * @param connection
	 * @return
	 * @throws IOException
	 */
	private static String getResponseStr(HttpURLConnection connection) throws IOException {
		StringBuffer result = new StringBuffer();
		// 从连接中获取http状态码
		int responseCode = connection.getResponseCode();
		// 确认是否和百度短视频接口建立了连接
		if (responseCode == HttpURLConnection.HTTP_OK) {
			// 如果返回的状态码是OK的,则连接成功,那么取出连接的输入流
			InputStream in = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, ENCODING));
			String inputLine = "";
			while ((inputLine = reader.readLine()) != null) {
				// 将消息逐行读入结果中
				result.append(inputLine);
			}
		}
		// 将结果转化成String 并返回
		return String.valueOf(result);
	}

	/**
	 * 根据传入的url,通过百度短视频的接口将其转化为短url
	 * 
	 * @param originURL
	 * @return
	 */
	public static String getShortURL(String originURL) {
		String tinyUrl = null;
		try {
			// 指定百度短视频的接口
			URL url = new URL("http://dwz.cn/create.php");
			// 建立连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// POST Request Define:
			// 设置连接参数
			// 使用连接进行输出和输入
			connection.setDoOutput(true);
			connection.setDoInput(true);
			// 不使用缓存
			connection.setUseCaches(false);
			// 设置连接超时时间为30秒
			connection.setConnectTimeout(TIMEOUT);
			// 设置请求模式为POST
			connection.setRequestMethod("POST");
			// 设置POST信息,这里为传入的原始url
			String postData = URLEncoder.encode(originURL.toString(), "utf-8");
			// 输出原始的url
			connection.getOutputStream().write(("url=" + postData).getBytes());
			// 连接百度短视频接口
			connection.connect();
			// 获取返回的字符串
			String responseStr = getResponseStr(connection);
			log.info("response string: " + responseStr);
			// 获取到转化后的短的url
			tinyUrl = getValueByKey_JSON(responseStr, "tinyurl");
			log.info("tinyurl: " + tinyUrl);
			// 关闭连接
			connection.disconnect();
		} catch (IOException e) {
			log.error("getshortURL error:" + e.toString());
		}
		return tinyUrl;

	}

	/**
	 * ‘ 百度短链接接口 无法处理不知名网站，会安全识别报错
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		getShortURL(
				"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx2c605206217d88b5&redirect_uri=http://115.28.159.6/cityrun/wechatlogin.action&role_type=1&response_type=code&scope=snsapi_userinfo&state=STATE123qweasd#wechat_redirect");
	}
}

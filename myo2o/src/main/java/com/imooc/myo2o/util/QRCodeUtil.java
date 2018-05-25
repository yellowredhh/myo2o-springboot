package com.imooc.myo2o.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 生成二维码的图片流
 * 
 * @author hh
 * 
 */
public class QRCodeUtil {
	public static BitMatrix generateQRCodeStream(String url, HttpServletResponse resp) {
		// 给响应添加头部信息,告诉浏览器返回的是图片流
		resp.setHeader("Cache-Control", "no-store");
		// 不要缓存二维码
		resp.setHeader("Pragma", "no-cache");
		resp.setDateHeader("Expires", 0);
		resp.setContentType("image/png");
		// 设置图片的文字编码以及内边框距
		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 0);
		BitMatrix bitMatrix;
		try {
			// 参数分别为编码内容,编码类型,生成图片宽度,高度,设置参数
			bitMatrix = new MultiFormatWriter().encode("https://www.baidu.com", BarcodeFormat.QR_CODE, 300, 300, hints);
		} catch (WriterException e) {
			e.printStackTrace();
			return null;
		}
		return bitMatrix;
	}
}

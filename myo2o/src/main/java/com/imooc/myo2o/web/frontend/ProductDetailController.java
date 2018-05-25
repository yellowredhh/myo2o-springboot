package com.imooc.myo2o.web.frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.entity.Product;
import com.imooc.myo2o.service.ProductService;
import com.imooc.myo2o.util.HttpServletRequestUtil;
import com.imooc.myo2o.util.QRCodeUtil;
import com.imooc.myo2o.util.baidu.ShortNetAddress;

@Controller
@RequestMapping("/frontend")
public class ProductDetailController {
	@Autowired
	private ProductService productService;

	// 二维码扫描的真正的url(很长,后面会用到转换为短url的方法)

	private static String URLPREFIX = "https://open.weixin.qq.com/connect/oauth2/authorize?"
			+ "appid=wxd7f6c5b8899fba83&" + "redirect_uri=115.28.159.6/myo2o/shop/adduserproductmap&"
			+ "response_type=code&scope=snsapi_userinfo&state=";
	private static String URLSUFFIX = "#wechat_redirect";

	/**
	 * 根据productId获取product的信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listproductdetailpageinfo", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listProductDetailPageInfo(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 获取商品id
		long productId = HttpServletRequestUtil.getLong(request, "productId");
		Product product = null;
		if (productId != -1) {
			product = productService.getProductByProductId(productId);
			// 获取顾客信息
			PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
			if (user == null) {
				// 如果没登录,则不需要二维码(不显示二维码)
				modelMap.put("needQRCode", false);
			} else {
				// 如果当前用户登录了,就需要二维码(显示二维码)
				modelMap.put("needQRCode", true);
			}
			modelMap.put("product", product);
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty productId");
		}
		return modelMap;
	}

	/**
	 * 为商品生成而二维码
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/generateqrcode4product", method = RequestMethod.GET)
	@ResponseBody
	private void generateQRCode4Product(HttpServletRequest request, HttpServletResponse response) {
		long productId = HttpServletRequestUtil.getLong(request, "productId");
		// 获取顾客信息
		PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
		if (productId != -1 && user != null && user.getUserId() != null) {
			// 获取当前时间戳,保证二维码的有效性
			long timpStamp = System.currentTimeMillis();
			String content = "{\"productId\":" + productId + ",\"customerId\":" + user.getUserId() + ",\"createTime\":"
					+ timpStamp + "}";
			String longUrl = URLPREFIX + content + URLSUFFIX;
			// 调用短url生成方法将长url转化为短url(因为二维码要求的url比较短)
			String shortUrl = ShortNetAddress.getShortURL(longUrl);
			// 传入短url生成二维码图像
			BitMatrix qRcodeImg = QRCodeUtil.generateQRCodeStream(shortUrl, response);
			try {
				// 将二维码以图片流的形式输出到前端
				MatrixToImageWriter.writeToStream(qRcodeImg, "png", response.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

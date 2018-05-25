package com.imooc.myo2o.web.shop;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.myo2o.dto.EchartSeries;
import com.imooc.myo2o.dto.EchartXAxis;
import com.imooc.myo2o.dto.ShopAuthMapExecution;
import com.imooc.myo2o.dto.UserProductMapExecution;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.entity.Product;
import com.imooc.myo2o.entity.ProductSellDaily;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.entity.ShopAuthMap;
import com.imooc.myo2o.entity.UserProductMap;
import com.imooc.myo2o.enums.UserProductMapStateEnum;
import com.imooc.myo2o.service.PersonInfoService;
import com.imooc.myo2o.service.ProductSellDailyService;
import com.imooc.myo2o.service.ProductService;
import com.imooc.myo2o.service.ShopAuthMapService;
import com.imooc.myo2o.service.UserProductMapService;
import com.imooc.myo2o.util.HttpServletRequestUtil;
import com.imooc.myo2o.util.weixin.message.req.WechatInfo;

/**
 * 
 * 这个listproductselldailyinfobyshop方法返回的xDate的时间是乱序的,我在dao底层是用了order by
 * create_time的,但是就是乱序,未知bug(你可以查下console中打印出来的sql语句和sql结果都是符合预期的,但是这个时间乱序...唉)
 * 
 * 另外这个listuserproductmapsbyshop方法有一个小问题,返回的时间数据在前端不能转化为我想要的时间格式,
 * 即使我在前端js拼接中加入new
 * Date(item.createTime).Format("yyyy-MM-dd")这种形式也不行(好了问题解决了,是忘记引入commonutil.js了,所以没有时间函数)
 * 
 * @author hh
 *
 */
@Controller
@RequestMapping("/shop")
public class UserProductManagementController {
	@Autowired
	private UserProductMapService userProductMapService;
	@Autowired
	private PersonInfoService personInfoService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ShopAuthMapService shopAuthMapService;
	@Autowired
	private ProductSellDailyService productSellDailyService;

	/**
	 * 查询某一个商铺的商品的7天的销量
	 * 要知道这个productSellDailyService.listProductSellDaily(productSellDailyCondition,
	 * beginTime, endTime) 方法中有一段sql如下 order by
	 * psd.product_id,psd.create_time,这样理解for循环就会容易多了
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listproductselldailyinfobyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listProductSellDailyInfobyShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 从session中获取当前店铺的信息
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		if (currentShop != null && currentShop.getShopId() != null) {
			// 添加查询条件
			ProductSellDaily productSellDailyCondition = new ProductSellDaily();
			productSellDailyCondition.setShop(currentShop);
			Calendar calendar = Calendar.getInstance();
			// 获取昨天的日期(因为quartz是每天0点去统计销量,所以今天的销量是无法统计到的)
			calendar.add(Calendar.DATE, -1);
			Date endTime = calendar.getTime();
			// 获取七天之前的日期:由于之前已经减去了一天,所以这里再减去六天,获取到的是七天之前的日期(因为我们要做的是查询最近一周的销量)
			calendar.add(Calendar.DATE, -6);
			Date beginTime = calendar.getTime();
			// 传入查询条件进行销量查询
			List<ProductSellDaily> productSellDailyList = productSellDailyService
					.listProductSellDaily(productSellDailyCondition, beginTime, endTime);
			// 定义日期格式
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// 商品名列表,使用hashset进行去重以保证名字唯一
			HashSet<String> legendData = new HashSet<String>();
			// x轴数据(前端页面中就是日期)
			HashSet<String> xData = new HashSet<String>();
			// 定义series(表示的就是所有的商品的7天的销量统计)
			List<EchartSeries> series = new ArrayList<EchartSeries>();
			// 日销量列表(每天的销量都是这个list中的一个元素)
			List<Integer> totalList = new ArrayList<Integer>();
			// 当前商品名,默认为空
			String currentProductName = "";
			for (int i = 0; i < productSellDailyList.size(); i++) {
				ProductSellDaily productSellDaily = productSellDailyList.get(i);
				// 添加商品名(hashset的,所以自动去重)
				legendData.add(productSellDaily.getProduct().getProductName());
				// 添加x轴数据
				xData.add(sdf.format(productSellDaily.getCreateTime()));
				if (!currentProductName.equals(productSellDaily.getProduct().getProductName())
						&& !currentProductName.isEmpty()) {
					// 如果前一个商品名字(就是这里的currentProductName,:))和正在遍历的商品名字不同,
					// 并且前一个商品名字不为空(也就避免了第一次遍历进入这个循环),
					// 也就是说遍历到了下一个商品了,则将前一轮遍历的信息加入到series中
					// 包括商品名以及与商品对应的统计日期以及当你销量

					// 拼接series
					EchartSeries es = new EchartSeries();
					// name不是引用变量,不需要克隆
					es.setName(currentProductName);
					// 由于后面要进行totalList的重置,而totalList又是引用对象,所以这里传入的是一个totalList的副本数据,
					// 如果直接写totalList,那么后面重置totalList的时候就会把这个es对象的Data属性也重置掉
					es.setData(totalList.subList(0, totalList.size()));
					series.add(es);
					// 至此算是完成了一次大的循环,这个大循环是从下面的else开始的
					// (也就是把一种product的7天的销量都统计完了,因为在listProductSellDaily这个方法的查询sql结果是按照
					// order by psd.product_id,psd.create_time排序的 )

					// 重置totalList,准备统计另一个product的销量(如果还有的话,这里既然进入了循环,那就肯定是还有的)
					totalList = new ArrayList<Integer>();
					// 将另一种product的名字设置为currentProductName;
					currentProductName = productSellDaily.getProduct().getProductName();
					// 统计另一种product的销量
					totalList.add(productSellDaily.getTotal());
				} else {
					// 如果还是当前的product,则继续统计销量
					// 由于currentProductName默认为空,所以第一次遍历肯定是直接进入到else
					totalList.add(productSellDaily.getTotal());
					currentProductName = productSellDaily.getProduct().getProductName();
				}
				// 最后一个遍历的商品的信息也需要添加进去(因为上面的逻辑中没有对最后一个商品的统计信息进行添加,只是进行了统计)
				if (i == productSellDailyList.size() - 1) {
					// 拼接series
					EchartSeries es = new EchartSeries();
					es.setName(currentProductName);
					// 其实这里可以不用克隆了,但是为了防止以后还要添加逻辑还会更改totalList的值,所以还是用克隆吧
					es.setData(totalList.subList(0, totalList.size()));
					series.add(es);
				}
			}
			modelMap.put("series", series);
			modelMap.put("legendData", legendData);
			// 拼接xAxis
			List<EchartXAxis> xAxis = new ArrayList<EchartXAxis>();
			EchartXAxis exa = new EchartXAxis();
			exa.setData(xData);
			xAxis.add(exa);
			modelMap.put("xAxis", xAxis);
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("error", "empty shopId");
		}
		return modelMap;
	}

	/**
	 * 根据店铺去查询某一个特定店铺的销售记录
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listuserproductmapsbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listUserProductMapsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 获取分页信息
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		// 获取当前店铺信息
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		// 空值判定,主要确保shopId不为空
		if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null) && (currentShop.getShopId() != null)) {
			UserProductMap userProductMapCondition = new UserProductMap();
			userProductMapCondition.setShopId(currentShop.getShopId());
			// 获取用于模糊查询的商品名称
			String productName = HttpServletRequestUtil.getString(request, "productName");
			if (productName != null) {
				userProductMapCondition.setProductName(productName);
			}
			// 根据传入的查询条件进行该商铺销售情况的高级查询和分页查询
			UserProductMapExecution ue = userProductMapService.listUserProductMap(userProductMapCondition, pageIndex,
					pageSize);
			modelMap.put("userProductMapList", ue.getUserProductMapList());
			modelMap.put("count", ue.getCount());
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
		}
		return modelMap;
	}

	/**
	 * 添加消费记录
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/adduserproductmap", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> addUserProductMap(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
		// 获取二维码里面state携带的content信息
		String qrCodeinfo = HttpServletRequestUtil.getString(request, "state");
		ObjectMapper mapper = new ObjectMapper();
		WechatInfo wechatInfo = null;
		try {
			wechatInfo = mapper.readValue(qrCodeinfo, WechatInfo.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		// 校验二维码是否过期
		if (!checkQRCodeInfo(wechatInfo)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "二维码信息非法！");
			return modelMap;
		}
		// 获取添加消费记录所需要的参数并将其组成userproductmap实例
		Long productId = wechatInfo.getProductId();
		Long customerId = wechatInfo.getCustomerId();
		UserProductMap userProductMap = compactUserProductMap4Add(customerId, productId);
		// 空值校验
		if (userProductMap != null && customerId != -1) {
			try {
				// 权限检查
				if (!checkShopAuth(user.getUserId(), userProductMap)) {
					modelMap.put("success", false);
					modelMap.put("errMsg", "无操作权限");
					return modelMap;
				}
				// 添加消费记录
				UserProductMapExecution se = userProductMapService.addUserProductMap(userProductMap);
				if (se.getState() == UserProductMapStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入授权信息");
		}
		return modelMap;
	}

	/**
	 * 二维码检查
	 * 
	 * @param wechatInfo
	 * @return
	 */
	private boolean checkQRCodeInfo(WechatInfo wechatInfo) {
		if (wechatInfo != null && wechatInfo.getProductId() != null && wechatInfo.getCustomerId() != null
				&& wechatInfo.getCreateTime() != null) {
			// 获取当前时间戳
			long nowTime = System.currentTimeMillis();
			// 如果二维码距离生成时间小于5秒则有效
			if ((nowTime - wechatInfo.getCreateTime()) <= 5000) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 拼接UserProductMap条件
	 * 
	 * @param customerId
	 * @param productId
	 * @return
	 */
	private UserProductMap compactUserProductMap4Add(Long customerId, Long productId) {
		UserProductMap userProductMap = null;
		if (customerId != null && productId != null) {
			userProductMap = new UserProductMap();
			PersonInfo personInfo = personInfoService.getPersonInfoById(customerId);
			// 主要为了获取到商品积分
			Product product = productService.getProductByProductId(productId);
			userProductMap.setProductId(productId);
			userProductMap.setShopId(product.getShop().getShopId());
			userProductMap.setProductName(product.getProductName());
			userProductMap.setUserName(personInfo.getName());
			// 获取到商品积分
			userProductMap.setPoint(product.getPoint());
			userProductMap.setCreateTime(new Date());
		}
		return userProductMap;
	}

	/**
	 * 权限检查
	 * 
	 * @param userId
	 * @param userProductMap
	 * @return
	 */
	private boolean checkShopAuth(long userId, UserProductMap userProductMap) {
		ShopAuthMapExecution shopAuthMapExecution = shopAuthMapService
				.listShopAuthMapByShopId(userProductMap.getShopId(), 1, 1000);
		for (ShopAuthMap shopAuthMap : shopAuthMapExecution.getShopAuthMapList()) {
			if (shopAuthMap.getEmployeeId() == userId) {
				return true;
			}
		}
		return false;
	}
}

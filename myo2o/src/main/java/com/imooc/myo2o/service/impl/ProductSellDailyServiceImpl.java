package com.imooc.myo2o.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.myo2o.dao.ProductSellDailyDao;
import com.imooc.myo2o.entity.ProductSellDaily;
import com.imooc.myo2o.service.ProductSellDailyService;

/**
 * 之前QuartzConfiguration中
 * jobDetailFactoryBean.setTargetBeanName("productSellDailyService");这里的@service弄了一个beanname,不然在Quartz中会报Field
 * jobDetailFactory in com.imooc.myo2o.config.quartz.QuartzConfiguration
 * required a bean named 'ProductSellDailyService' that could not be found.
 * 
 * Action:
 * 
 * Consider defining a bean named 'ProductSellDailyService' in your
 * configuration. 后来我把QuartzConfiguration中的代码改成了
 * jobDetailFactoryBean.setTargetBeanName("productSellDailyServiceImpl"),因为spring默认的beanname即使类名首字母小写,所以不需要在@Servcie中加value="";
 * 
 * @author hh
 *
 */
@Service
public class ProductSellDailyServiceImpl implements ProductSellDailyService {

	@Autowired
	private ProductSellDailyDao productSellDailyDao;

	private final static Logger logger = LoggerFactory.getLogger(ProductSellDailyServiceImpl.class);

	@Override
	public void dailyCalculate() {
		logger.info("===================Quartz Running!=========================");
		productSellDailyDao.insertProductSellDaily();
		productSellDailyDao.insertDefaultProductSellDaily();
		// System.out.println("quartz 运行中");
	}

	@Override
	public List<ProductSellDaily> listProductSellDaily(ProductSellDaily productSellDailyCondition, Date beginTime,
			Date endTime) {
		List<ProductSellDaily> productSellDailyList = productSellDailyDao
				.queryProductSellDailyList(productSellDailyCondition, beginTime, endTime);
		return productSellDailyList;
	}

}

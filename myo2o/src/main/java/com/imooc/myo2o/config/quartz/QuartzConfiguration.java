package com.imooc.myo2o.config.quartz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfiguration {
	@Autowired
	private MethodInvokingJobDetailFactoryBean jobDetailFactory;

	// @Autowired
	// private ProductSellDailyService productSellDailyService;

	@Autowired
	private CronTriggerFactoryBean productSellDailyTriggerFactory;

	/**
	 * 创建jobDetail并返回
	 * 
	 * @return
	 */
	@Bean(name = "jobDetailFactory")
	public MethodInvokingJobDetailFactoryBean createMethodInvokingJobDetailFactoryBean() {
		// new出jobDetailFactory对象,用于生产jobDetail;
		MethodInvokingJobDetailFactoryBean jobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
		// 设置jobDetail的名字
		jobDetailFactoryBean.setName("product_sell_daily_job");
		// 设置jobDetail的组名
		jobDetailFactoryBean.setGroup("job_product_sell_daily_group");
		// 不允许多个定时任务并发执行
		jobDetailFactoryBean.setConcurrent(false);
		// 指定运行任务的类(这里要指定实现类,Quartz会自动到IOC容器中寻找名为productSellDailyServiceImpl的bean)
		jobDetailFactoryBean.setTargetBeanName("productSellDailyServiceImpl");
		// 我用的是spring 5.0版本,估计是spring版本太高,所以这里不能使用setTargetObject或者setTargetClass的形式引入类了
		// 这里有个问题,按照博客上的写法,理论上来说只要有setTargetObject或者setTargetClass之中的一个就可以正常运行的,但是我这里两者都要才能启动,而且并没有达到我想要的效果(虽然启动了但是还是没法运行任务)
		// jobDetailFactoryBean.setTargetClass(ProductSellDailyService.class);
		// jobDetailFactoryBean.setTargetObject(productSellDailyService);
		// 指定运行任务的方法
		jobDetailFactoryBean.setTargetMethod("dailyCalculate");
		return jobDetailFactoryBean;
	}

	/**
	 * 创建cronTrigger并返回
	 * 
	 * @return
	 */
	@Bean(name = "productSellDailyTriggerFactory")
	public CronTriggerFactoryBean createCronTriggerFactoryBean() {
		// 创建trigger
		CronTriggerFactoryBean triggerFactory = new CronTriggerFactoryBean();
		// 设置trigger的名字
		triggerFactory.setName("product_sell_daily_trigger");
		// 设置tirgger的组名,没必要和jobDetail的一样
		triggerFactory.setGroup("job_product_sell_daily_group");
		// 绑定jobDetail
		triggerFactory.setJobDetail(jobDetailFactory.getObject());
		// "0/3 * * * * ? *" 每三秒执行一次
		// 定时任务表达式:秒,分,小时,日,月,周,年;
		// 其中年可以省略,0/3表示从第0秒开始,每3秒执行一次,如果写成30/3就是从30秒后开始,每3秒执行一次
		// "0 0 0 * * ? *" 每天凌晨12点(就是0点)执行一次,*表示每的意思,
		triggerFactory.setCronExpression("0 0 0 * * ? *");
		return triggerFactory;
	}

	/**
	 * 创建调度工厂并返回
	 * 
	 * @return
	 */
	@Bean(name = "schedulerFactory")
	public SchedulerFactoryBean createSchedulerFactoryBean() {
		SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
		// 调度工厂绑定trigger(可以绑定多个,参数是一个可变数组)
		schedulerFactory.setTriggers(productSellDailyTriggerFactory.getObject());
		return schedulerFactory;
	}
}

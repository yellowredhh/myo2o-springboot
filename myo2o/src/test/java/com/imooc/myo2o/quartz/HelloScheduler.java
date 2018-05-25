package com.imooc.myo2o.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class HelloScheduler {
	public static void main(String[] args) throws SchedulerException {
		// 创建jobDetail实例
		JobDetail jobDetail = JobBuilder.newJob(HelloJob.class).withIdentity("myJob", "group1").build();
		// 创建tigger实例,定义该job立即执行,并且每隔两秒执行一次
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("myTrigger", "groupl").startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2).repeatForever()).build();
		// 创建scheduler实例
		SchedulerFactory sfact = new StdSchedulerFactory();
		Scheduler scheduler = sfact.getScheduler();
		scheduler.start();
		// 当前时间
		Date date = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
		System.out.println("Current Exec Time Is:" + sf.format(date));
		// 绑定jobDetail和trigger
		scheduler.scheduleJob(jobDetail, trigger);
	}
}

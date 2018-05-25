package com.imooc.myo2o.dto;

import java.util.HashSet;

/**
 * 迎合销量统计柱状图中的横坐标
 * 
 * @author hh
 *
 */
public class EchartXAxis {
	// 固定值只需要一个getter方法
	private String type = "category";
	// 为了去重使用了HashSet
	private HashSet<String> data;

	public HashSet<String> getData() {
		return data;
	}

	public void setData(HashSet<String> data) {
		this.data = data;
	}

	public String getType() {
		return type;
	}

}

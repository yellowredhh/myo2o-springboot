package com.imooc.myo2o.dto;

import java.util.List;

/**
 * 迎合销量统计柱状图中的series项
 * 
 * @author hh
 *
 */
public class EchartSeries {
	private String name;
	// 固定值只需要一个getter方法
	private String type = "bar";
	private List<Integer> data;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Integer> getData() {
		return data;
	}

	public void setData(List<Integer> data) {
		this.data = data;
	}

	public String getType() {
		return type;
	}

}

package com.imooc.myo2o.util;

public class PageCalculator {
	/*
	 * 由于数据库只认识行数,而前端只认识页数,所以需要一个行页转换方法.
	 */
	public static int calculateRowIndex(int pageIndex, int pageSize) {
		return pageIndex > 0 ? (pageIndex - 1) * pageSize : 0;
	}
}

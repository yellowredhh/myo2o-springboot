package com.imooc.myo2o.Exceptions;

/*
 * 必须继承RuntimeException,如果继承的是Exception,当已经进行了DML操作但是事务还未关闭时,此时如果抛出异常,则DML不会回滚.
 * 当然如果硬要继承Exception并且还想回滚事务可以通过在@Transactional注解指定rollbackFor=Exception.class来解决.
 */
public class ProductExecutionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3936557078550899764L;

	public ProductExecutionException(String msg) {
		super(msg);
	}
}

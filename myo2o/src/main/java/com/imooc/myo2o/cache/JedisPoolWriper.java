package com.imooc.myo2o.cache;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 强指定redis的JedisPool接口构造函数，这样才能在centos成功创建jedispool
 * 
 * @author 
 *
 */
public class JedisPoolWriper {
	//redis连接池对象
	private JedisPool jedisPool;
	
	/**
	 * 构造器
	 * @param poolConfig	连接池配置相关信息
	 * @param host			redis数据库位于哪个主机ip
	 * @param port			访问redis数据库的端口
	 */
	public JedisPoolWriper(final JedisPoolConfig poolConfig, final String host, final int port) {
		try {
			jedisPool = new JedisPool(poolConfig, host, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

}

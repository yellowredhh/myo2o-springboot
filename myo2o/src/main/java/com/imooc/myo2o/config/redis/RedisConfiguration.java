package com.imooc.myo2o.config.redis;

import com.imooc.myo2o.cache.JedisPoolWriper;
import com.imooc.myo2o.cache.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfiguration {
    //导入application.properties中的redis的属性
    @Value("${redis.hostname}")
    private String hostname;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.pool.maxActive}")
    private int maxTotal;
    @Value("${redis.pool.maxIdle}")
    private int maxIdle;
    @Value("${redis.pool.maxWait}")
    private long maxWaitMills;
    @Value("${redis.pool.testOnBorrow}")
    private boolean testOnBorrow;

    @Autowired
    private JedisPoolConfig jedisPoolConfig;
    @Autowired
    private JedisPoolWriper jedisWritePool;
    @Autowired
    private JedisUtil jedisUtil;

    /**
     * 配置jedis连接池
     * @return
     */
    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig =new JedisPoolConfig();
        //控制一个pool可以连接多少个jedis实例
        jedisPoolConfig.setMaxTotal(maxTotal);
        //控制连接池的最大空闲连接数
        jedisPoolConfig.setMaxIdle(maxIdle);
        //最大等待时间:当连接池中没有可用连接时,连接池等待连接被归还的最大时间,超过时间则抛出异常
        jedisPoolConfig.setMaxWaitMillis(maxWaitMills);
        //在获取连接的时候检查有效性
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        return jedisPoolConfig;
    }

    /**
     *
     * @return
     */
    @Bean(name = "jedisWritePool")
    public JedisPoolWriper getJedisPoolWriper() {
        return new JedisPoolWriper(jedisPoolConfig,hostname,port);
    }

    /**
     * 创建jedis工具类,对redis操作进行封装
     * @return
     */
    @Bean(name = "jedisUtil")
    public JedisUtil getJedisUtil() {
        JedisUtil jedisUtil = new JedisUtil();
        jedisUtil.setJedisPool(jedisWritePool);
        return jedisUtil;
    }

    //创建redis工具类中封装的内部类

    @Bean(name = "jedisKeys")
    public JedisUtil.Keys createJedisKeys(){
        JedisUtil.Keys jedisKeys = jedisUtil.new Keys();
        return jedisKeys;
    }

    @Bean(name = "jedisStrings")
    public JedisUtil.Strings createJedisStrings(){
        JedisUtil.Strings jedisStrings = jedisUtil.new Strings();
        return jedisStrings;
    }

    @Bean(name = "jedisHash")
    public JedisUtil.Hash createJedisHash(){
        JedisUtil.Hash jedisHash = jedisUtil.new Hash();
        return jedisHash;
    }

    @Bean(name = "jedisLists")
    public JedisUtil.Lists createJedisLists(){
        JedisUtil.Lists jedisLists = jedisUtil.new Lists();
        return jedisLists;
    }

    @Bean(name = "jedisSets")
    public JedisUtil.Sets createJedisSets(){
        JedisUtil.Sets jedisSets = jedisUtil.new Sets();
        return jedisSets;
    }

}

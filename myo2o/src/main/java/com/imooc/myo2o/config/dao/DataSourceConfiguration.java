package com.imooc.myo2o.config.dao;

import java.beans.PropertyVetoException;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.imooc.myo2o.util.DESUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 配置dataSource到IOC容器中去
 */
@Configuration
//配置mybait mapper的扫描路径
@MapperScan("com.imooc.myo2o.dao")
public class DataSourceConfiguration {
    //从application.properties文件中读取对应的键值信息
    @Value("${jdbc.driver}")
    private String JdbcDriver;
    @Value("${jdbc.url}")
    private String JdbcUrl;
    @Value("${jdbc.username}")
    private String JdbcUsername;
    @Value("${jdbc.password}")
    private String JdbcPassword;

    /**
     * 生成与spring-dao.xml对应的bean dataSource
     * @return
     * @throws PropertyVetoException
     */
    @Bean(name="dataSource")
    public ComboPooledDataSource createDataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        //驱动
        dataSource.setDriverClass(JdbcDriver);
        //数据库url
        dataSource.setJdbcUrl(JdbcUrl);
        //数据库账号名
        dataSource.setUser(DESUtils.getDecryptString(JdbcUsername));
        //数据库账号的密码
        dataSource.setPassword(DESUtils.getDecryptString(JdbcPassword));

        //c3p0连接池的一些其他属性
        //连接池最大,最小连接数
        dataSource.setMaxPoolSize(30);
        dataSource.setMinPoolSize(10);
        //关闭连接后不自动commit
        dataSource.setAutoCommitOnClose(false);
        //获取连接超时时间
        dataSource.setCheckoutTimeout(10000);
        //当获取连接失败时重试次数
        dataSource.setAcquireRetryAttempts(2);
        return dataSource;
    }
}

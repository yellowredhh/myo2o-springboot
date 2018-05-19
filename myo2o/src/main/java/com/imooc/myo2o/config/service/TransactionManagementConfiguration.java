package com.imooc.myo2o.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
@Configuration
//使用了这个注解后就可以在service层开始@Transactional
@EnableTransactionManagement
public class TransactionManagementConfiguration implements TransactionManagementConfigurer{

    //有多个dataSource,用这个注解来限定是用哪一个dataSource
    @Qualifier("dataSource")
    //自动注入DataSourceConfiguration创建的dataSource
    @Autowired
    private DataSource dataSource;

    /**
     * 由于DataSourceTransactionManager是继承自AbstractPlatformTransactionManager,而后者又实现了PlatformTransactionManager,所以可以直接返回DataSourceTransactionManager
     * @return
     */
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
}

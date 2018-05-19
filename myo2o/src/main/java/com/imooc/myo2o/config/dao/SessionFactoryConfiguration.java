package com.imooc.myo2o.config.dao;

import java.io.IOException;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@Configuration
public class SessionFactoryConfiguration {

    @Autowired
    private DataSource dataSource;

    //静态字段的值必须使用setter方法导入
    private static String mybatisConfigFileLocation;
    @Value("${mybatis_config_file}")
    public  void setMybatisConfigFileLocation(String mybatisConfigFileLocation) {
        SessionFactoryConfiguration.mybatisConfigFileLocation = mybatisConfigFileLocation;
    }

    private static String mapperPath;
    @Value("${mapper_path}")
    public  void setMapperPath(String mapperPath) {
        SessionFactoryConfiguration.mapperPath = mapperPath;
    }

    @Value("${type_alias_package}")
    private String typeAliasPackage;


    /**
     * 创建sqlSessionFactory实例,并且设置Configuration,设置mapper映射路径,设置dataSource数据库源
     *
     * @return
     * @throws IOException
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean createSqlSessionFactory() throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        //设置mybatis configuration 扫描路径
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(mybatisConfigFileLocation));
        //添加mapper扫描路径
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + mapperPath;
        sqlSessionFactoryBean.setMapperLocations(pathMatchingResourcePatternResolver.getResources(packageSearchPath));
        //设置dataSource
        sqlSessionFactoryBean.setDataSource(dataSource);
        //设置typeAlias包扫描路径
        sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasPackage);
        return sqlSessionFactoryBean;
    }
}

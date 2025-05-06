package com.example.announcement.config;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration // 表示這是一個配置類，等同於 Spring 的 XML 配置文件。Spring 容器會在啟動時加載這個類，並應用其中的配置。
@EnableTransactionManagement // 啟用注解驅動的事務管理功能。這樣可以使用 @Transactional 注解來管理數據庫事務。
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/announcement?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("springboot");
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.example.announcement.model");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "update"); // 自動更新表結構
        properties.put("hibernate.connection.characterEncoding", "UTF-8");
        return properties;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }

    @Bean
    public ServletContextListener servletContextListener() {
        return new ServletContextListener() {
            @Override
            public void contextInitialized(ServletContextEvent sce) {
                // 可以在此放置初始化邏輯
                System.out.println("Application started.");
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                // 清理 JDBC 驅動
                Enumeration<java.sql.Driver> drivers = java.sql.DriverManager.getDrivers();
                while (drivers.hasMoreElements()) {
                    java.sql.Driver driver = drivers.nextElement();
                    try {
                        java.sql.DriverManager.deregisterDriver(driver);
                        System.out.println("Deregistered JDBC driver: " + driver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // 停止 AbandonedConnectionCleanupThread
                try {
                    com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
                    System.out.println("AbandonedConnectionCleanupThread has been shut down.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
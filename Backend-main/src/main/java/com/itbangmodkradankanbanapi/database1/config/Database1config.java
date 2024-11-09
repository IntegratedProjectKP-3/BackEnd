package com.itbangmodkradankanbanapi.database1.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "Database1EntityManagerFactory",
        transactionManagerRef = "Database1TransactionManager",
        basePackages = {"com.itbangmodkradankanbanapi.database1.repositories"})


public class Database1config {

    //@Primary
//@Bean(name = "Database1DataSource")
//public DataSource Database1DataSource(
//        @Value("${mysql_url:mysql}") String mysqlUrl,
//        @Value("${mysql_user:root}") String mysqlUser,
//        @Value("${mysql_password:mysql@sit}") String mysqlPassword) {
//    return DataSourceBuilder.create()
//            .url("jdbc:mysql://" + mysqlUrl + ":3306/integratedproject")
//            .username(mysqlUser)
//            .password(mysqlPassword)
//            .driverClassName("com.mysql.cj.jdbc.Driver")
//            .build();
//}

    @Primary
    @Bean(name = "Database1DataSource")
    public DataSource Database1DataSource() {
        return DataSourceBuilder.create() .url("jdbc:mysql://localhost:3306/integratedproject") .username("root") .password("mysql@sit") .driverClassName("com.mysql.cj.jdbc.Driver") .build();
    }

    @Primary
    @Bean (name="Database1EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean Database1EntityManagerFactory(
            @Qualifier("Database1DataSource") DataSource Database1DataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(Database1DataSource)
                .packages("com.itbangmodkradankanbanapi.database1.entities")
                .persistenceUnit("Database1")
                .build();
    }

    @Primary
    @Bean (name = "Database1TransactionManager")
    public PlatformTransactionManager Database1TransactionManager(@Qualifier("Database1EntityManagerFactory") EntityManagerFactory Database1EntityManagerFactory) {
        return new JpaTransactionManager(Database1EntityManagerFactory);
    }





}

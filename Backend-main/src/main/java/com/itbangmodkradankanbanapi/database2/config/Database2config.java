package com.itbangmodkradankanbanapi.database2.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
@EnableJpaRepositories(entityManagerFactoryRef = "Database2EntityManagerFactory",
        transactionManagerRef = "Database2TransactionManager",
        basePackages = {"com.itbangmodkradankanbanapi.database2.repositories"})


public class Database2config {

//    @Primary
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSourceProperties Database2DataSourceProperties() {
//        return new DataSourceProperties();
//    }

//
    @Bean(name = "Database2DataSource")
    public DataSource Database2DataSource() {
       return DataSourceBuilder.create()
               .url("jdbc:mysql://ip23ft.sit.kmutt.ac.th:3306/itbkk_shared")
               .driverClassName("com.mysql.cj.jdbc.Driver")
               .username("authuser")
               .password("VT4eTSRo")
               .build();

        // return DataSourceBuilder.create()
        //         .url("jdbc:mysql://${MYSQL_HOST_2:ip23ft.sit.kmutt.ac.th}:3306/itbkk_shared")
        //         .driverClassName("com.mysql.cj.jdbc.Driver")
        //         .username("${MYSQL_USER_2:authuser}")
        //         .password("${MYSQL_PASSWORD_2:VT4eTSRo}")
        //         .build();
    }


    @Bean (name="Database2EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean Database2EntityManagerFactory(
            @Qualifier("Database2DataSource") DataSource Database2DataSource,
            EntityManagerFactoryBuilder builder) {

        return builder
                .dataSource(Database2DataSource)
                .packages("com.itbangmodkradankanbanapi.database2.entities")
                .persistenceUnit("Database2")
                .build();
    }


    @Bean(name = "Database2TransactionManager")
    public PlatformTransactionManager Database2TransactionManager(@Qualifier("Database2EntityManagerFactory") EntityManagerFactory Database2EntityManagerFactory) {
        return new JpaTransactionManager(Database2EntityManagerFactory);
    }
}

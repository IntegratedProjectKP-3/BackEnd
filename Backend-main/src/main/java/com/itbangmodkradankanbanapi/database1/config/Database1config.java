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
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory1",
        transactionManagerRef = "Database1TransactionManager",
        basePackages = {"com.itbangmodkradankanbanapi.database1.repositories"})

public class Database1config {
    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties Database1DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource Database1DataSource() {
        return DataSourceBuilder.create() .url("jdbc:mysql://localhost:3306/integratedproject_v2") .username("root") .password("mysql@sit") .build();
    }

    @Primary
    @Bean (name="entityManagerFactory1")
    public LocalContainerEntityManagerFactoryBean Database1EntityManagerFactory(@Qualifier("Database1DataSource") DataSource Database1DataSource, EntityManagerFactoryBuilder builder) {
        return builder.dataSource(Database1DataSource)
                .packages("com.itbangmodkradankanbanapi.database1.entities")
                .persistenceUnit("Database1")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager Database1TransactionManager(@Qualifier("entityManagerFactory1") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}

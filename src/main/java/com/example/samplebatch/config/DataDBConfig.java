package com.example.samplebatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories( // JPA를 사용해서 특정한 패키지 엔티티에 값을 부여
        basePackages = "com.example.samplebatch.repository",    // DataDBConfig가 어떤 패키지에서 동작하게 할거냐 세팅
        entityManagerFactoryRef = "dataEntityManager",          // dataEntityManager -> 우리가 작성할 메서드 명
        transactionManagerRef = "dataTransactionManager"        // dataTransactionManager -> 우리가 작성할 메서드 명
)
public class DataDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-data")
    public DataSource dataDBSource() {

        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean dataEntityManager() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(dataDBSource());
        em.setPackagesToScan(new String[]{"com.example.samplebatch.entity"});   // 엔티티들이 모여질 패키지를 등록
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");     // 2개의 DB를 연결시키기 위해 ddl 설정
        properties.put("hibernate.show_sql", "true");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager dataTransactionManager() {

        JpaTransactionManager transactionManager = new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(dataEntityManager().getObject());

        return transactionManager;
    }
}

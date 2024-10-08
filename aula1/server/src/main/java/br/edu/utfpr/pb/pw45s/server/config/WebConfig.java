package br.edu.utfpr.pb.pw45s.server.config;

import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.jdbc.support.DatabaseStartupValidator;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.stream.Stream;


@Configuration
public class WebConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET","POST","PUT","PATCH","OPTIONS","DELETE")
                        .allowedHeaders("Authorization","x-xsrf-token",
                                "Access-Control-Allow-Headers", "Origin",
                                "Accept", "X-Requested-With", "Content-Type",
                                "Access-Control-Request-Method",
                                "Access-Control-Request-Headers", "Auth-Id-Token");
            }
        };
    }

    @Bean
    public BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return beanFactory -> {
          String[] flyWay = beanFactory.getBeanNamesForType(Flyway.class);
            Stream.of(flyWay)
                    .map(beanFactory::getBeanDefinition)
                    .forEach(it -> it.setDependsOn("databaseStartupValidator"));
            String[] jpa = beanFactory.getBeanNamesForType(EntityManagerFactory.class);
            Stream.of(jpa)
                    .map(beanFactory::getBeanDefinition)
                    .forEach(it -> it.setDependsOn("databaseStartupValidator"));
        };
    }

    @Bean
    public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource) {
        var dsv = new DatabaseStartupValidator();
        dsv.setDataSource(dataSource);
        dsv.setTimeout(120);
        dsv.setInterval(7);
        // dsv.setValidationQuery(DatabaseDriver.POSTGRESQL.getValidationQuery());
        dsv.afterPropertiesSet();
        return dsv;
    }
}

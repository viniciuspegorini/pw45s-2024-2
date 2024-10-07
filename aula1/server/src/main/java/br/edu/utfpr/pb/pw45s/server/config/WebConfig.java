package br.edu.utfpr.pb.pw45s.server.config;

import jakarta.persistence.EntityManagerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


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
}

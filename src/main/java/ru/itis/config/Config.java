package ru.itis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.itis.orm.EntityManagerFactory;

@Configuration
@ComponentScan({"ru.itis"})
public class Config {


    @Bean(destroyMethod = "destroy")
    @Scope("singleton")
    public EntityManagerFactory getEntityManagerFactory(CharSequence url) {
        return new EntityManagerFactory(url);
    }

    @Bean
    public CharSequence getUrl() {
        return "url";
    }

}
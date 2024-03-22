package com.kafein.garage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Configuration
public class JsonPopulatorConfiguration {

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator(){
        Jackson2RepositoryPopulatorFactoryBean factoryBean = new Jackson2RepositoryPopulatorFactoryBean();
        factoryBean.setResources(new Resource[]{new ClassPathResource("garageslot.json"), new ClassPathResource("vehicletype.json")});
        return factoryBean;
    }
}

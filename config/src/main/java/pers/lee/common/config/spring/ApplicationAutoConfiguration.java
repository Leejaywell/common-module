package pers.lee.common.config.spring;

import pers.lee.common.config.ApplicationConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Jay.Lee on 2019/11/25 17:11
 */
@Configuration
public class ApplicationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ApplicationConfiguration.class)
    public ApplicationConfiguration applicationConfiguration() {
        return ApplicationConfiguration.get();
    }
}

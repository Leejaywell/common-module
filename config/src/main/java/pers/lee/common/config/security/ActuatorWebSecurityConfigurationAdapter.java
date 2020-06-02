package pers.lee.common.config.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by Passyt on 2018/11/15.
 */
@Configuration
@ConditionalOnClass(value = {EnableWebSecurity.class})
@EnableWebSecurity
public class ActuatorWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
                .authorizeRequests()
                .antMatchers("/admin/**").authenticated()
                .antMatchers("/**").permitAll()
                .and()
                .httpBasic();
    }

}


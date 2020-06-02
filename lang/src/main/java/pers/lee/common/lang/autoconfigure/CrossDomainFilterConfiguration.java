package pers.lee.common.lang.autoconfigure;

import pers.lee.common.lang.web.CrossDomainFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Created by Passyt on 2018/5/16.
 */
@ConditionalOnWebApplication
public class CrossDomainFilterConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public FilterRegistrationBean crossDomainFilterBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new CrossDomainFilter());
        registrationBean.addUrlPatterns("*.ci", "*.rpc");
        return registrationBean;
    }

}

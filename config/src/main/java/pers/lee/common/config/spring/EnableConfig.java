package pers.lee.common.config.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by Passyt on 2018/3/24.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ApplicationAutoConfiguration.class, ConfigEventAutoConfiguration.class})
public @interface EnableConfig {

    String applicationKey();

}
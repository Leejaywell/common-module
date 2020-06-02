package pers.lee.common.config;

import java.lang.annotation.*;

/**
 * Created by Passyt on 2018/3/24.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Config {

    /**
     * key of configuration
     *
     * @return
     */
    String value();
}
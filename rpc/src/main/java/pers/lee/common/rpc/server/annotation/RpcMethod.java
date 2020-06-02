package pers.lee.common.rpc.server.annotation;

import java.lang.annotation.*;

/**
 * 
 * @author ZhangPei
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface RpcMethod {

	String value() default "";

	boolean ignore() default false;
}

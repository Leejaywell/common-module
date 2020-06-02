package pers.lee.common.rpc.server.annotation;

import java.lang.annotation.*;

/**
 * 
 * @author ZhangPei
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface RpcParam {

	String value();
}

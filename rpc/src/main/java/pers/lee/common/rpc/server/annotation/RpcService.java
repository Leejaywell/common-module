package pers.lee.common.rpc.server.annotation;

import java.lang.annotation.*;

/**
 * 
 * @author ZhangPei
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface RpcService {

	String value();
}

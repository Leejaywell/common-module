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
public @interface RpcConverter {

	String value();

	RpcConverterType type() default RpcConverterType.All;

	public enum RpcConverterType {
		All, Parameter, Return
	}

}
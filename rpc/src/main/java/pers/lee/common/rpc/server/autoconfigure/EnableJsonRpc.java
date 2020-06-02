package pers.lee.common.rpc.server.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: Jay
 * @date: 2018/4/20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({JsonRpcConfiguration.class})
public @interface EnableJsonRpc {
    /**
     * rpc properties path
     */
    String rpcConfig() default "";

    /**
     * url patterns
     */
    String[] urlPatterns() default {"*.rpc"};

    /**
     * inti rpc type
     *
     * @return
     */
    RpcType type() default RpcType.WEB_FILTER;

}

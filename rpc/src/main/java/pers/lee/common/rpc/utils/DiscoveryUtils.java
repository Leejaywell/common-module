package pers.lee.common.rpc.utils;

import org.apache.commons.lang3.ClassUtils;

import java.util.concurrent.Callable;

/**
 * DiscoveryUtils
 *
 * @author Drizzt Yang
 */
public class DiscoveryUtils {
    
    public static <T> T create(String className, Callable<T> factoryCallable, Callable<T> alternateFactoryCallable) {
        try {
            ClassUtils.getClass(className);
            return factoryCallable.call();
        } catch (Throwable throwable) {
            if(alternateFactoryCallable == null) {
                return null;
            }
            try {
                return alternateFactoryCallable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean discovery(String className) {
        try {
            ClassUtils.getClass(className);
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

}

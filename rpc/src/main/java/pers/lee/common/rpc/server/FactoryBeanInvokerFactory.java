package pers.lee.common.rpc.server;

import pers.lee.common.lang.utils.ReflectUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * FactoryBeanInvokerFactory
 *
 * @author Drizzt Yang
 */
public class FactoryBeanInvokerFactory extends BaseMethodInvokerFactory {
    public static final String FACTORY_BEAN_PATTERN;

    static {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("^[\\.\\w]+");
        stringBuffer.append("\\(");
        stringBuffer.append("\\)");
        stringBuffer.append("[\\.\\w]+");
        stringBuffer.append("\\(");
        stringBuffer.append("([\\.\\w]+\\,)*");
        stringBuffer.append("([\\.\\w]+(:[\\w]+)?)?");
        stringBuffer.append("\\)");
        stringBuffer.append("$");
        FACTORY_BEAN_PATTERN = stringBuffer.toString();
    }

    public MethodInvoker getMethodInvoker(String key) throws IllegalConfigurationException {
        key = PatternUtils.removeWhite(key);
        if (!Pattern.matches(FACTORY_BEAN_PATTERN, key)) {
            return super.getMethodInvoker(key);
        }

        int factoryMethodIndex = key.indexOf("()");
        int methodDotIndex = key.lastIndexOf(".", factoryMethodIndex);
        String factoryClassName = key.substring(0, methodDotIndex);
        String factoryMethodName = key.substring(methodDotIndex + 1, factoryMethodIndex);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> factoryClass = getBeanClass(factoryClassName, classLoader);

        Method factoryMethod = getPublicMethod(factoryClass, factoryMethodName, new ArrayList<Class<?>>());
        if (!Modifier.isStatic(factoryMethod.getModifiers())) {
            throw new IllegalConfigurationException("The factory method [" + factoryMethodName + "] is not static");
        }

        Class<?> clazz = factoryMethod.getReturnType();

        Method method = digestMethod(key.substring(factoryMethodIndex + 2), classLoader, clazz);
        Object bean;
        try {
            bean = ReflectUtils.invokeMethod(factoryMethod, null);
        } catch (RuntimeException e) {
            Throwable throwable = e;
            if (e.getCause() != null) {
                throwable = e.getCause();
            }
            if (throwable instanceof RpcException) {
                throw (RpcException) throwable;
            } else {
                throw new RpcException(throwable.getMessage(), throwable);
            }
        }
        return createMethodInvoker(bean, method);
    }

    protected Method digestMethod(String configurationString, ClassLoader classLoader, Class<?> clazz) throws IllegalConfigurationException {
        int methodDotIndex;
        int bracketLeftIndex = configurationString.indexOf("(", 0);
        int bracketRightIndex = configurationString.indexOf(")", bracketLeftIndex);
        methodDotIndex = configurationString.lastIndexOf(".", bracketLeftIndex);
        String methodName = configurationString.substring(methodDotIndex + 1, bracketLeftIndex);
        String[] parameterClassNames = configurationString.substring(bracketLeftIndex + 1, bracketRightIndex).split(",");

        List<Class<?>> parameterClasses = new ArrayList<Class<?>>();
        if (parameterClassNames.length > 1 || !"".equals(parameterClassNames[0])) {
            for (String parameterClassName : parameterClassNames) {
                getParameterClasses(classLoader, parameterClasses, parameterClassName);
            }
        }

        return getPublicMethod(clazz, methodName, parameterClasses);
    }
}

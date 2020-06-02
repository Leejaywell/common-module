package pers.lee.common.rpc.server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author YangYang
 * @version 0.1, 2008-3-17 16:15:30
 */
public class BaseMethodInvokerFactory implements MethodInvokerFactory {
    public static final String PATTERN_CONFIGURATION;
    static {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("^[\\.\\w]+");
        stringBuffer.append("\\.");
        stringBuffer.append("[\\w]+");
        stringBuffer.append("\\(");
        stringBuffer.append("([\\.\\w]+\\,)*");
        stringBuffer.append("([\\.\\w]+(:[\\w]+)?)?");
        stringBuffer.append("\\)");
        stringBuffer.append("$");
        PATTERN_CONFIGURATION = stringBuffer.toString();
    }

    public MethodInvoker getMethodInvoker(String key) throws IllegalConfigurationException {
        key = PatternUtils.removeWhite(key);
        if(!Pattern.matches(PATTERN_CONFIGURATION, key)){
            throw new IllegalConfigurationException("Invalid configuration [" + key + "]");
        }

        int bracketLeftIndex = key.indexOf("(");
        int bracketRightIndex = key.indexOf(")", bracketLeftIndex);
        int methodDotIndex = key.lastIndexOf(".", bracketLeftIndex);

        String className = key.substring(0, methodDotIndex);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> clazz = getBeanClass(className, classLoader);

        String methodName = key.substring(methodDotIndex + 1, bracketLeftIndex);
        String[] parameterClassNames = key.substring(bracketLeftIndex + 1, bracketRightIndex).split(",");

        List<Class<?>> parameterClasses = new ArrayList<Class<?>>();
        if(parameterClassNames.length > 1 || !"".equals(parameterClassNames[0])) {
            for (String parameterClassName : parameterClassNames) {
                getParameterClasses(classLoader, parameterClasses, parameterClassName);
            }
        }

        Method method = getPublicMethod(clazz, methodName, parameterClasses);
        Object bean = null;
        if(!Modifier.isStatic(method.getModifiers())) {
            try {
                bean = clazz.newInstance();
            } catch (Exception e) {
                throw new IllegalConfigurationException("Instantiation bean fails", e);
            }
        }

        return createMethodInvoker(bean, method);
    }

    protected MethodInvoker createMethodInvoker(Object bean, Method method) {
        return new MethodInvoker(method, bean);
    }

    protected void getParameterClasses(ClassLoader classLoader, List<Class<?>> parameterClasses, String parameterClassName) throws IllegalConfigurationException {
        try {
            parameterClasses.add(classLoader.loadClass(parameterClassName));
        } catch (ClassNotFoundException e) {
            throw new IllegalConfigurationException("Invalid parameter class configuration [" + parameterClassName + "]", e);
        }
    }

    protected Class<?> getBeanClass(String className, ClassLoader classLoader) throws IllegalConfigurationException {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalConfigurationException("Invalid class configuration [" + className + "]", e);
        }
    }

    protected Method getPublicMethod(Class<?> clazz, String methodName, List<Class<?>> parameterClasses) throws IllegalConfigurationException {
        Method method;
        try {
            method = clazz.getMethod(methodName, parameterClasses.toArray(new Class[parameterClasses.size()]));
        } catch (NoSuchMethodException e) {
            throw new IllegalConfigurationException("Invalid method configuration [" + methodName + "]", e);
        }

        if(!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalConfigurationException("The method is not public");
        }
        return method;
    }
}

package pers.lee.common.rpc.server;

import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Method;

/**
 * @author YangYang
 * @version 0.1, 2008-3-20 16:10:05
 */
public class SpringBeanMethodInvokerFactory extends FactoryBeanInvokerFactory {
	
    private WebApplicationContext webApplicationContext;

    public static final String PATTERN_RPC_CONFIGURATION_METHOD_PART;
    public static final String SPRING_PREFIX = "spring.";
    static {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("spring\\.");
        stringBuffer.append("[\\w]+");
        stringBuffer.append("\\(");
        stringBuffer.append("([\\.\\w]+(:[\\w]+)?\\,)*");
        stringBuffer.append("([\\.\\w]+(:[\\w]+)?)?");
        stringBuffer.append("\\)");
        stringBuffer.append("(:[\\w]+)?$");
        PATTERN_RPC_CONFIGURATION_METHOD_PART = stringBuffer.toString();
    }

    public MethodInvoker getMethodInvoker(String key) throws IllegalConfigurationException {
        key = PatternUtils.removeWhite(key);
        if(!key.startsWith(SPRING_PREFIX)) {
            return super.getMethodInvoker(key);
        }

        int bracketLeftIndex = key.indexOf("(");
        int methodDotIndex = key.lastIndexOf(".", bracketLeftIndex);
        if (methodDotIndex < SPRING_PREFIX.length()) {
            throw new IllegalConfigurationException("Not found bean name in configuration [" + key + "]");
        }
        String beanName = key.substring(SPRING_PREFIX.length(), methodDotIndex);
        Object bean = getWebApplicationContext().getBean(beanName);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Class<?> clazz = getWebApplicationContext().getType(beanName);
        Method method = digestMethod(key.substring(methodDotIndex + 1), classLoader, clazz);
        return createMethodInvoker(bean, method);
    }

    public WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }

    public void setWebApplicationContext(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }
}

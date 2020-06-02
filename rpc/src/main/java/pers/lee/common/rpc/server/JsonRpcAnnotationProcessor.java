package pers.lee.common.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import pers.lee.common.rpc.server.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * @author: Jay
 * @date: 2018/4/23
 */
public class JsonRpcAnnotationProcessor {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final String PATH_PREFIX = "/";
    private static final Pattern PATTERN_JSONRPC_PATH = Pattern.compile("^/?[A-Za-z0-9._~-]+(/[A-Za-z0-9._~-]+)*$");

    private Map<RpcConfig.RpcKey, Method> rpcMethods = new HashMap<>();
    private Map<String, Method> converterMethods = new HashMap<>();
    private SpringBeanAnnotationMethodInvokerFactory methodInvokerFactory;
    private RpcInvokerFactory rpcInvokerFactory;

    public void registerMetaInfo(Class clazz) {
        if (clazz == null) {
            return;
        }
        registerRpcService(clazz);
        registerRpcConverter(clazz);
    }

    public RpcConfig process() {
        Set<String> domains = new HashSet<>();
        Map<RpcConfig.RpcKey, RpcInvoker> rpcMap = new HashMap<>();
        rpcMethods.forEach(((rpcKey, method) -> {
            MethodInvoker methodInvoker = methodInvokerFactory.getMethodInvoker(method);
            List<MethodInvoker> parameterConverterInvokers = createParameterConverterInvokers(createParameterConverters(method));
            MethodInvoker returnConverterInvoker = createReturnConverterInvoker(method);

            logger.info(format("registered bean[%s] for JSON-RPC domain[%s], method[%s]", method.getDeclaringClass().getName(), rpcKey.getPath(), rpcKey.getMethod()));

            RpcInvoker rpcInvoker = this.rpcInvokerFactory.getRpcInvoker(methodInvoker, parameterConverterInvokers, returnConverterInvoker);
            domains.add(rpcKey.getPath());
            rpcMap.put(rpcKey, rpcInvoker);
        }));
        return new RpcConfig(domains, rpcMap);
    }

    private void registerRpcConverter(Object bean) {
        List<Method> publicMethods = getMethods(AopUtils.getTargetClass(bean));
        for (Method method : publicMethods) {
            if (method.isAnnotationPresent(RpcConverter.class)) {
                RpcConverter rpcConverter = method.getAnnotation(RpcConverter.class);
                String converterName = rpcConverter.value();
                converterMethods.put(converterName, method);
            }
        }
    }

    private void registerRpcService(Class clazz) {
        // Note:findAnnotationOnBean can only find the first match result
        RpcService rpcService = AnnotationUtils.findAnnotation(clazz, RpcService.class);
        if (rpcService != null) {
            String path = rpcService.value();
            if (!path.startsWith(PATH_PREFIX)) {
                throw new RuntimeException("domain[" + path + "] should start with " + PATH_PREFIX);
            }
            if (!PATTERN_JSONRPC_PATH.matcher(path).matches()) {
                throw new RuntimeException("the path [" + path + "] for the bean [" + clazz.getName() + "] is not valid");
            }
            registerRpcMethods(clazz, path);
        }

    }

    private void registerRpcMethods(Class clazz, String path) {
        logger.info("registering class [{}] for JSON-RPC in path[{}]", clazz.getName(), path);
        List<Method> methods = getMethods(clazz);
        for (Method method : methods) {
            RpcMethod rpcMethod = AnnotationUtils.findAnnotation(method, RpcMethod.class);
            if (rpcMethod == null) {
                continue;
            }
            if (rpcMethod.ignore()) {
                continue;
            }
            String methodExportName = method.getName();
            String value = rpcMethod.value();
            if (!"".equals(value.trim()) && value.length() > 0) {
                methodExportName = value;
            }
            RpcConfig.RpcKey rpcKey = new RpcConfig.RpcKey(path, methodExportName);
            rpcMethods.put(rpcKey, method);
        }
    }


    private List<MethodInvoker> createParameterConverterInvokers(List<String> parameterConverters) {
        List<MethodInvoker> methodInvokers = new ArrayList<>();
        for (String parameterConverter : parameterConverters) {
            if (parameterConverter == null) {
                methodInvokers.add(null);
            } else {
                methodInvokers.add(createConverterInvoker(parameterConverter));
            }
        }
        return methodInvokers;
    }

    private MethodInvoker createConverterInvoker(String parameterConverter) {
        Method method = converterMethods.get(parameterConverter);
        if (method == null) {
            throw new RuntimeException("converter of [" + parameterConverter + "] not found");
        }
        return this.methodInvokerFactory.getMethodInvoker(method);
    }

    private List<String> createParameterConverters(Method method) {
        List<String> converters = new ArrayList<>();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < paramAnnotations.length; i++) {
            Annotation[] ann = paramAnnotations[i];
            boolean returnConverterDefined = false;
            for (Annotation an : ann) {
                if (an instanceof RpcParam) {
                    RpcParam rpcParam = (RpcParam) an;
                    String converterName = rpcParam.value();
                    converters.add(converterName);
                    returnConverterDefined = true;
                    break;
                }
            }
            if (!returnConverterDefined) {
                converters.add(null);
            }
        }
        return converters;
    }

    private MethodInvoker createReturnConverterInvoker(Method method) {
        RpcResult rpcResult = AnnotationUtils.findAnnotation(method, RpcResult.class);
        if (rpcResult == null) {
            return null;
        }
        return createConverterInvoker(rpcResult.value().trim());
    }

    private List<Method> getMethods(Class clazz) {
        Set<Method> methodFormObjects = getSkippedMethods();
        return Stream.of(ReflectionUtils.getUniqueDeclaredMethods(clazz))
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> !methodFormObjects.contains(method))
                .collect(Collectors.toList());
    }

    private Set<Method> getSkippedMethods() {
        return Stream.of(Object.class.getMethods()).collect(Collectors.toSet());
    }

    public void setMethodInvokerFactory(SpringBeanAnnotationMethodInvokerFactory methodInvokerFactory) {
        this.methodInvokerFactory = methodInvokerFactory;
    }

    public void setRpcInvokerFactory(RpcInvokerFactory rpcInvokerFactory) {
        this.rpcInvokerFactory = rpcInvokerFactory;
    }

    public SpringBeanAnnotationMethodInvokerFactory getMethodInvokerFactory() {
        return methodInvokerFactory;
    }

    public RpcInvokerFactory getRpcInvokerFactory() {
        return rpcInvokerFactory;
    }
}

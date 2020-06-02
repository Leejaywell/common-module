package pers.lee.common.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author: Jay
 * @date: 2018/4/25
 */
public class JsonRpcInvokerContext implements JsonRpcContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonRpcInvokerContext.class);

    public static final String PREFIX_CONVERTOR_PARAMETER = "convertor.parameter.";
    public static final String PREFIX_CONVERTOR_RETURN = "convertor.return.";
    public static final String PREFIX_RPC = "rpc.";

    public static final String PATTERN_RPC_KEY_PATH = "^\\[[\\.\\w\\\\/]+\\]\\.";
    public static final String PATTERN_RPC_KEY = PATTERN_RPC_KEY_PATH + "[\\w]+$";

    private RpcConfig rpcConfig;
    private MethodInvokerFactory methodInvokerFactory;
    private RpcInvokerFactory rpcInvokerFactory;

    public JsonRpcInvokerContext() {
        rpcConfig = new RpcConfig();
    }

    public void init(Properties properties) {
        HashMap<String, MethodInvoker> parameterConvertorMap = new HashMap<>();
        HashMap<String, MethodInvoker> returnConvertorMap = new HashMap<>();

        Properties rpcProperties = new Properties();
        for (Object o : properties.keySet()) {
            String key = (String) o;
            if (key.startsWith(PREFIX_CONVERTOR_PARAMETER)) {
                String convertorName = key.substring(PREFIX_CONVERTOR_PARAMETER.length());
                try {
                    parameterConvertorMap.put(convertorName, methodInvokerFactory.getMethodInvoker(properties.getProperty(key)));
                } catch (IllegalConfigurationException e) {
                    LOGGER.warn("Ignored RPC parameter convertor with key [" + key + "] because of initialization failure", e);
                }

            } else if (key.startsWith(PREFIX_CONVERTOR_RETURN)) {
                String convertorName = key.substring(PREFIX_CONVERTOR_RETURN.length());
                try {
                    returnConvertorMap.put(convertorName, methodInvokerFactory.getMethodInvoker(properties.getProperty(key)));
                } catch (IllegalConfigurationException e) {
                    LOGGER.warn("Ignored RPC return convertor with key [" + key + "] because of initialization failure", e);
                }

            } else if (key.startsWith(PREFIX_RPC)) {
                rpcProperties.setProperty(key.substring(PREFIX_RPC.length()), properties.getProperty(key));
            } else {
                LOGGER.warn("Ignored configuration with key [" + key + "]");
            }
        }
        Map<RpcConfig.RpcKey, RpcInvoker> rpcMap = new HashMap<>();
        Set<String> domains = new HashSet<>();
        for (Object o : rpcProperties.keySet()) {
            String key = (String) o;
            try {
                RpcConfig.RpcKey rpcKey = getRPCKey(key);
                rpcMap.put(rpcKey, createRPCInvoker(rpcProperties.getProperty(key), parameterConvertorMap, returnConvertorMap));
                domains.add(rpcKey.getPath());
            } catch (IllegalConfigurationException e) {
                LOGGER.warn("Ignored RPC configuration with key [" + PREFIX_RPC + key + "] because of initialization failure", e);
            }
        }
        rpcConfig.setRpcMap(rpcMap);
        rpcConfig.setDomains(domains);
    }

    protected RpcInvoker createRPCInvoker(String configurationString,
                                          HashMap<String, MethodInvoker> parameterConvertorMap,
                                          HashMap<String, MethodInvoker> returnConvertorMap) throws IllegalConfigurationException {
        RpcInvokerConfiguration rpcInvokerConfiguration = new RpcInvokerConfiguration(configurationString);
        MethodInvoker coreMethodInvoker = methodInvokerFactory.getMethodInvoker(rpcInvokerConfiguration
                .getMethodInvokerConfiguration());

        MethodInvoker returnConvertorInvoker = null;
        String returnConvertorName = rpcInvokerConfiguration.getReturnConvertor();
        if (returnConvertorName != null) {
            if (returnConvertorMap.get(returnConvertorName) == null) {
                throw new IllegalConfigurationException("Not found the return convertor [" + returnConvertorName
                        + "] of RPC configuration [" + configurationString + "]");
            }
            returnConvertorInvoker = returnConvertorMap.get(returnConvertorName);
        }

        List<MethodInvoker> parameterConvertorInvokers = new ArrayList<MethodInvoker>();
        for (String convertorName : rpcInvokerConfiguration.getParameterConvertors()) {
            MethodInvoker methodInvoker = null;
            if (convertorName != null) {
                methodInvoker = parameterConvertorMap.get(convertorName);
                if (methodInvoker == null) {
                    throw new IllegalConfigurationException("Not found the parameter convertor [" + convertorName
                            + "] of RPC configuration [" + configurationString + "]");
                }
            }
            parameterConvertorInvokers.add(methodInvoker);
        }
        return this.rpcInvokerFactory.getRpcInvoker(coreMethodInvoker, parameterConvertorInvokers, returnConvertorInvoker);
    }

    private RpcConfig.RpcKey getRPCKey(String keyString) throws IllegalConfigurationException {
        if (!Pattern.matches(PATTERN_RPC_KEY, keyString)) {
            throw new IllegalConfigurationException("Invalid RPC configuration key [" + keyString + "]");
        }

        List<String> replacedBuffer = new ArrayList<String>();
        String method = PatternUtils.replace(keyString, PATTERN_RPC_KEY_PATH, "", replacedBuffer);
        String path = replacedBuffer.get(0).substring(1, replacedBuffer.get(0).length() - 2);
        return new RpcConfig.RpcKey(path, method);
    }

    public RpcInvoker getRpcInvoker(String path, String method) {
        return rpcConfig.getRPCInvoker(new RpcConfig.RpcKey(path, method));
    }

    @Override
    public RpcConfig getRpcConfig() {
        return this.rpcConfig;
    }

    @Override
    public boolean accept(String domain) {
        return this.rpcConfig.getDomains().contains(domain);
    }

    public void setRPCInvokerFactory(RpcInvokerFactory rpcInvokerFactory) {
        this.rpcInvokerFactory = rpcInvokerFactory;
    }

    public void setMethodInvokerFactory(MethodInvokerFactory methodInvokerFactory) {
        this.methodInvokerFactory = methodInvokerFactory;
    }
}

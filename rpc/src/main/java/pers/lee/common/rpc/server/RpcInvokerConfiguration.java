package pers.lee.common.rpc.server;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author YangYang
 * @version 0.1, 2008-3-28 15:51:29
 */
public class RpcInvokerConfiguration {
    public static final String PATTERN_RPC_CONFIGURATION;
    static {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("^[\\.\\w]+");
        stringBuffer.append("(\\.[\\w]+\\(\\))?");
        stringBuffer.append("\\.");
        stringBuffer.append("[\\w]+");
        stringBuffer.append("\\(");
        stringBuffer.append("([\\.\\w]+(:[\\w]+)?\\,)*");
        stringBuffer.append("([\\.\\w]+(:[\\w]+)?)?");
        stringBuffer.append("\\)");
        stringBuffer.append("(:[\\w]+)?$");
        PATTERN_RPC_CONFIGURATION = stringBuffer.toString();
    }

    private String methodInvokerConfiguration;
    private List<String> parameterConvertors;
    private String returnConvertor;

    public RpcInvokerConfiguration(String configuration) throws IllegalConfigurationException {
        parameterConvertors = new ArrayList<String>();
        parseRPCConfiguration(configuration);
    }

    private void parseRPCConfiguration(String configurationString) throws IllegalConfigurationException {
        configurationString = PatternUtils.removeWhite(configurationString);
        if(!Pattern.matches(PATTERN_RPC_CONFIGURATION, configurationString)){
            throw new IllegalConfigurationException("Invalid RPC configuration [" + configurationString + "]");
        }

        List<String> replacedBuffer = new ArrayList<String>();

        int bracketLeftIndex = configurationString.lastIndexOf("(");

        String methodPart = configurationString.substring(bracketLeftIndex);

        String parsedString = PatternUtils.replace(methodPart, "(:[\\w]+)?\\,", ",", replacedBuffer);
        parsedString = PatternUtils.replace(parsedString, "(:[\\w]+)?\\)", ")", replacedBuffer);

        if (replacedBuffer.size() > 0) {
            for (String replacedString : replacedBuffer) {
                if (replacedString.length() < 2) {
                    getParameterConvertors().add(null);
                } else {
                    getParameterConvertors().add(replacedString.substring(1, replacedString.length() - 1));
                }
            }
        }

        replacedBuffer = new ArrayList<>();
        parsedString = PatternUtils.replace(parsedString, "\\)(:[\\w]+)?", ")", replacedBuffer);
        String replacedString = replacedBuffer.get(replacedBuffer.size() - 1);
        if (replacedString.length() > 2) {
            returnConvertor = replacedBuffer.get(replacedBuffer.size() - 1).substring(2);
        }

        methodInvokerConfiguration = configurationString.substring(0, bracketLeftIndex) + parsedString;
    }

    public String getMethodInvokerConfiguration() {
        return methodInvokerConfiguration;
    }

    public List<String> getParameterConvertors() {
        return parameterConvertors;
    }

    public String getReturnConvertor() {
        return returnConvertor;
    }
}

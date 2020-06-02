package pers.lee.common.rpc.ci.config;

/**
 * ConfigFilter
 *
 * @author Drizzt Yang
 */
public interface ConfigFilter {

    String READ_ONLY = "readonly";
    String INVISIBLE = "invisible";
    String VARIABLE = "variable";

    String getConfigurationVisibility(String key);

    boolean isVisible(String key);

    boolean isVariable(String key);
}

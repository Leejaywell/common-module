package pers.lee.common.rpc.ci.query;

/**
 * @author yangyang
 * @since 2009-10-23
 */
public class Aggregator {
	
    public static final String AGGREGATOR_FUNCTION_COUNT = "count";
    public static final String AGGREGATOR_FUNCTION_MIN = "min";
    public static final String AGGREGATOR_FUNCTION_MAX = "max";
    public static final String AGGREGATOR_FUNCTION_SUM = "sum";
    public static final String AGGREGATOR_FUNCTION_AVG = "avg";

    private String property;
    private String function;

    public Aggregator() {
	}

	public Aggregator(String property, String function) {
		this.property = property;
		this.function = function;
	}

	public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}

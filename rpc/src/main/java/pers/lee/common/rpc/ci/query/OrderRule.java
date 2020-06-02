package pers.lee.common.rpc.ci.query;

/**
 * Order represents the query order rules
 * 
 * @author YangYang
 * @version 0.1, 2008-5-27 22:50:35
 */
public class OrderRule {

	public static final String TYPE_ASC = "asc";
	public static final String TYPE_DESC = "desc";

	private String property;
	private String type;

	public OrderRule() {
	}

	public OrderRule(String property, String type) {
		this.property = property;
		this.type = type;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

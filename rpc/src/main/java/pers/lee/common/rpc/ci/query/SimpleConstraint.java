package pers.lee.common.rpc.ci.query;

/**
 * The simple constraint represents a constraint with one property of the entity.
 * There are several constraint compareOperator supported. The value should be
 * suitable with the property.
 * 
 * The values is supposed to be some simple value, like number, string, boolean,
 * date
 * 
 * @author YangYang
 * @version 0.1, 2008-5-27 21:30:00
 */
public class SimpleConstraint implements Constraint {

	public static final String COMPARE_EQUAL = "=";
	public static final String COMPARE_NOT_EQUAL = "!=";
	public static final String COMPARE_GREATER = ">";
	public static final String COMPARE_SMALLER = "<";
	public static final String COMPARE_GREATER_EQUAL = ">=";
	public static final String COMPARE_SMALLER_EQUAL = "<=";
	public static final String COMPARE_LIKE = "like";
	@Deprecated
	public static final String COMPARE_IS_NULL = "isNULL";

	public static final String COMPARE_IS = "is";

	public static final String VALUE_NULL = "null";

	public static final String VALUE_NOT_NULL = "not null";

	private String entityProperty;
	private String compareOperator;
	private Object value;

	private boolean isNot;

	public SimpleConstraint() {
	}

	public SimpleConstraint(String entityProperty, String compareOperator, Object value) {
		this.entityProperty = entityProperty;
		this.compareOperator = compareOperator;
		this.value = value;
	}

	public boolean isNot() {
		return isNot;
	}

	public void setNot(boolean not) {
		isNot = not;
	}

	public String getEntityProperty() {
		return entityProperty;
	}

	public void setEntityProperty(String entityProperty) {
		this.entityProperty = entityProperty;
	}

	public String getCompareOperator() {
		return compareOperator;
	}

	public void setCompareOperator(String compareOperator) {
		this.compareOperator = compareOperator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SimpleConstraint))
			return false;

		SimpleConstraint that = (SimpleConstraint) o;

		if (isNot != that.isNot)
			return false;
		if (compareOperator != null ? !compareOperator.equals(that.compareOperator) : that.compareOperator != null)
			return false;
		if (entityProperty != null ? !entityProperty.equals(that.entityProperty) : that.entityProperty != null)
			return false;
		if (value != null ? !value.equals(that.value) : that.value != null)
			return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (entityProperty != null ? entityProperty.hashCode() : 0);
		result = 31 * result + (compareOperator != null ? compareOperator.hashCode() : 0);
		result = 31 * result + (value != null ? value.hashCode() : 0);
		result = 31 * result + (isNot ? 1 : 0);
		return result;
	}
}

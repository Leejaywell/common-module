package pers.lee.common.rpc.ci.query;

import java.util.ArrayList;
import java.util.List;

/**
 * ConstraintGroup represents a group of constraints concated in one logic
 * operator.
 * 
 * @author YangYang
 * @version 0.1, 2008-5-27 21:30:09
 */
public class ConstraintGroup implements Constraint {

	public static final String LOGIC_AND = "and";
	public static final String LOGIC_OR = "or";

	private boolean isNot;

	private String logicOperator;

	private List<Constraint> constraints;

	public ConstraintGroup() {
	}

	public ConstraintGroup(String logicOperator) {
		this.logicOperator = logicOperator;
	}

	public ConstraintGroup(String logicOperator, List<Constraint> constraints) {
		this.logicOperator = logicOperator;
		this.constraints = constraints;
	}

	public boolean isNot() {
		return isNot;
	}

	public void setNot(boolean not) {
		isNot = not;
	}

	public String getLogicOperator() {
		return logicOperator;
	}

	public void setLogicOperator(String logicOperator) {
		this.logicOperator = logicOperator;
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	public void addConstraint(Constraint constraint) {
		if (this.constraints == null) {
			this.constraints = new ArrayList<Constraint>();
		}
		this.constraints.add(constraint);
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ConstraintGroup))
			return false;

		ConstraintGroup that = (ConstraintGroup) o;

		if (isNot != that.isNot)
			return false;
		if (constraints != null ? !constraints.equals(that.constraints) : that.constraints != null)
			return false;
		if (logicOperator != null ? !logicOperator.equals(that.logicOperator) : that.logicOperator != null)
			return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (isNot ? 1 : 0);
		result = 31 * result + (logicOperator != null ? logicOperator.hashCode() : 0);
		result = 31 * result + (constraints != null ? constraints.hashCode() : 0);
		return result;
	}
}

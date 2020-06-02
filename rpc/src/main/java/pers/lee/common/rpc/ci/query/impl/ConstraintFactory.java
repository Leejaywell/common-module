package pers.lee.common.rpc.ci.query.impl;

import pers.lee.common.rpc.ci.query.Constraint;
import pers.lee.common.rpc.ci.query.ConstraintGroup;
import pers.lee.common.rpc.ci.query.SimpleConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author YangYang
 * @version 0.1, 2008-5-27 21:30:34
 */
@SuppressWarnings("unchecked")
public class ConstraintFactory {

    public static Constraint getConstraint(Map plainConstraint) {
        if (plainConstraint == null) {
            return null;
        }
        Constraint constraint;
        if (plainConstraint.get("entityProperty") != null && plainConstraint.get("compareOperator") != null) {
            constraint = createSimpleConstraint(plainConstraint);
        } else {
            List<Map> innerConstraints = (List) plainConstraint.get("constraints");
            if (innerConstraints == null || innerConstraints.size() == 0) {
                return null;
            } else {
                // if there is only 1 constraint, return it as a simple
                // constrait. but the not property of the group should be
                // processsed
                if (innerConstraints.size() == 1) {
                    constraint = createSimpleConstraint(innerConstraints.get(0));
                    if (Boolean.TRUE.equals(plainConstraint.get("not"))) {
                        constraint.setNot(!constraint.isNot());
                    }
                    return constraint;
                }
                ConstraintGroup constraintGroup = new ConstraintGroup();
                constraint = constraintGroup;
                constraintGroup.setLogicOperator((String) plainConstraint.get("logicOperator"));

                List<Constraint> constraintList = new ArrayList<Constraint>();
                for (Map eachPlainConstraint : innerConstraints) {
                    constraintList.add(getConstraint(eachPlainConstraint));
                }
                constraintGroup.setConstraints(constraintList);
            }
        }
        constraint.setNot(Boolean.TRUE.equals(plainConstraint.get("not")));
        return constraint;
    }

    private static Constraint createSimpleConstraint(Map plainConstraint) {
        Constraint constraint;
        SimpleConstraint simpleConstraint = new SimpleConstraint();

        simpleConstraint.setEntityProperty((String) plainConstraint.get("entityProperty"));
        simpleConstraint.setCompareOperator((String) plainConstraint.get("compareOperator"));
        simpleConstraint.setValue(plainConstraint.get("value"));
        constraint = simpleConstraint;
        return constraint;
    }
}

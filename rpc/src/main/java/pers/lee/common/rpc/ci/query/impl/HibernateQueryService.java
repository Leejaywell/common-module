package pers.lee.common.rpc.ci.query.impl;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.lee.common.lang.json.Json;
import pers.lee.common.lang.json.TypeIntrospector;
import pers.lee.common.rpc.ci.query.*;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author YangYang
 * @version 0.1, 2008-5-27 21:37:24
 */
@SuppressWarnings("unchecked")
public class HibernateQueryService implements QueryService {

    private static final Logger log = LoggerFactory.getLogger(HibernateQueryService.class);
    private static final Pattern VALUE_PROPERTY_PATTERN = Pattern.compile("\\{#(.+?)\\}");

    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    public HashMap<String, Object> getEntities() {
        HashMap<String, Object> entityDescriptors = new HashMap<>();
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        for (EntityType<?> each : entities) {
            entityDescriptors.put(each.getName(), new TypeIntrospector().introspect(each.getJavaType()));
        }

        return entityDescriptors;
    }

    @SuppressWarnings("unchecked")
    public List query(String entityName, Constraint constraint, ResultDescriptor resultDescriptor) {
        if (resultDescriptor == null) {
            resultDescriptor = this.getDefaultResultDescriptor();
        }

        HashMap<String, Object> parameters = new HashMap<>();
        HashMap<String, Integer> returnIndices = new HashMap<>();
        String queryString = prepareHSQL(entityName, constraint, resultDescriptor, parameters, returnIndices).toString();
        org.hibernate.Query query = getQuery(resultDescriptor, parameters, queryString);
        List list = query.list();

        if (returnIndices.size() == 0) {
            return returnSpecify(list, resultDescriptor.getReturnProperties());
        } else if (returnIndices.size() == 1) {
            return returnSpecify(list, returnIndices.keySet().iterator().next());
        } else {
            return returnSpecify(list, returnIndices);
        }
    }

    protected List returnSpecify(List list, Set<String> properties) {
        List<HashMap> returnValues = new ArrayList<>();

        for (Object value : list) {
            returnValues.add(BeanUtils.getObjectProperties(value, properties));
        }
        return returnValues;
    }

    protected List returnSpecify(List list, String key) {
        List<Map<String, Object>> returnValues = new ArrayList<>();
        for (Object item : list) {
            Map<String, Object> value = new HashMap<>();
            value.put(key, item);
            returnValues.add(value);
        }
        return returnValues;
    }

    protected List returnSpecify(List list, HashMap<String, Integer> returnIndices) {
        List<HashMap> returnValues = new ArrayList<>();

        for (Object item : list) {
            HashMap<String, Object> value = new HashMap<>();
            Object[] objects;
            if (!item.getClass().isArray()) {
                objects = new Object[]{item};
            } else {
                objects = (Object[]) item;
            }
            for (String property : returnIndices.keySet()) {
                value.put(property, objects[returnIndices.get(property)]);
            }
            returnValues.add(value);
        }
        return returnValues;
    }

    private StringBuilder prepareHSQL(String entityName, Constraint constraint, ResultDescriptor resultDescriptor,
                                      HashMap<String, Object> parameters, HashMap<String, Integer> returnIndices) {
        String alias = getEntityAlias(entityName);

        StringBuilder stringBuilder = new StringBuilder();
        GroupDescriptor groupDescriptor = null;
        if (resultDescriptor instanceof GroupDescriptor) {
            groupDescriptor = (GroupDescriptor) resultDescriptor;
        }

        List<String> selectParts = new ArrayList<>();
        if (returnIndices != null) {
            int index = 0;
            if (resultDescriptor != null && !isEmpty(resultDescriptor.getReturnProperties())) {
                for (String property : resultDescriptor.getReturnProperties()) {
                    selectParts.add(getWrapperProperty(alias, property));
                    returnIndices.put(property, index);
                    index++;
                }
            }
            if (groupDescriptor != null && !isEmpty(groupDescriptor.getAggregators())) {
                for (Aggregator aggregator : groupDescriptor.getAggregators()) {
                    selectParts.add(aggregator.getFunction() + "(" + getWrapperProperty(alias, aggregator.getProperty()) + ")");
                    returnIndices.put(aggregator.getProperty() == null ? aggregator.getFunction() : aggregator.getProperty()
                            + aggregator.getFunction().substring(0, 1).toUpperCase() + aggregator.getFunction().substring(1), index);
                    index++;
                }
            }
        }

        if (!isEmpty(selectParts)) {
            stringBuilder.append("select ");
            stringBuilder.append(StringUtils.join(selectParts, ","));
        }

        stringBuilder.append(" from ");
        stringBuilder.append(entityName).append(" ").append(alias).append(" ");

        if (constraint != null) {
            stringBuilder.append(" where ").append(getConstraintHQL(alias, constraint, parameters));
        }

        if (groupDescriptor != null && !groupDescriptor.getGroupProperties().isEmpty()) {
            stringBuilder.append(" group by ");
            for (String groupProperty : groupDescriptor.getGroupProperties()) {
                stringBuilder.append(getWrapperProperty(alias, groupProperty)).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        if (resultDescriptor != null && resultDescriptor.getOrders() != null && resultDescriptor.getOrders().size() > 0) {
            stringBuilder.append(" order by ");

            for (OrderRule orderRule : resultDescriptor.getOrders()) {
                stringBuilder.append(getWrapperProperty(alias, orderRule.getProperty())).append(" ");
                stringBuilder.append(orderRule.getType());
                stringBuilder.append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder;
    }

    private String getStringValue(Object object) {
        if (object instanceof HashMap) {
            Date date = Json.getDefault().toJavaObject(object, Date.class);
            String pattern = (String) ((HashMap) object).get("pattern");
            if (pattern == null) {
                pattern = "yyyy-MM-dd HH:mm:ss";
            }
            return new SimpleDateFormat(pattern).format(date);
        }
        return object.toString();
    }

    private ResultDescriptor getDefaultResultDescriptor() {
        ResultDescriptor resultDescriptor = new ResultDescriptor();
        resultDescriptor.setSize(10);
        return resultDescriptor;
    }

    private static String getSimpleConstraintHQL(String alias, SimpleConstraint simpleConstraint, HashMap<String, Object> parameters) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getWrapperProperty(alias, simpleConstraint.getEntityProperty()));
        stringBuffer.append(" ");
        stringBuffer.append(simpleConstraint.getCompareOperator()).append(" ");
        // support the type "is null"
        if ("is".equalsIgnoreCase(simpleConstraint.getCompareOperator())) {
            stringBuffer.append(simpleConstraint.getValue());
        } else {
            String valueProperty = getPropertyExpression(simpleConstraint.getValue());
            if (valueProperty != null) {
                stringBuffer.append(alias).append(".").append(valueProperty);
            } else {
                stringBuffer.append(":").append(getParameterKey(simpleConstraint, parameters));
            }
        }
        return stringBuffer.toString();
    }

    private static String getPropertyExpression(Object value) {
        if (!(value instanceof String)) {
            return null;
        }

        String stringValue = String.class.cast(value);
        Matcher matcher = VALUE_PROPERTY_PATTERN.matcher(stringValue);
        if (matcher.matches()) {
            return matcher.group(1);
        }

        return null;
    }

    private static String getParameterKey(SimpleConstraint simpleConstraint, HashMap<String, Object> parameters) {
        String parameterValueKey = simpleConstraint.getEntityProperty().replace('.', '_').replace("(", "_").replace(")", "_");
        String parameterValueKeyAlias = parameterValueKey;
        while (parameters.get(parameterValueKeyAlias) != null) {
            parameterValueKeyAlias = parameterValueKey + "_" + new Random().nextInt(999);
        }
        parameters.put(parameterValueKeyAlias, simpleConstraint.getValue());
        return parameterValueKeyAlias;
    }

    public static String getConstraintHQL(String alias, Constraint constraint, HashMap<String, Object> parameters) {
        StringBuffer stringBuffer = new StringBuffer();
        if (constraint instanceof SimpleConstraint) {
            stringBuffer.append(getSimpleConstraintHQL(alias, (SimpleConstraint) constraint, parameters));
        } else if (constraint instanceof ConstraintGroup) {
            ConstraintGroup constraintGroup = (ConstraintGroup) constraint;
            stringBuffer.append(constraintGroup.isNot() ? "!" : "");
            if (constraintGroup.getConstraints() != null && constraintGroup.getConstraints().size() > 0) {
                stringBuffer.append("(");
                for (Constraint innerConstraint : constraintGroup.getConstraints()) {
                    stringBuffer.append(" ");
                    stringBuffer.append(getConstraintHQL(alias, innerConstraint, parameters));
                    stringBuffer.append(" ").append(constraintGroup.getLogicOperator());
                }
                stringBuffer.delete(stringBuffer.length() - constraintGroup.getLogicOperator().length(), stringBuffer.length());
                stringBuffer.append(")");
            }
        }

        return stringBuffer.toString();
    }

    public int delete(String entityName, Constraint constraint) {
        HashMap<String, Object> parameters = new HashMap<>();

        if (constraint == null) {
            throw new IllegalArgumentException("delete can not be applied without constraint");
        }
        if (constraint instanceof ConstraintGroup) {
            ConstraintGroup constraintGroup = (ConstraintGroup) constraint;
            if (constraintGroup.getConstraints() == null || constraintGroup.getConstraints().size() < 1) {
                throw new IllegalArgumentException("delete can not be applied without constraint");
            }
        }
        String queryString = prepareHSQL(entityName, constraint, null, parameters, null).toString();

        /*
         * org.hibernate.Query query = this.getSession().createQuery("delete " +
         * queryString); return query.executeUpdate();
         */
        // get and delete one by one
        org.hibernate.Query query = this.getSession().createQuery(queryString);
        for (String parameter : parameters.keySet()) {
            query.setString(parameter, getStringValue(parameters.get(parameter)));
        }
        List list = query.list();
        for (Object entity : list) {
            this.getSession().delete(entity);
        }
        return list.size();
    }

    public List group(String entityName, Constraint constraint, GroupDescriptor groupDescriptor) {
        HashMap<String, Object> parameters = new HashMap<>();
        HashMap<String, Integer> returnIndices = new HashMap<>();
        String queryString = prepareHSQL(entityName, constraint, groupDescriptor, parameters, returnIndices).toString();
        org.hibernate.Query query = getQuery(groupDescriptor, parameters, queryString);
        List list = query.list();

        return returnSpecify(list, returnIndices);
    }

    private org.hibernate.Query getQuery(ResultDescriptor resultDescriptor, HashMap<String, Object> parameters, String queryString) {
        if (log.isInfoEnabled()) {
            log.debug("created hql : [" + queryString + "]");
        }
        org.hibernate.Query query = this.getSession().createQuery(queryString);
        query.setFirstResult(resultDescriptor.getStartIndex());
        Integer size = resultDescriptor.getSize();
        if (size != null) {
            query.setMaxResults(size);
        }
        for (String parameter : parameters.keySet()) {
            Object value = parameters.get(parameter);
            if (value instanceof Boolean) {
                query.setBoolean(parameter, (Boolean) value);
            } else {
                query.setString(parameter, getStringValue(value));
            }
        }
        return query;
    }

    private static String getEntityAlias(String entityName) {
        return entityName.trim().replaceAll("\\.", "_");
    }

    private static String getWrapperProperty(String alias, String property) {
        if (property == null || property.isEmpty()) {
            return alias;
        }
        if (property.indexOf("(") >= 0 || property.indexOf(")") >= 0) {
            return property;
        }
        return alias + "." + property.trim();
    }

    private static Boolean isEmpty(Collection<?> datas) {
        return datas == null || (datas.size() == 0);
    }

}


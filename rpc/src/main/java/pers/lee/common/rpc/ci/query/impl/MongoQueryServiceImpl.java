package pers.lee.common.rpc.ci.query.impl;

import pers.lee.common.lang.json.Json;
import pers.lee.common.rpc.ci.management.EntityContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pers.lee.common.rpc.ci.query.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author rubys@vip.qq.com
 * @since 2013-8-12
 */
public class MongoQueryServiceImpl implements QueryService {

    private EntityContext entityContext;
    private MongoTemplate mongoTemplate;

    @Override
    public HashMap<String, Object> getEntities() {
        return entityContext.getEntities();
    }

    @Override
    public List<?> query(String entityName, Constraint constraint, ResultDescriptor resultDescriptor) {
        if (resultDescriptor == null) {
            resultDescriptor = this.getDefaultResultDescriptor();
        }
        MongoPersistentEntity<?> persistentEntity = entityContext.getPersistentEntity(entityName);
        return mongoTemplate.find(getQuery(constraint, resultDescriptor, persistentEntity), entityContext.getEntityClass(entityName));
    }

    @Override
    public int delete(String entityName, Constraint constraint) {
        if (constraint == null) {
            throw new IllegalArgumentException("delete can not be applied without constraint");
        }
        if (constraint instanceof ConstraintGroup) {
            ConstraintGroup constraintGroup = (ConstraintGroup) constraint;
            if (constraintGroup.getConstraints() == null || constraintGroup.getConstraints().size() < 1) {
                throw new IllegalArgumentException("delete can not be applied without constraint");
            }
        }
        Class<?> entityClass = entityContext.getEntityClass(entityName);
        MongoPersistentEntity<?> persistentEntity = entityContext.getPersistentEntity(entityName);

        Query query = getQuery(constraint, this.getDefaultResultDescriptor(), persistentEntity);
        List<?> results = mongoTemplate.find(query, entityClass);
        mongoTemplate.remove(query, entityClass);
        return results.size();
    }

    @Override
    public List<?> group(String entityName, Constraint constraint, GroupDescriptor groupDescriptor) {
        if (groupDescriptor == null || groupDescriptor.getAggregators() == null) {
            throw new IllegalArgumentException("Group descriptor is required.");
        }
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        if (constraint != null) {
            MongoPersistentEntity<?> persistentEntity = entityContext.getPersistentEntity(entityName);
            Criteria criteria = getCriteria(constraint, persistentEntity);
            if (criteria != null) {
                aggregationOperations.add(Aggregation.match(criteria));
            }
        }
        List<Field> fields = new ArrayList<>();
        if (groupDescriptor.getGroupProperties() != null) {
            for (String property : groupDescriptor.getGroupProperties()) {
                fields.add(Fields.field(property));
            }
        }

        for (Aggregator aggregator : groupDescriptor.getAggregators()) {
            if (Aggregator.AGGREGATOR_FUNCTION_COUNT.equals(aggregator.getFunction())) {
                aggregationOperations.add(Aggregation.group(aggregator.getProperty()).count().as(aggregator.getProperty()
                        + StringUtils.capitalize(aggregator.getFunction())));
            } else if (Aggregator.AGGREGATOR_FUNCTION_MAX.equals(aggregator.getFunction())) {
                aggregationOperations.add(Aggregation.group(Fields.from(fields.toArray(new Field[0]))).max(aggregator.getProperty()).as(Aggregator.AGGREGATOR_FUNCTION_MAX));
            } else if (Aggregator.AGGREGATOR_FUNCTION_MIN.equals(aggregator.getFunction())) {
                aggregationOperations.add(Aggregation.group(Fields.from(fields.toArray(new Field[0]))).min(aggregator.getProperty()).as(Aggregator.AGGREGATOR_FUNCTION_MIN));
            } else if (Aggregator.AGGREGATOR_FUNCTION_AVG.equals(aggregator.getFunction())) {
                aggregationOperations.add(Aggregation.group(Fields.from(fields.toArray(new Field[0]))).avg(aggregator.getProperty()).as(Aggregator.AGGREGATOR_FUNCTION_AVG));
            } else if (Aggregator.AGGREGATOR_FUNCTION_SUM.equals(aggregator.getFunction())) {
                aggregationOperations.add(Aggregation.group(Fields.from(fields.toArray(new Field[0]))).sum(aggregator.getProperty()).as(Aggregator.AGGREGATOR_FUNCTION_SUM));
            }
        }
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        AggregationResults<Object> results = mongoTemplate.aggregate(aggregation, entityContext.getCollection(entityName), Object.class);
        return results.getMappedResults();
    }

    private Query getQuery(Constraint constraint, ResultDescriptor resultDescriptor, MongoPersistentEntity<?> persistentEntity) {
        Query query = new Query();
        query.skip(resultDescriptor.getStartIndex());
        if (resultDescriptor.getSize() != null) {
            query.limit(resultDescriptor.getSize());
        }

        if (constraint != null) {
            Criteria criteria = getCriteria(constraint, persistentEntity);
            if (criteria != null) {
                query.addCriteria(criteria);
            }
        }

        if (resultDescriptor.getOrders() != null) {
            for (OrderRule orderRule : resultDescriptor.getOrders()) {
                Sort.Direction orderType;
                if (OrderRule.TYPE_ASC.equalsIgnoreCase(orderRule.getType())) {
                    orderType = Sort.Direction.ASC;
                } else {
                    orderType = Sort.Direction.DESC;
                }
                query.with(Sort.by(orderType, orderRule.getProperty()));
            }
        }
        if (resultDescriptor.getReturnProperties() != null) {
            for (String returnProp : resultDescriptor.getReturnProperties()) {
                query.fields().include(returnProp);
            }
        }
        return query;
    }

    private Criteria getSimpleCriteria(SimpleConstraint sc, MongoPersistentEntity<?> persistentEntity) {
        Criteria criteria = Criteria.where(sc.getEntityProperty());
        MongoPersistentProperty persistentProperty = persistentEntity.getPersistentProperty(sc.getEntityProperty());
        Object value = sc.getValue();
        if (persistentProperty != null) {
            value = Json.getDefault().toJavaObject(sc.getValue(), persistentProperty.getActualType());
        }
        if (SimpleConstraint.COMPARE_EQUAL.equals(sc.getCompareOperator()) || "is".equalsIgnoreCase(sc.getCompareOperator())) {
            criteria.is(value);
        } else if (SimpleConstraint.COMPARE_NOT_EQUAL.equals(sc.getCompareOperator())) {
            criteria.ne(value);
        } else if (SimpleConstraint.COMPARE_GREATER.equals(sc.getCompareOperator())) {
            criteria.gt(value);
        } else if (SimpleConstraint.COMPARE_SMALLER.equals(sc.getCompareOperator())) {
            criteria.lt(value);
        } else if (SimpleConstraint.COMPARE_GREATER_EQUAL.equals(sc.getCompareOperator())) {
            criteria.gte(value);
        } else if (SimpleConstraint.COMPARE_SMALLER_EQUAL.equals(sc.getCompareOperator())) {
            criteria.lte(value);
        } else if (SimpleConstraint.COMPARE_LIKE.equals(sc.getCompareOperator())) {
            criteria.regex(Pattern.compile("^.*" + String.valueOf(value) + ".*$"));
        } else if (SimpleConstraint.COMPARE_IS_NULL.equals(sc.getCompareOperator())) {
            criteria.is(null);
        } else {
            throw new IllegalStateException("Constraint compareOperator[" + sc.getCompareOperator() + "] is illegal.");
        }

        if (sc.isNot()) {
            return new Criteria().norOperator(criteria);
        }
        return criteria;
    }

    private Criteria getCriteria(Constraint constraint, MongoPersistentEntity<?> persistentEntity) {
        Criteria criteria = null;
        if (constraint == null) {
            return criteria;
        }
        if (constraint instanceof SimpleConstraint) {
            criteria = getSimpleCriteria((SimpleConstraint) constraint, persistentEntity);
        } else if (constraint instanceof ConstraintGroup) {
            ConstraintGroup constraintGroup = (ConstraintGroup) constraint;
            if (constraintGroup.getConstraints() != null && constraintGroup.getConstraints().size() > 0) {
                criteria = new Criteria();

                List<Criteria> orCriterias = new ArrayList<>();
                List<Criteria> andCriterias = new ArrayList<>();
                addCriteria(constraintGroup, constraintGroup.getLogicOperator(), orCriterias, andCriterias, persistentEntity);
                if (orCriterias != null && !orCriterias.isEmpty()) {
                    criteria.orOperator(orCriterias.toArray(new Criteria[]{}));
                }
                if (andCriterias != null && !andCriterias.isEmpty()) {
                    criteria.andOperator(andCriterias.toArray(new Criteria[]{}));
                }
                return criteria;
            }
        }
        return criteria;
    }

    private void addCriteria(Constraint constraint, String logicOperator, List<Criteria> orCriterias, List<Criteria> andCriterias, MongoPersistentEntity<?> persistentEntity) {
        if (constraint == null) {
            return;
        }
        if (constraint instanceof SimpleConstraint) {
            Criteria criteria = getSimpleCriteria((SimpleConstraint) constraint, persistentEntity);
            if (ConstraintGroup.LOGIC_OR.equalsIgnoreCase(logicOperator)) {
                orCriterias.add(criteria);
            } else {
                andCriterias.add(criteria);
            }
        } else if (constraint instanceof ConstraintGroup) {
            ConstraintGroup constraintGroup = (ConstraintGroup) constraint;
            if (constraintGroup.getConstraints() != null && constraintGroup.getConstraints().size() > 0) {
                for (Constraint innerConstraint : constraintGroup.getConstraints()) {
                    addCriteria(innerConstraint, constraintGroup.getLogicOperator(), orCriterias, andCriterias, persistentEntity);
                }
            }
        }
    }

    private ResultDescriptor getDefaultResultDescriptor() {
        ResultDescriptor resultDescriptor = new ResultDescriptor();
        resultDescriptor.setSize(10);
        return resultDescriptor;
    }

    @Required
    public void setEntityContext(EntityContext entityContext) {
        this.entityContext = entityContext;
    }

    @Required
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}

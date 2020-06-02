package pers.lee.common.rpc.ci.management.impl;

import pers.lee.common.lang.json.Json;
import pers.lee.common.lang.utils.ReflectUtils;

import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author YangYang
 * @version 0.1, 2008-9-20 22:25:39
 */
@SuppressWarnings("unchecked")
public class UniquePropertyAwareManagementService extends BaseManagementService {

    private static final String AND = " and ";

    private UniquePropertyConfiguration uniquePropertyConfiguration;

    public Object get(String name, Map properties) {
        Object object = super.get(name, properties);
        if (object == null && uniquePropertyConfiguration != null
                && uniquePropertyConfiguration.existUniqueProperty(name)) {
            for (Set<String> uniqueProperties : uniquePropertyConfiguration.getUniquePropertySet(name)) {
                object = getUniqueObject(properties, name, uniqueProperties);
                if (object != null) {
                    return object;
                }
            }
        }
        return object;
    }

    private Object getUniqueObject(Map properties, String name, Set<String> uniqueProperties) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("from ").append(name);
        stringBuffer.append(" where ");

        HashMap<String, Object> parameterMap = new HashMap();
        boolean searchable = false;
        for (String uniqueProperty : uniqueProperties) {
            if (properties.get(uniqueProperty) == null) {
                searchable = false;
                break;
            }
            searchable = true;
            stringBuffer.append(uniqueProperty);
            stringBuffer.append(" = :");
            if (properties.get(uniqueProperty) == null) {
                return null;
            }
            String parameter = getParameter(uniqueProperty);
            parameterMap.put(parameter, properties.get(uniqueProperty));
            stringBuffer.append(parameter);

            stringBuffer.append(AND);
        }
        if (!searchable) {
            return null;
        }
        stringBuffer.delete(stringBuffer.length() - AND.length(), stringBuffer.length());
        Query query = getEntityManager().createQuery(stringBuffer.toString());
//        Query query = getSession().createQuery(stringBuffer.toString());

        for (String parameter : parameterMap.keySet()) {
            query.setParameter(parameter, getStringValue(parameterMap.get(parameter)));
        }

        return query.getSingleResult();
    }

    private String getStringValue(Object object) {
        if (object instanceof Map) {
            Date date = Json.getDefault().toJavaObject(object, Date.class);
            String pattern = (String) ((Map<String, Object>) object).get("pattern");
            if (pattern == null) {
                pattern = "yyyy-MM-dd HH:mm:ss";
            }
            return new SimpleDateFormat(pattern).format(date);
        }
        return object.toString();
    }

    private String getParameter(String uniqueProperty) {
        return uniqueProperty.replace(".", "_");
    }

    public UniquePropertyConfiguration getUniquePropertyConfiguration() {
        return uniquePropertyConfiguration;
    }

    public void setUniquePropertyConfiguration(UniquePropertyConfiguration uniquePropertyConfiguration) {
        this.uniquePropertyConfiguration = uniquePropertyConfiguration;
    }

    protected Object save(String name, Map properties) {
        Class clazz = getEntityType(name).getJavaType();
        Object object = Json.getDefault().toJavaObject(properties, clazz);
        ReflectUtils.forIn(object.getClass(), new InnerEntityProcessor(object, this));
        return getEntityManager().merge(object);
    }


}

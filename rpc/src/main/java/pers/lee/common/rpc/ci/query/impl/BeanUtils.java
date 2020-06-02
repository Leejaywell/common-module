package pers.lee.common.rpc.ci.query.impl;

import pers.lee.common.rpc.ci.convertor.HibernateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author YangYang
 * @version 0.1, 2008-6-16 12:10:08
 */
@SuppressWarnings("unchecked")
public class BeanUtils {

	private static final Log log = LogFactory.getLog(BeanUtils.class);

	public static HashMap getObjectProperties(Object object, Set<String> propertyNames) {
		HashMap properties = new HashMap();
		HashMap allProperties = new HashMap((Map) HibernateUtils.convertEntity(object));
		if (propertyNames == null) {
			return allProperties;
		}
		for (String propertyName : propertyNames) {
			Object propertyValue = getPropertyValue(allProperties, propertyName);
			if (propertyValue != null) {
				properties.put(propertyName, propertyValue);
			}
		}
		return properties;
	}

	private static Object getPropertyValue(HashMap properties, String propertyName) {
		Object propertyValue;
		if (propertyName.indexOf(".") <= 0) {
			return properties.get(propertyName);
		}

		String[] partialPropertyNames = propertyName.split("\\.");
		propertyValue = properties;

		for (String partialPropertyName : partialPropertyNames) {
			if (!(propertyValue instanceof HashMap)) {
				log.warn("Invalid property name [" + propertyName + " ]");
				propertyValue = null;
			}
			propertyValue = ((HashMap) propertyValue).get(partialPropertyName);
		}
		return propertyValue;
	}

}

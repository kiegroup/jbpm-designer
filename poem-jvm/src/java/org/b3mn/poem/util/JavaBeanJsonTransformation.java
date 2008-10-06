package org.b3mn.poem.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;



public class JavaBeanJsonTransformation {

	public static JSONObject toJsonObject(Object bean, Collection<String> attributes) {
		// Initialize attributes with an empty collection if the parameter is null
		// to simplify coding below
		if (attributes == null) {
			attributes = new ArrayList<String>();
		}
		Class beanClass = bean.getClass();
		JSONObject jsonObject = new JSONObject();
		
		// Iterate over the class methods
		for (Method method : beanClass.getMethods()) {
			// Extract attribute name from method name 
			String attributeName = method.getName().substring(3);
			attributeName = attributeName.substring(0,1).toLowerCase() + attributeName.substring(1); 
			// If the method is a getter and either no attribute filter is given or 
			// the attribute collection contains the attribute name
			if ((method.getName().startsWith("get") || method.getName().startsWith("has") || 
					method.getName().startsWith("is")) && 
					(method.getParameterTypes().length == 0) && 
					((attributes.size() == 0) || (attributes.contains(attributeName)))) {
				try {
					// Call method without any parameters
					Object classAttribute = method.invoke(bean, new Object[0]);
					// Store name and value in the json object
					jsonObject.put(attributeName, classAttribute.toString());
				} catch (Exception e) {
					// If sth goes wrong ignore the method
				} 
			}
		}
		// Return string representation 
		return jsonObject; 

	}
	
	public static JSONObject toJsonObject(Object bean) {
		return toJsonObject(bean, null);
	}
	
	public static Object createJavaBean(String jsonString, Class javaClass) {
		try {
			return updateJavaBean(new JSONObject(jsonString), javaClass.newInstance());
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Object createJavaBean(JSONObject jsonObject, Class javaClass) {
		try {
			return updateJavaBean(jsonObject, javaClass.newInstance());
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Object updateJavaBean(String jsonString, Object bean) {
		try {
			return updateJavaBean(new JSONObject(jsonString), bean);
		} catch (JSONException e) {
			return null;
		}
	}
	
	public static Object updateJavaBean(JSONObject jsonObject, Object bean) {
		for (String attributeName : JSONObject.getNames(jsonObject)) {
			for (Method method : bean.getClass().getMethods()) {
				// If the class contains the setter 
				if (("set" + attributeName.toLowerCase()).
						equals(method.getName().toLowerCase()) && 
						method.getParameterTypes().length == 1) {
					try {
						// Check for a valid parameter type and invoke the setter
						String parameterType = method.getParameterTypes()[0].getName();
						if (parameterType.equals(int.class.getName())) {
							method.invoke(bean, jsonObject.getInt(attributeName));
						} else if (parameterType.equals(long.class.getName())) {
							method.invoke(bean, jsonObject.getLong(attributeName));
						} else if (parameterType.equals(boolean.class.getName())) {
							method.invoke(bean, jsonObject.getBoolean(attributeName));
						} else if (parameterType.equals(double.class.getName())) {
							method.invoke(bean, jsonObject.getDouble(attributeName));
						} else if (parameterType.equals(String.class.getName())) {
							method.invoke(bean, jsonObject.getString(attributeName));
						}
					} catch (Exception e) {
						// Ignore Exceptions here
					}
				}
			}
		}
		return bean;
	}	
}

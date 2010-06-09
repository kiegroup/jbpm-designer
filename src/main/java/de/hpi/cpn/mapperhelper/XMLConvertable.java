package de.hpi.cpn.mapperhelper;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public abstract class XMLConvertable {

	public static void registerMapping(XStream xstream) {
	}

	@SuppressWarnings("unchecked")
	public void parse(JSONObject modelElement) {
		Iterator jsonKeys = modelElement.keys();
		while (jsonKeys.hasNext()) {
			String key = (String) jsonKeys.next();
			String readMethodName = "readJSON" + key;
			if (hasJSONMethod(readMethodName)) {
				try {
					getClass().getMethod(readMethodName, JSONObject.class)
							.invoke(this, modelElement);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				readJSONunknown(modelElement, key);
			}
		}
	}

	public void readJSONunknown(JSONObject modelElement, String key) {
	}

	protected boolean hasJSONMethod(String methodName) {
		Method[] methods = getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)
					& hasMethodJSONParameter(methods[i], JSONObject.class)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected boolean hasMethodJSONParameter(Method method, Class jsonClass) {
		Class[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			if (parameterTypes[i].equals(jsonClass)) {
				return true;
			}
		}
		return false;
	}
}


package util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;

public class PrivateAccessor {
	public static Object invokePrivateMethod(Object o, String methodName,
			Object[] params) {
		Assert.assertNotNull(o);
		Assert.assertNotNull(methodName);
		Assert.assertNotNull(params);

		for (Method method : o.getClass().getDeclaredMethods()) {
			if (methodName.equals(method.getName())) {
				try {
					method.setAccessible(true);
					return method.invoke(o, params);
				} catch (IllegalAccessException ex) {
					Assert.fail("IllegalAccessException accessing "
							+ methodName);
				} catch (InvocationTargetException ite) {
					Assert.fail("InvocationTargetException accessing "
							+ methodName);
				}
			}
		}
		Assert.fail("Method '" + methodName + "' not found");
		return null;
	}

	public static Object getPrivateField(Object o, String fieldName) {
		// Check we have valid arguments...
		Assert.assertNotNull(o);
		Assert.assertNotNull(fieldName);

		// Go and find the private field...
		final Field fields[] = o.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; ++i) {
			if (fieldName.equals(fields[i].getName())) {
				try {
					fields[i].setAccessible(true);
					return fields[i].get(o);
				} catch (IllegalAccessException ex) {
					Assert
							.fail("IllegalAccessException accessing "
									+ fieldName);
				}
			}
		}
		Assert.fail("Field '" + fieldName + "' not found");
		return null;
	}
}

package com.andrewma.vision.webserver.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import android.util.Log;

import com.andrewma.vision.webserver.core.annotations.Action;

public abstract class Controller {
	private Map<String, Method> actions = new HashMap<String, Method>();

	public Controller() {
		final Method[] methods = getClass().getMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(Action.class)) {
				actions.put(method.getName().toLowerCase(), method);
			}
		}
	}

	protected Object execute(String actionString, String idString,
			Properties params) {
		try {
			final Method action = actions.get(actionString.toLowerCase());
			final Class<?>[] paramTypes = action.getParameterTypes();

			if (paramTypes == null || paramTypes.length == 0) {
				return actions.get(actionString.toLowerCase()).invoke(this);
			} else {
				if (int.class.equals(paramTypes[0])) {
					int id = Integer.parseInt(idString);
					return actions.get(actionString.toLowerCase()).invoke(this,
							id);
				} else {
					Object param = mapParamsToObject(params, paramTypes[0]);
					return actions.get(actionString.toLowerCase()).invoke(this,
							param);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private <E> E mapParamsToObject(Properties params, Class<E> typeToMap) {
		E result;
		try {
			result = typeToMap.newInstance();
			for (Enumeration<Object> e = params.keys(); e.hasMoreElements();) {
				final String fieldName = e.nextElement().toString();
				final String fieldValue = params.get(fieldName).toString();
				if (fieldValue != null && !fieldValue.equals("")) {
					final Field field = typeToMap.getField(fieldName);
					final Class<?> fieldType = field.getType();
					if (float.class.equals(fieldType)) {
						field.setFloat(result, Float.parseFloat(fieldValue));
					} else if (int.class.equals(fieldType)) {
						field.setInt(result, Integer.parseInt(fieldValue));
					} else if (boolean.class.equals(fieldType)) {
						field.setBoolean(result,
								"true".equals(fieldValue.toLowerCase()));
					} else if (String.class.equals(fieldType)) {
						field.set(result, fieldValue);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	protected abstract String getTag();
}

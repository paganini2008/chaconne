package indi.atlantis.framework.chaconne.dag;

import java.util.HashMap;
import java.util.Map;

import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * 
 * Context
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public final class Context {

	private Map<String, Object> data = new HashMap<String, Object>();

	public Object getAttribute(String attributeName) {
		return data.get(attributeName);
	}

	public void setAttribute(String attributeName, Object attributeValue) {
		if (attributeValue == null) {
			data.remove(attributeName);
		} else {
			data.put(attributeName, attributeValue);
		}
	}

	public boolean hasAttribute(String attributeName) {
		return data.containsKey(attributeName);
	}

	public void append(Map<String, ?> kwargs) {
		if (kwargs != null) {
			data.putAll(kwargs);
		}
	}

	public String[] attributeNames() {
		return data.keySet().toArray(new String[0]);
	}

	public int countOfAttributes() {
		return data.size();
	}

	public String getString(String attributeName) {
		return getString(attributeName, null);
	}

	public String getString(String attributeName, String defaultValue) {
		Object value = data.get(attributeName);
		if (value != null) {
			try {
				return String.class.cast(value);
			} catch (RuntimeException e) {
				return value.toString();
			}
		}
		return defaultValue;
	}

	public Integer getInteger(String attributeName) {
		return getInteger(attributeName, null);
	}

	public Integer getInteger(String attributeName, Integer defaultValue) {
		Object value = data.get(attributeName);
		if (value != null) {
			try {
				return Integer.class.cast(value);
			} catch (RuntimeException e) {
				return ConvertUtils.convertValue(value, Integer.class, defaultValue);
			}
		}
		return defaultValue;
	}

	public Long getLong(String attributeName) {
		return getLong(attributeName, null);
	}

	public Long getLong(String attributeName, Long defaultValue) {
		Object value = data.get(attributeName);
		if (value != null) {
			try {
				return Long.class.cast(value);
			} catch (RuntimeException e) {
				return ConvertUtils.convertValue(value, Long.class, defaultValue);
			}
		}
		return defaultValue;
	}

	public Double getDouble(String attributeName) {
		return getDouble(attributeName, null);
	}

	public Double getDouble(String attributeName, Double defaultValue) {
		Object value = data.get(attributeName);
		if (value != null) {
			try {
				return Double.class.cast(value);
			} catch (RuntimeException e) {
				return ConvertUtils.convertValue(value, Double.class, defaultValue);
			}
		}
		return defaultValue;
	}

	public Boolean getBoolean(String attributeName) {
		return getBoolean(attributeName, null);
	}

	public Boolean getBoolean(String attributeName, Boolean defaultValue) {
		Object value = data.get(attributeName);
		if (value != null) {
			try {
				return Boolean.class.cast(value);
			} catch (RuntimeException e) {
				return ConvertUtils.convertValue(value, Boolean.class, defaultValue);
			}
		}
		return defaultValue;
	}

}

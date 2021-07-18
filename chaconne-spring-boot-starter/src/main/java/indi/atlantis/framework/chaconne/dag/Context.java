/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package indi.atlantis.framework.chaconne.dag;

import java.util.Collections;
import java.util.LinkedHashMap;
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

	private Map<String, Object> data = new LinkedHashMap<String, Object>();

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public void set(Object value) {
		data.put("value", value);
	}

	public Object get() {
		return data.get("value");
	}

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

	public Map<String, Object> copy() {
		return Collections.unmodifiableMap(data);
	}

	public void merge(Context anotherContext) {
		if (anotherContext != null) {
			data.putAll(anotherContext.copy());
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

	public String toString() {
		return "[Context] " + data.toString();
	}

}

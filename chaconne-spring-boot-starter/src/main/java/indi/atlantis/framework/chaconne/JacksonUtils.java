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
package indi.atlantis.framework.chaconne;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * JacksonUtils
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public abstract class JacksonUtils {

	private static final ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static byte[] toJsonStringBytes(Object object) {
		try {
			return mapper.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static String toJsonString(Object object) {
		return toJsonString(object, false);
	}

	public static String toJsonString(Object object, boolean format) {
		try {
			if (format) {
				return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
			}
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static <T> T parseJson(byte[] bytes, Class<T> requiredType) {
		try {
			return mapper.readValue(bytes, requiredType);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static <T> T parseJson(String json, Class<T> requiredType) {
		try {
			return mapper.readValue(json, requiredType);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

}

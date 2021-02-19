package indi.atlantis.framework.jobby;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * JacksonUtils
 * 
 * @author Jimmy Hoff
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
		try {
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

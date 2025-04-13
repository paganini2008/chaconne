package com.github.chaconne.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.convert.ConversionService;

/**
 * 
 * @Description: BeanUtils
 * @Author: Fred Feng
 * @Date: 08/04/2025
 * @Version 1.0.0
 */
public abstract class BeanUtils {

    public static <T> T map2Bean(Map<String, Object> map, Class<T> clazz,
            ConversionService conversionService) throws Exception {
        try {
            T object = ConstructorUtils.invokeConstructor(clazz);
            Field[] fields = FieldUtils.getAllFields(clazz);
            String fieldName;
            Object fieldValue;
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                fieldName = field.getName();
                fieldValue = map.get(fieldName);
                if (fieldValue != null) {
                    try {
                        fieldValue = field.getType().cast(fieldValue);
                    } catch (ClassCastException e) {
                        if (conversionService != null && conversionService
                                .canConvert(fieldValue.getClass(), field.getType())) {
                            try {
                                fieldValue = conversionService.convert(fieldValue, field.getType());
                            } catch (RuntimeException ee) {
                                fieldValue = null;
                            }
                        } else {
                            fieldValue = null;
                        }
                    }
                    if (fieldValue != null) {
                        field.setAccessible(true);
                        field.set(object, fieldValue);
                    }
                }
            }
            return object;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static Map<String, Object> bean2Map(Object object) {
        return bean2Map(object, e -> true);
    }

    public static Map<String, Object> bean2Map(Object object, Predicate<Field> filter) {
        List<Field> fieldList = FieldUtils.getAllFieldsList(object.getClass());
        return fieldList.stream().filter(filter).collect(LinkedHashMap::new, (m, e) -> {
            if (Modifier.isStatic(e.getModifiers())) {
                return;
            }
            try {
                e.setAccessible(true);
                m.put(e.getName(), e.get(object));
            } catch (Exception ignored) {
            }
        }, LinkedHashMap::putAll);
    }

    public static void populateBean(Object object, Map<String, Object> kwargs,
            boolean overwritten) {
        populateBean(object, kwargs, overwritten, ConvertUtils.getDefaultConversionService());
    }

    public static void populateBean(Object object, Map<String, Object> kwargs, boolean overwritten,
            ConversionService conversionService) {
        PropertyDescriptor[] propertyDescriptors =
                org.springframework.beans.BeanUtils.getPropertyDescriptors(object.getClass());
        String propertyName;
        Object originalValue = null, latestValue;
        for (PropertyDescriptor pd : propertyDescriptors) {
            propertyName = pd.getName();
            latestValue = kwargs.get(propertyName);
            if (!overwritten) {
                try {
                    originalValue = FieldUtils.readDeclaredField(object, propertyName);
                } catch (Exception ingored) {
                }
                if (latestValue == null && originalValue != null) {
                    continue;
                }
            }
            try {
                latestValue = pd.getPropertyType().cast(latestValue);
            } catch (ClassCastException e) {
                try {
                    latestValue = ConvertUtils.convert(latestValue, pd.getPropertyType());
                } catch (Exception ingored) {
                    latestValue = null;
                }
            }
            try {
                FieldUtils.writeDeclaredField(object, propertyName, latestValue, true);
            } catch (Exception ingored) {
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(URI.create("lb://wwwbao/1"));
    }

}

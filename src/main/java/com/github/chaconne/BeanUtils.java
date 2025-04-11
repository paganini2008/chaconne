package com.github.chaconne;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 
 * @Description: BeanUtils
 * @Author: Fred Feng
 * @Date: 08/04/2025
 * @Version 1.0.0
 */
public abstract class BeanUtils {

    public static void populateBean(TaskInfoVo vo, Map<String, Object> kwargs) {
        Field[] fields = TaskInfoVo.class.getDeclaredFields();
        for (Field field : fields) {
            Object value = kwargs.get(field.getName());
            if (value != null) {
                field.setAccessible(true);
                try {
                    field.set(vo, value);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

}

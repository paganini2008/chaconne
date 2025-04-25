package com.github.chaconne;

import java.util.Map;

/**
 * 
 * @Description: CustomTaskFactory
 * @Author: Fred Feng
 * @Date: 25/04/2025
 * @Version 1.0.0
 */
public interface CustomTaskFactory {

    CustomTask createTaskObject(Map<String, Object> record);

}

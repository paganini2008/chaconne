package com.github.chaconne;

import com.github.chaconne.utils.ApplicationContextUtils;

/**
 * 
 * @Description: ManagedTaskInvocation
 * @Author: Fred Feng
 * @Date: 11/04/2025
 * @Version 1.0.0
 */
public class ManagedTaskInvocation extends DefaultTaskInvocation {

    @Override
    protected Object createTaskObject(Class<?> clz) {
        return ApplicationContextUtils.getOrCreateBean(clz);
    }

}

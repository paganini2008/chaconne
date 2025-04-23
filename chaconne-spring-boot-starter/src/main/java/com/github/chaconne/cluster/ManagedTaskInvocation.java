package com.github.chaconne.cluster;

import com.github.chaconne.DefaultTaskInvocation;
import com.github.chaconne.cluster.utils.ApplicationContextUtils;

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

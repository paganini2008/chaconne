package com.github.chaconne.cluster;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 
 * @Description: TaskMemberClusterModeSelector
 * @Author: Fred Feng
 * @Date: 28/05/2025
 * @Version 1.0.0
 */
public class TaskMemberClusterModeSelector implements ImportSelector {

    private static final String[] localClusterConfigClassNames =
            {TaskMemberClusterAutoConfiguration.class.getName(),
                    LocalTaskMemberClusterAutoConfiguration.class.getName()};
    private static final String[] remoteClusterConfigClassNames =
            {TaskMemberClusterAutoConfiguration.class.getName(),
                    RemoteTaskMemberClusterAutoConfiguration.class.getName()};

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes annotationAttributes =
                AnnotationAttributes.fromMap(importingClassMetadata
                        .getAnnotationAttributes(EnableTaskMemberCluster.class.getName()));
        ClusterMode clusterMode = (ClusterMode) annotationAttributes.get("mode");
        return clusterMode == ClusterMode.LOCAL ? localClusterConfigClassNames
                : remoteClusterConfigClassNames;
    }

}

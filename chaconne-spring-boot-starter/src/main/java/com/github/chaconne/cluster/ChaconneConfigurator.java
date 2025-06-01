package com.github.chaconne.cluster;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;

/**
 * 
 * @Description: ChaconneConfigurator
 * @Author: Fred Feng
 * @Date: 29/05/2025
 * @Version 1.0.0
 */
public class ChaconneConfigurator implements Configurator {

    private final HazelcastTaskMemberStore taskMemberStore;
    private final HazelcastTaskQueueStore taskQueueStore;

    public ChaconneConfigurator(HazelcastTaskMemberStore taskMemberStore,
            HazelcastTaskQueueStore taskQueueStore) {
        this.taskMemberStore = taskMemberStore;
        this.taskQueueStore = taskQueueStore;
    }

    @Override
    public void applyConfig(Config config) {
        MapStoreConfig mapStoreConfig =
                new MapStoreConfig().setEnabled(true).setImplementation(taskQueueStore)
                        .setWriteBatchSize(10).setWriteDelaySeconds(0).setWriteCoalescing(false);
        MapConfig taskQueueMapConfig = new MapConfig(HazelcastTaskQueue.DEFAULT_QUEUE_NAME);
        taskQueueMapConfig.setMapStoreConfig(mapStoreConfig);
        config.addMapConfig(taskQueueMapConfig);

        mapStoreConfig = new MapStoreConfig().setEnabled(true).setImplementation(taskMemberStore)
                .setWriteBatchSize(10).setWriteDelaySeconds(0).setWriteCoalescing(false);
        MapConfig taskMemberMapConfig =
                new MapConfig(HazelcastTaskMemberStore.TASK_MEMBER_STORE_NAME);
        taskMemberMapConfig.setMapStoreConfig(mapStoreConfig);
        config.addMapConfig(taskMemberMapConfig);
    }

}

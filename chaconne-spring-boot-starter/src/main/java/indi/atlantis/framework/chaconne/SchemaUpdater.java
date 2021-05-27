package indi.atlantis.framework.chaconne;

/**
 * 
 * SchemaUpdater
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface SchemaUpdater {

	void onCluserOnline() throws Exception;

	void onClusterOffline();

}

package indi.atlantis.framework.jobhub;

import indi.atlantis.framework.jobhub.server.ServerModeConfiguration;
import indi.atlantis.framework.jobhub.ui.UIModeConfiguration;

/**
 * 
 * DeployMode
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public enum DeployMode {

	EMBEDDED(EmbeddedModeConfiguration.class.getName()),

	SERVER(ServerModeConfiguration.class.getName()),

	UI(UIModeConfiguration.class.getName());

	private final String configurationClassName;

	private DeployMode(String configurationClassName) {
		this.configurationClassName = configurationClassName;
	}

	public String getConfigurationClassName() {
		return configurationClassName;
	}

}

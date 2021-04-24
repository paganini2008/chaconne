package indi.atlantis.framework.chaconne.console;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobLogForm
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobLogForm {

	private String jobKey;
	private long traceId;
	private String log;
	private String level;

}

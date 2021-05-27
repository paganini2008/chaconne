package indi.atlantis.framework.chaconne.console;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobTraceForm
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobTraceForm {

	private String jobKey;
	private Date startDate;
	private Date endDate;

}

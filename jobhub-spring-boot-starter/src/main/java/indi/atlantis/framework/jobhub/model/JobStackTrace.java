package indi.atlantis.framework.jobhub.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobStackTrace
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobStackTrace implements Serializable {

	private static final long serialVersionUID = -1363956445864067818L;

	private long traceId;
	private int jobId;
	private String stackTrace;

}

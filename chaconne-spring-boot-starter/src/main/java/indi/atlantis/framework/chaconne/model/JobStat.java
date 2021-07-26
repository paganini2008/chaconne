package indi.atlantis.framework.chaconne.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * JobStat
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Getter
@Setter
@ToString
public class JobStat implements Serializable {

	private static final long serialVersionUID = -1773410428525994119L;
	private int completedCount;
	private int skippedCount;
	private int failedCount;
	private int finishedCount;
	private int retryCount;
	private String executionDate;

}

package indi.atlantis.framework.chaconne.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import indi.atlantis.framework.chaconne.JobKey;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobDetail
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@JsonInclude(value = Include.NON_NULL)
@Getter
@Setter
public class JobDetail implements Serializable {

	private static final long serialVersionUID = 4349796691146506537L;
	private int jobId;
	private JobKey jobKey;
	private String description;
	private String attachment;
	private String email;
	private int retries;
	private int weight;
	private long timeout;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createDate;
	private JobRuntimeDetail jobRuntime;
	private JobTriggerDetail jobTriggerDetail;

}

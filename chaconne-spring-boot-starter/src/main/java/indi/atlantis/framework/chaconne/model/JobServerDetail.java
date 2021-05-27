package indi.atlantis.framework.chaconne.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobServerDetail
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobServerDetail implements Serializable {

	private static final long serialVersionUID = -6360857616979966695L;
	private String clusterName;
	private String groupName;
	private String instanceId;
	private String contextPath;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startDate;
	private String contactPerson;
	private String contactEmail;

}

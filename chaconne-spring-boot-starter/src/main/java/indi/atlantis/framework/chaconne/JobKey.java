package indi.atlantis.framework.chaconne;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;

import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.CharsetUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobKey
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Setter
@Getter
public final class JobKey implements Serializable, Comparable<JobKey> {

	private static final long serialVersionUID = 3147872689801742981L;
	private static final String IDENTIFIER_PATTERN = "%s.%s.%s@%s";
	private static final Charset DEFAULT_CHARSET = CharsetUtils.UTF_8;
	private String clusterName;
	private String groupName;
	private String jobName;
	private String jobClassName;

	public JobKey() {
	}

	JobKey(String clusterName, String groupName, String jobName, String jobClassName) {
		Assert.hasNoText(clusterName, "Cluster Name must be required");
		Assert.hasNoText(groupName, "Group Name must be required");
		Assert.hasNoText(jobName, "Job Name must be required");
		Assert.hasNoText(jobClassName, "Job Class Name must be required");
		this.clusterName = clusterName;
		this.groupName = groupName;
		this.jobName = jobName;
		this.jobClassName = jobClassName;
	}

	@JsonIgnore
	public String getIdentifier() {
		final String repr = String.format(IDENTIFIER_PATTERN, clusterName, groupName, jobName, jobClassName);
		return Base64Utils.encodeToString(repr.getBytes(DEFAULT_CHARSET));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (clusterName != null ? 0 : clusterName.hashCode());
		result = prime * result + (groupName != null ? 0 : groupName.hashCode());
		result = prime * result + (jobName != null ? 0 : jobName.hashCode());
		result = prime * result + (jobClassName != null ? 0 : jobClassName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof JobKey) {
			JobKey jobKey = (JobKey) obj;
			return jobKey.getClusterName().equals(getClusterName()) && jobKey.getGroupName().equals(getGroupName())
					&& jobKey.getJobName().equals(getJobName()) && jobKey.getJobClassName().equals(getJobClassName());
		}
		return false;
	}

	public String toString() {
		return String.format(IDENTIFIER_PATTERN, clusterName, groupName, jobName, jobClassName);
	}

	@Override
	public int compareTo(JobKey otherKey) {
		String left = String.format(IDENTIFIER_PATTERN, clusterName, groupName, jobName, jobClassName);
		String right = String.format(IDENTIFIER_PATTERN, otherKey.getClusterName(), otherKey.getGroupName(), otherKey.getJobName(),
				otherKey.getJobClassName());
		return left.compareTo(right);
	}

	public static JobKey of(JobDefinition jobDef) {
		Assert.isNull(jobDef, "JobDef instance must be required.");
		String clusterName = jobDef.getClusterName();
		String groupName = jobDef.getGroupName();
		String jobName = jobDef.getJobName();
		String jobClassName = jobDef.getJobClassName();
		return new JobKey(clusterName, groupName, jobName, jobClassName);
	}

	public static JobKey of(Map<String, Object> map) {
		String clusterName = (String) map.get("clusterName");
		String groupName = (String) map.get("groupName");
		String jobName = (String) map.get("jobName");
		String jobClassName = (String) map.get("jobClassName");
		return new JobKey(clusterName, groupName, jobName, jobClassName);
	}

	public static JobKey by(String clusterName, String groupName, String jobName, String jobClassName) {
		return new JobKey(clusterName, groupName, jobName, jobClassName);
	}

	public static JobKey decode(String identifier) {
		String repr;
		try {
			repr = new String(Base64Utils.decodeFromString(identifier), DEFAULT_CHARSET);
		} catch (RuntimeException e) {
			throw new IllegalJobKeyException(identifier, e);
		}
		int index = repr.lastIndexOf("@");
		if (index < 0) {
			throw new IllegalJobKeyException(repr);
		}
		String part = repr.substring(0, index);
		String[] args = part.split("\\.", 3);
		if (args.length != 3) {
			throw new IllegalJobKeyException(repr);
		}
		return new JobKey(args[0], args[1], args[2], repr.substring(index + 1));
	}

}

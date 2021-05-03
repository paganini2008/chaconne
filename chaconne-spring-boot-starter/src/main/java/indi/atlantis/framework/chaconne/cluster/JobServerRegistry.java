package indi.atlantis.framework.chaconne.cluster;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.chaconne.JobDao;
import indi.atlantis.framework.chaconne.JobQueryDao;
import indi.atlantis.framework.tridenter.ApplicationInfo;
import indi.atlantis.framework.tridenter.utils.BeanLifeCycle;
import indi.atlantis.framework.tridenter.utils.Contact;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JobServerRegistry
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class JobServerRegistry implements BeanLifeCycle {

	@Autowired
	private JobDao jobDao;

	@Autowired
	private JobQueryDao jobQueryDao;

	@Override
	public void configure() throws Exception {
		jobDao.cleanJobServerDetail();
		log.info("Clean up cluster registry ok.");
	}

	public void registerCluster(ApplicationInfo applicationInfo) {
		Map<String, Object> kwargs = new HashMap<String, Object>();
		kwargs.put("clusterName", applicationInfo.getClusterName());
		kwargs.put("groupName", applicationInfo.getApplicationName());
		kwargs.put("instanceId", applicationInfo.getId());
		kwargs.put("contextPath", applicationInfo.getApplicationContextPath());
		kwargs.put("startDate", new Timestamp(applicationInfo.getStartTime()));
		Contact contact = applicationInfo.getContact();
		kwargs.put("contactPerson", contact.getName());
		kwargs.put("contactEmail", contact.getEmail());
		jobDao.saveJobServerDetail(kwargs);
		log.info("Registered cluster: " + applicationInfo.getClusterName());
	}

	public void unregisterCluster(String clusterName) {
		jobDao.deleteJobServerDetail(Collections.singletonMap("clusterName", clusterName));
		log.info("Unregistered cluster: " + clusterName);
	}

	public String[] getClusterContextPaths(String clusterName) {
		List<String> results = jobQueryDao.selectContextPath(clusterName);
		return results.toArray(new String[0]);
	}

}

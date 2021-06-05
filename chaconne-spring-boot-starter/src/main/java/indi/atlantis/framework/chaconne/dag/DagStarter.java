package indi.atlantis.framework.chaconne.dag;

import org.slf4j.Logger;

import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.NotManagedJob;

/**
 * 
 * DagStarter
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class DagStarter implements NotManagedJob {

	@Override
	public Object execute(JobKey jobKey, Object attachment, Logger log) throws Exception {
		Context context = new Context();
		context.setAttribute("attachment", attachment);
		return context;
	}

}

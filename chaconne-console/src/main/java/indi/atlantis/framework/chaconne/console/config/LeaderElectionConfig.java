/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package indi.atlantis.framework.chaconne.console.config;

import org.springframework.context.annotation.Bean;

import indi.atlantis.framework.tridenter.election.CcrLeaderElection;
import indi.atlantis.framework.tridenter.election.CcrLeaderElectionListener;
import indi.atlantis.framework.tridenter.election.LeaderElection;
import indi.atlantis.framework.tridenter.election.LeaderElectionListener;

/**
 * 
 * LeaderElectionConfig
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
//@Configuration
public class LeaderElectionConfig {

	@Bean
	public LeaderElectionListener leaderElectionListener() {
		return new CcrLeaderElectionListener();
	}

	@Bean
	public LeaderElection leaderElection() {
		return new CcrLeaderElection();
	}
	
}

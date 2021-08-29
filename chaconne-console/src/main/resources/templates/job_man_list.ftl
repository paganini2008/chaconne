<#import "common/page.ftl" as pageToolbar>
<script type="text/javascript">
	$(function(){
		
		TableUtils.initialize(${(page.size)!10});
		
		TableUtils.rowColour();
	
		$('.tblCom').colResizable({
			  liveDrag:true, 
	          gripInnerHtml:"<div class='grip'></div>", 
	          draggingClass:"dragging", 
	          resizeMode:'fit',
	          disabledColumns: [0,9]
		});
	
		
	})
</script>
<div id="tabContent">
		<table border="0" cellspacing="0" cellpadding="0" class="tblCom" width="100%">
			<thead>
				<tr>
					<td width="3%">
						#
					</td>
					<td width="10%" class="tdLeft5">
						Cluster Name
					</td>
					<td width="8%" class="tdLeft5">
						Group Name
					</td>
					<td width="10%" class="tdLeft5">
						Job Name
					</td>
					<td class="tdLeft5">
						Job Class
					</td>
					<td width="12%" class="tdLeft5">
						Email
					</td>
					<td width="5%" class="tdRight5">
						Retries
					</td>
					<td width="7%" class="tdLeft5">
						State
					</td>
					<td width="4%">
						Type
					</td>
					<td width="12%">
						Create Date
					</td>
					<td width="16%" class="tdLeft5">
						&nbsp;
					</td>
				</tr>
			</thead>
			<tbody>
				<#if page ?? && page.results?? && page.results? size gt 0>
					<#list page.results as bean>
						<tr>
							<td width="3%">
							    <a href="${contextPath}/job/detail/${(bean.jobKey.identifier ? html)!}">${(page.page - 1) * (page.size) + (bean_index + 1)}</a>
							</td>
							<td width="10%" class="tdLeft5">
								${(bean.jobKey.clusterName)!}
							</td>
							<td width="8%" class="tdLeft5">
								${(bean.jobKey.groupName)!}
							</td>
							<td width="10%" class="tdLeft5" title="${(bean.jobKey.jobName)!}">
								${(bean.jobKey.jobName)!}
							</td>
							<td class="tdLeft5" title="${(bean.jobKey.jobClassName)!}">
								${(bean.jobKey.jobClassName)!}
							</td>
							<td width="12%" class="tdLeft5" title="${(bean.email)!}">
								${(bean.email)!}
							</td>
							<td width="5%" class="tdRight5">
								${(bean.retries)!}&nbsp;
							</td>
							<td width="7%" class="tdLeft5">
								${(bean.jobRuntime.jobState.repr)!}
							</td>
							<td width="4%">
								${(bean.jobTriggerDetail.triggerType.repr?substring(0,1))!}
							</td>
							<td width="12%">
								${(bean.createDate? string('yyyy-MM-dd HH:mm:ss'))!}
							</td>
							<td width="16%" class="tdLeft5">
								<#if bean.jobRuntime.jobState.repr == 'Scheduling' || bean.jobRuntime.jobState.repr == 'Paused'>
									<a class="pauseJob" href="${contextPath}/job/toggle/${(bean.jobKey.identifier? html)!}"><#if bean.jobRuntime.jobState.repr == 'Paused'>[Resume]<#else>[Pause]</#if></a>
									&nbsp;|
								</#if>
								<#if bean.jobRuntime.jobState.repr == 'Scheduling'>
									<a class="runJob" href="${contextPath}/job/trigger/${(bean.jobKey.identifier? html)!}">[Run]</a>
									&nbsp;|
									<a class="deleteJob" href="${contextPath}/job/delete/${(bean.jobKey.identifier? html)!}">[Delete]</a>
								</#if>
							</td>
						</tr>
					</#list>
				<#else>
					<tr>
						<td colspan="11">
							<p class="tabNoData">
								No data and please search again.
							</p>
						</td>
					</tr>
				</#if>
			</tbody>
		</table>
</div>
<#if page ?? && page.results?? && page.results? size gt 0>
	<@pageToolbar.page page = page display = 0/> 
</#if>
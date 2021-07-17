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
	          disabledColumns: [0,10]
		});
		
		$('.statDetail').click(function(){
			var jobId = $(this).attr("jobId");
			var url = '${contextPath}/job/stat/detail';
			$.ajax({
			    url: url,
				type:'post',
				contentType: 'application/json;',
				dataType:'json',
				data: JSON.stringify({
					"jobId": jobId
				}),
				success: function(data){
					var categories=[];
					var completedCount=[];
					var failedCount=[];
					var skippedCount=[];
					var finishedCount=[];
				    $.each(data.data,function(i,item){
				    	categories.push(item.executionDate);
				    	completedCount.push(item.completedCount);
				    	failedCount.push(item.failedCount);
				    	skippedCount.push(item.skippedCount);
				    	finishedCount.push(item.finishedCount);
				    });
				    showStatDetailChart(categories,completedCount,failedCount,skippedCount,finishedCount);
				}
			});
			return false;
		});
		
	})
	
	function showStatDetailChart(categories,completedCount,failedCount,skippedCount,finishedCount){
		$('#tabBox').html('');
		var chart = Highcharts.chart('tabBox',{
						chart: {
							type: 'area'
						},
						title: {
							text: 'Job Execution Result Statistics By Day'
						},
						xAxis: {
							categories: categories,
							allowDecimals: false
						},
						yAxis: {
							title: {
								text: 'Job Execution Counts'
							}
						},
						tooltip: {
					        pointFormat: '{series.name}: <b>{point.y:,.0f}</b>'
					    },
						series: [{
							name: 'Completed Count',
							data: completedCount
						}, {
							name: 'Failed Count',
							data: failedCount
						}, {
							name: 'Skipped Count',
							data: skippedCount
						}, {
							name: 'Finished Count',
							data: finishedCount
						}]
					});
	}
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
					<td width="10%" class="tdLeft5">
						Group Name
					</td>
					<td class="tdLeft5">
						Job Name
					</td>
					<td width="8%" class="tdLeft5">
						Completed
					</td>
					<td width="8%" class="tdLeft5">
						Failed
					</td>
					<td width="8%" class="tdLeft5">
						Skipped
					</td>
					<td width="8%" class="tdLeft5">
						Retries
					</td>
					<td width="12%" class="tdLeft5">
						Last Execution
					</td>
				</tr>
			</thead>
			<tbody>
				<#if page ?? && page.results?? && page.results? size gt 0>
					<#list page.results as bean>
						<tr>
							<td width="3%">
							    <a class="statDetail" jobId="${(bean.jobId)!}" href="javascript:void(0);">${(page.page - 1) * (page.size) + (bean_index + 1)}</a>
							</td>
							<td width="10%" class="tdLeft5">
								${(bean.clusterName)!}
							</td>
							<td width="10%" class="tdLeft5">
								${(bean.groupName)!}
							</td>
							<td class="tdLeft5">
								${(bean.jobName)!}
							</td>
							<td width="8%" class="tdLeft5">
								${(bean.completedCount)!}
							</td>
							<td width="8%" class="tdLeft5">
								${(bean.failedCount)!}
							</td>
							<td width="8%" class="tdLeft5">
								${(bean.skippedCount)!}
							</td>
							<td width="8%" class="tdLeft5">
								${(bean.retryCount)!}
							</td>
							<td width="12%" class="tdLeft5">
								${(bean.executionDate?html)!}
							</td>
						</tr>
					</#list>
				<#else>
					<tr>
						<td colspan="9">
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
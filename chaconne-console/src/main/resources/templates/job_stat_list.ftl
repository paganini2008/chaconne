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
	          disabledColumns: [0,8]
		});
		
		$('.statDetail').click(function(){
			$('#tabBox').html('<div id="statByMonth"></div><div id="statByDay"></div>');
			var jobId = $(this).attr("jobId");
			statByMonth(jobId);
			statByDay(jobId);
			return false;
		});
		
	})
	
	function statByMonth(jobId){
		var url = '${contextPath}/job/stat/detail/month';
			$.ajax({
			    url: url,
				type:'post',
				contentType: 'application/json;',
				data: JSON.stringify({
					"jobId": jobId
				}),
				dataType:'json',
				success: function(data){
					var categories=['Completed Count', 'Failed Count', 'Skipped Count', 'Finished Count', 'Retry Count'];
					var series=[];
				    $.each(data.data,function(i,item){
				    	series.push({
				    		name: item.executionDate,
				    		data: [item.completedCount,item.failedCount,item.skippedCount,item.finishedCount,item.retryCount]
				    	});
				    });
				    showBarChart(categories, series);
				}
			});
	}
	
	function statByDay(jobId){
		var url = '${contextPath}/job/stat/detail/day';
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
	}
	
	function showBarChart(categories,data){
		Highcharts.chart('statByMonth', {
		    chart: {
		        type: 'bar',
		        backgroundColor: '#F0F0F0'
		    },
		    title: {
		        text: '<font style="font-weight: 800;">Job Execution Result Statistics</font>'
		    },
		    xAxis: {
		        categories: categories,
		        title: {
		            text: null
		        }
		    },
		    yAxis: {
		        min: 0,
		        title: {
		            text: 'Execution Count',
		            align: 'high'
		        },
		        labels: {
		            overflow: 'justify'
		        }
		    },
		    tooltip: {
		        valueSuffix: ' '
		    },
		    plotOptions: {
		        bar: {
		            dataLabels: {
		                enabled: true
		            }
		        }
		    },
		    legend: {
		        layout: 'vertical',
		        align: 'right',
		        verticalAlign: 'top',
		        x: 0,
		        y: 0,
		        floating: true,
		        borderWidth: 1,
		        backgroundColor:
		            Highcharts.defaultOptions.legend.backgroundColor || '#FFFFFF',
		        shadow: true
		    },
		    credits: {
		        enabled: false
		    },
		    exporting: false,
		    series: data
		});
	}
	
	function showStatDetailChart(categories,completedCount,failedCount,skippedCount,finishedCount){
		var chart = Highcharts.chart('statByDay',{
						chart: {
							type: 'area',
							backgroundColor: '#F0F0F0'
						},
						title: {
							text: '<font style="font-weight: 800;">Job Execution Result Statistics By Day</font>'
						},
						exporting: false,
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
					<td width="10%" class="tdLeft5">
						Job Name
					</td>
					<td class="tdLeft5">
						Job Class
					</td>
					<td width="8%" class="tdLeft5">
						Completed
					</td>
					<td width="6%" class="tdLeft5">
						Failed
					</td>
					<td width="6%" class="tdLeft5">
						Skipped
					</td>
					<td width="6%" class="tdLeft5">
						Retries
					</td>
					<td width="15%">
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
							<td width="10%" class="tdLeft5">
								${(bean.jobName)!}
							</td>
							<td class="tdLeft5">
								${(bean.jobClassName)!}
							</td>
							<td width="8%" class="tdLeft5">
								${(bean.completedCount)!}
							</td>
							<td width="6%" class="tdLeft5">
								${(bean.failedCount)!}
							</td>
							<td width="6%" class="tdLeft5">
								${(bean.skippedCount)!}
							</td>
							<td width="6%" class="tdLeft5">
								${(bean.retryCount)!}
							</td>
							<td width="15%" class="tdLeft5">
								${(bean.executionDate?html)!}
							</td>
						</tr>
					</#list>
				<#else>
					<tr>
						<td colspan="10">
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
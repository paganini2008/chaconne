<#setting number_format="#">
<#include "head.ftl">
<style type="text/css">
	#right {
		overflow-x: hidden;
		overflow-y: scroll;
	}

	#jobStat {
    	height: 320px;
    	width: calc(100% - 20px);
    	margin: 0px auto;
    	clear: both;
    }
    
    #jobStat1 {
    	float: left;
    	height: 320px;
    	width: 49%;
    	border-top-left-radius:30px;
    }
    
    #jobStat2{
    	float: left;
    	height: 320px;
    	width: 51%;
    }

	#tabBox {
		clear: both;
		height: 360px;
		width: calc(100% - 20px);
    	margin: 0px auto;
    	border-bottom-left-radius:30px;
	}
	
	.division{
		clear: both;
		height: 10px;
		width: 100%;
	}
    
	    
</style>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="https://code.highcharts.com/modules/export-data.js"></script>
<script src="https://code.highcharts.com/modules/accessibility.js"></script>
<script type="text/javascript">
	
	function main(){
	
		showJobStateCount();
		
		showJobStat();
		
		showJobStatDetail();
	}
	
	function showJobStateCount(){
			var url = '${contextPath}/overview/job/state';
			$.ajax({
			    url: url,
				type:'post',
				contentType: 'application/json;',
				dataType:'json',
				success: function(data){
					var series = [];
				    $.each(data.data,function(i,item){
				    	series.push({
				    		name: item.displayJobState,
				    		y: item.jobCount
				    	});
				    });
				    showPieChart('jobStat1','Job Execution State Statistics',series);
				}
			});
	}
	
	function showJobStat(){
		    var url = '${contextPath}/overview/job/stat';
			$.ajax({
			    url: url,
				type:'post',
				contentType: 'application/json;',
				dataType:'json',
				success: function(data){
					var series = [];
					var jobStat = data.data;
				    series.push({
				    	name: 'Completed Count',
				    	y: jobStat.completedCount
				    });
				    series.push({
				    	name: 'Failed Count',
				    	y: jobStat.failedCount
				    });
				    series.push({
				    	name: 'Skipped Count',
				    	y: jobStat.skippedCount
				    });
				    series.push({
				    	name: 'Finished Count',
				    	y: jobStat.finishedCount
				    });
				    showPieChart('jobStat2','Job Execution Result Statistics',series);
				}
			});
	}
	
	function showJobStatDetail(){
			var url = '${contextPath}/overview/job/stat/detail';
			$.ajax({
			    url: url,
				type:'post',
				contentType: 'application/json;',
				dataType:'json',
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
	
	function showPieChart(divId, title, data){
		Highcharts.chart(divId, {
				chart: {
						plotBackgroundColor: null,
						plotBorderWidth: null,
						plotShadow: false,
						type: 'pie',
						backgroundColor: '#fff'
				},
				title: {
						text: '<font style="font-weight: 800;">' + title + '</font>'
				},
				exporting: false,
				tooltip: {
						pointFormat: '{series.name}: <b>&nbsp;{point.y}/{point.percentage:.1f} %</b>'
				},
				accessibility: {
			        point: {
			            valueSuffix: '%'
			        }
			    },
				plotOptions: {
						pie: {
								allowPointSelect: true,
								cursor: 'pointer',
								dataLabels: {
										enabled: true,
										format: '<b>{point.name}</b>:&nbsp;{point.y}/{point.percentage:.1f} %',
								}
						}
				},
				series: [{
						name: 'Count/Percentage',
						colorByPoint: true,
						data: data
				}]
			});
	}

	function showStatDetailChart(categories,completedCount,failedCount,skippedCount,finishedCount){
		$('#tabBox').html('');
		var chart = Highcharts.chart('tabBox',{
						chart: {
							type: 'area',
							backgroundColor: '#fff'
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
	
	$(function(){
		main();
	});
</script>
<body>
		<div id="top">
			<#include "top.ftl">
		</div>
		<div id="container">
			<div id="left">
				<#include "nav.ftl">
			</div>
			<div id="right">
				<div class="division"></div>
				<div id="jobStat">
					<div id="jobStat1"></div>
					<div id="jobStat2"></div>
				</div>
				<div id="tabBox">
				</div>
				<div class="division"></div>
			</div>
		</div>
		<#include "foot.ftl">
</body>
</html>
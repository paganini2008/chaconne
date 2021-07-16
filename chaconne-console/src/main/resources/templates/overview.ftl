<#setting number_format="#">
<#include "head.ftl">
<style type="text/css">
	#jobStat{
    	height: 60px;
    	width: 100%;
    	clear: both;
    }

	#tabBox {
		height: auto;
		width: 100%;
		position: relative;
		bottom: 5px;
	}
    
	    
</style>
<script type="text/javascript" src="${contextPath}/static/js/common.js"></script>
<script src="https://cdn.highcharts.com.cn/highcharts/highcharts.js"></script>
<script src="https://cdn.highcharts.com.cn/highcharts/modules/exporting.js"></script>
<script src="https://img.hcharts.cn/highcharts-plugins/highcharts-zh_CN.js"></script>
<script src="https://cdn.highcharts.com.cn/highcharts/themes/sand-signika.js"></script>
<script type="text/javascript">
	$(function(){
		onLoad();
	});
	
	function onLoad(){
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
				    		name: item.jobState.repr,
				    		y: item.jobCount
				    	});
				    });
				    showStatChart('Job Execution State Statistics',series);
				}
			});
			
			url = '${contextPath}/overview/job/stat';
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
				    showStatChart('Job Execution Result Statistics',series);
				}
			});
	}
	
	function showStatChart(title, data){
		Highcharts.chart('jobStat', {
				chart: {
						plotBackgroundColor: null,
						plotBorderWidth: null,
						plotShadow: false,
						type: 'pie'
				},
				title: {
						text: title
				},
				tooltip: {
						pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
				},
				plotOptions: {
						pie: {
								allowPointSelect: true,
								cursor: 'pointer',
								dataLabels: {
										enabled: true,
										format: '<b>{point.name}</b>: {point.percentage:.1f} %',
										style: {
												color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
										}
								}
						}
				},
				series: [{
						name: 'Brands',
						colorByPoint: true,
						data: data
				}]
			});
	}

	function showStatDetailChart(categories,completedCount,failedCount,skippedCount,retryCount){
		$('#tabBox').html('');
		var chart = Highcharts.chart('tabBox',{
						chart: {
							type: 'area'
						},
						title: {
							text: 'Statistics Detail'
						},
						xAxis: {
							categories: categories,
							allowDecimals: false
						},
						yAxis: {
							title: {
								text: 'Statistics Metrics'
							}
						},
						tooltip: {
					        pointFormat: '{series.name}: <b>{point.y:,.0f}</b>'
					    },
						series: [{
							name: 'CompletedCount',
							data: completedCount
						}, {
							name: 'FailedCount',
							data: failedCount
						}, {
							name: 'SkippedCount',
							data: skippedCount
						}, {
							name: 'RetryCount',
							data: retryCount
						}]
					});
	}
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
				<div id="jobStat">
				</div>
				<div id="tabBox">
				</div>
			</div>
		</div>
		<#include "foot.ftl">
</body>
</html>
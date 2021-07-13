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
				<div id="jobStat">
					<p>
					    <label>Not Scheduled: </label>${(dataMap['Not Scheduled'].jobCount)!0}
						<label>Scheduling: </label>${(dataMap['Scheduling'].jobCount)!0}
						<label>Running: </label>${(dataMap['Running'].jobCount)!0}
					</p>
					<p>
						<label>Paused: </label>${(dataMap['Paused'].jobCount)!0}
						<label>Finished: </label>${(dataMap['Finished'].jobCount)!0}
						<label>Frozen: </label>${(dataMap['Frozen'].jobCount)!0}
					</p>
				</div>
				<div id="tabBox">
				</div>
			</div>
		</div>
		<#include "foot.ftl">
</body>
</html>
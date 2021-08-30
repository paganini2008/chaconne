<#setting number_format="#">
<#include "head.ftl">
<style type="text/css">
	
	#tabContent{
		height: auto;
	}
	
	.jobLine{
		clear: both;
		height: 64px;
		line-height: 64px;
		text-align: left;
		width: 100%;
		overflow: hidden;
	}
	
	.jobLine label {
		font-size: 16pt;
		font-weight: 800;
		margin-left: 15px;
		display: inline-block;
		float: left;
	}
	
	.jobAttr {
		clear: both;
		height: 32px;
		line-height: 32px;
		width: 100%;
		overflow: hidden;
	}
	    
	.jobAttr label{
		width: 180px;
		height: 32px;
		line-height: 32px;
		display: inline-block;
		text-align: right;
		padding-right: 5px;
		float: left;
		vertical-align: top;
		font-weight: 800;
	}
	
	.jobAttr span {
		width: calc(50% - 300px);
		float: left;
		display: inline-block;
		height: 32px;
		line-height: 32px;
		text-align: left;
		padding-left: 5px;
	}
    
    #triggerDescription{
    	width: calc(100% - 200px); 
    	height: auto; 
    	text-align: left;
    	background-color: #3C3C3C;
    	border: 2px solid #000;
    	border-radius: 10px;
    }
	    
</style>
<script type="text/javascript">
	$(function(){
		$('#searchForm').submit(function(){
			var obj = $(this);
			var url = obj.attr('action');
			$.ajax({
			    url: url,
				type:'post',
				dataType:'html',
				data: obj.serialize(),
				success: function(data){
				    $('#tabBox').html(data);
				}
			});
			return false;
		});
		
		$('#saveBtn').click(function(){
	    	window.location.href= "${contextPath}/job/edit/${(jobDetail.jobKey.identifier)!}";
	    });
	
		onLoad();
	});
	
	function onLoad(){
		$('#searchForm').submit();
	}
	
</script>
<script type="text/javascript" src="${contextPath}/static/js/common.js"></script>
<body>
		<div id="top">
			<#include "top.ftl">
		</div>
		<div id="container">
			<div id="left">
				<#include "nav.ftl">
			</div>
			<div id="right">
				<div id="jobDetail">
					<div class="jobLine">
						<label>Job Basic Info:</label>
					</div>
					<div class="jobKey jobAttr">
						<label>Cluster Name:</label>
						<span id="clusterName">${(jobDetail.jobKey.clusterName)!}</span>
						<label>Group Name:</label>
						<span id="groupName">${(jobDetail.jobKey.groupName)!}</span>
					</div>
					<div class="jobKey jobAttr">
						<label>Job Name:</label>
						<span id="jobName">${(jobDetail.jobKey.jobName)!}</span>
						<label>Job Class Name:</label>
						<span id="jobClassName">${(jobDetail.jobKey.jobClassName)!}</span>
					</div>
					<#assign triggerType = jobDetail.jobTriggerDetail.triggerType.value!>
					<div class="triggerDetail jobAttr">
						<label>Trigger Type:</label>
						<span id="triggerType">${(jobDetail.jobTriggerDetail.triggerType.repr)!}</span>
						<label>Repeat Count:</label>
						<span id="repeatCount">${(jobDetail.jobTriggerDetail.repeatCount)!}</span>
					</div>
					<div class="triggerDetail jobAttr">
						<label>Start Date:</label>
						<span id="triggerStartDate">${(jobDetail.jobTriggerDetail.startDate?string('yyyy-MM-dd HH:mm:ss'))!'-'}</span>
						<label>End Date:</label>
						<span id="triggerEndDate">${(jobDetail.jobTriggerDetail.endDate?string('yyyy-MM-dd HH:mm:ss'))!'-'}</span>
					</div>
					<div class="triggerDescription jobAttr" style="height: auto;">
						<label>Trigger Description:</label>
						<span id="triggerDescription">
							<pre>${(jobDetail.jobTriggerDetail.triggerDescription?html)!'-'}</pre>
						</span>
					</div>
					<div class="jobRuntime jobAttr">
						<label>Job State:</label>
						<span id="jobState">${(jobDetail.jobRuntime.jobState.repr)!}</span>
						<label>Last Running State:</label>
						<span id="lastRunningState">${(jobDetail.jobRuntime.lastRunningState.repr)!}</span>
					</div>
					<div class="jobRuntime jobAttr">
						<label>Last Execution Time:</label>
						<span id="lastExecutionTime">${(jobDetail.jobRuntime.lastExecutionTime?string('yyyy-MM-dd HH:mm:ss'))!}</span>
						<label>Last Completion Time:</label>
						<span id="lastCompletionTime">${(jobDetail.jobRuntime.lastCompletionTime?string('yyyy-MM-dd HH:mm:ss'))!}</span>
					</div>
				</div>
				<div id="searchBox">
					<form class="pageForm" id="searchForm" method="post" action="${contextPath}/job/trace">
						<input type="hidden" value="${(page.page)!}" name="page" id="pageNo"/>
						<input type="hidden" value="${(jobDetail.jobKey.identifier)!}" name="jobKey"/>
						<input type="button" value="Edit Your Job" id="saveBtn"></input>
					</form>
				</div>
				<div id="tabBox">
				</div>
			</div>
		</div>
		<#include "foot.ftl">
</body>
</html>
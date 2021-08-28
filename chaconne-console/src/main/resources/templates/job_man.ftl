<#setting number_format="#">
<#include "head.ftl">
<style type="text/css">

    #searchBox{
    	height: 60px;
    	width: 100%;
    	clear: both;
    }

	#tabBox {
		height: calc(100% - 60px);
		width: 100%;
	}
	    
	
	#tabContent{
		height: auto;
	}
	
	#saveBtn{
		width: calc(100% - 20px);
		height: 36px;
		line-height: 36px;
		padding: 5px auto;
		cursor: pointer;
		text-align: center;
		font-weight: 800;
		float: left;
		display: inline-block;
		margin: 10px 10px;
		background-color: #97CBFF;
	}
	    
</style>
<script type="text/javascript" src="${contextPath}/static/js/common.js"></script>
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
			window.location.href= "${contextPath}/job/edit";
		});
	
		onLoad();
	});
	
	function onLoad(){
		$('#searchForm').attr('action','${contextPath}/job');
		$('#searchForm').submit();
	}
	
</script>
<#import "common/page.ftl" as pageToolbar>
<body>
		<div id="top">
			<#include "top.ftl">
		</div>
		<div id="container">
			<div id="left">
				<#include "nav.ftl">
			</div>
			<div id="right">
				<div id="searchBox">
					<form class="pageForm" id="searchForm" action="${contextPath}/job">
						<input type="hidden" value="${(page.page)!}" name="page" id="pageNo"/>
						<input type="button" value="Create Your Job" id="saveBtn"></input>
					</form>
				</div>
				<div id="tabBox">
				</div>
			</div>
		</div>
		<#include "foot.ftl">
</body>
</html>
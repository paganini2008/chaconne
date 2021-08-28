<#setting number_format="#">
<#include "head.ftl">
<style type="text/css">
	#right {
		overflow-x: hidden;
		overflow-y: scroll;
	}

	#searchBox{
    	height: 60px;
    	width: 100%;
    	clear: both;
    }

	#tabBox {
		height: calc(100%-60px);
		width: 100%;
		clear: both;
	}
	    
	
	#tabContent{
		height: auto;
	}
    
	    
</style>
<script type="text/javascript" src="${contextPath}/static/js/common.js"></script>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="https://code.highcharts.com/modules/export-data.js"></script>
<script src="https://code.highcharts.com/modules/accessibility.js"></script>
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
	
		onLoad();
	});

	function onLoad(){
		$('#searchForm').attr('action','${contextPath}/job/stat/list');
		$('#searchForm').submit();
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
				<div id="searchBox">
					<form class="pageForm" id="searchForm" method="post">
						<input type="hidden" value="${(page.page)!1}" name="page" id="pageNo"/>
					</form>
				</div>
				<div id="tabBox">
				</div>
			</div>
		</div>
		<#include "foot.ftl">
</body>
</html>
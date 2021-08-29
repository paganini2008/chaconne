<#setting number_format="#">
<#include "head.ftl">
<style type="text/css">

	#tabBox {
		height: calc(100%-60px);
	}
	
	#tabContent{
		height: auto;
	}
	
	#statByMonth{
		border-top-left-radius: 20px;
	}
	
    #statByDay{
		border-bottom-left-radius: 20px;
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
				<div class="division"></div>
			</div>
		</div>
		<#include "foot.ftl">
</body>
</html>
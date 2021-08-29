<script type="text/javascript">
	$(function(){
		$('#navBox li').hover(function(){
			$(this).css({'background-color':'#F0F0F0','border-radius':'8px','color':'#000'});
			$(this).find('a').css('color','#000');
		}, function(){
			$(this).css({'background-color':'','border-radius':'','color':'#fff'});
			$(this).find('a').css('color','#fff');
		});
	});
</script>
<style type="text/css">
</style>
<ul id="navBox">
	<li><a href="${contextPath}/index/1">Overview</a><em>&gt;</em></li>
	<li><a href="${contextPath}/index/2">Job Management</a><em>&gt;</em></li>
	<li><a href="${contextPath}/index/3">Job Statistics</a><em>&gt;</em></li>
	<li><a href="${contextPath}/index/4">Cron Editor</a><em>&gt;</em></li>
	<li><a href="${contextPath}/index/5">Quick Start</a><em>&gt;</em></li>
</ul>
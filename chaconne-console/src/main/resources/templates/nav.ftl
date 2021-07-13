<script type="text/javascript">
	$(function(){
		$('#navBox li').hover(function(){
			$(this).css({'background-color':'#F0F0F0','border-radius':'8px'});
		}, function(){
			$(this).css({'background-color':'', 'border-radius':''});
		});
	});
</script>
<style type="text/css">
	#navBox li a {
		color: #000;
	}
</style>
<ul id="navBox">
	<li><a href="${contextPath}/index/1">Overview</a></li>
	<li><a href="${contextPath}/index/2">Job Management</a></li>
	<li><a href="${contextPath}/index/3">Job Statistics</a></li>
	<li><a href="${contextPath}/index/4">Cron Editor</a></li>
	<li><a href="${contextPath}/index/5">Quick Start</a></li>
</ul>
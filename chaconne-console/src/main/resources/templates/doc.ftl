<#setting number_format="#">
<#include "head.ftl">
<style type="text/css">

	#tabBox {
		width: 100%;
		height: 120px;
		min-height: 120px;
		position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
       	margin: auto;
	}
	
	.hidden{
		width: 100%;
		font-weight: bold;
	}
	    
</style>
<body>
	<div id="top">
		<#include "top.ftl">
	</div>
	<div id="container">
		<div id="left">
			<#include "nav.ftl">
		</div>
		<div id="right">
			<iframe name="quickStartFrm" id="quickStart" src="${contextPath}/static/file/doc.html" frameborder="0" align="left" width="100%" height="100%" scrolling="yes">
    			<p>Your browser does not support iframe tag</p>
			</iframe>
		</div>
	</div>
	<#include "foot.ftl">
</body>
</html>
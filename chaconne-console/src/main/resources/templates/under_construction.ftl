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
				<div id="tabBox">
					<div class="hidden" style="font-size: 32pt; height: 64px; line-height: 64px;">Under Construction</div>
					<div class="hidden" style="font-size: 12px; height: 32px; line-height: 32px;">New Function will be here soon! </div>
				</div>
			</div>
		</div>
		<#include "foot.ftl">
	</body>
</html>
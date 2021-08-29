<#setting number_format="#">
<style type="text/css">
	#logBox {
		height: 100%;
	}
	
	#logTitle{
		clear: both;
		text-align: left; 
		height: 32px; 
		line-height: 32px;
		width: calc(100% - 20px);
		margin: 0 auto;
	}
	
	.show-error {
		color: #FF0000;
		cursor: pointer;
		font-weight: 800;
	}
	
	.show-info {
		color: #00BB00;
	}
	
	.show-warn {
		color: #D9B300;
	}
	
	.show-debug {
		color: #0066CC;
	}
</style>
<script type="text/javascript">
	$(function(){
		
		$('.show-error').click(function(){
			var traceId = $(this).parent().attr("traceId");
			var url = '${contextPath}/job/error/${jobKey!}';
			$.ajax({
			    url: url,
				type:'get',
				dataType:'html',
				data: {
					"traceId": traceId
				},
				success: function(data){
				    $('#tabBox').html(data);
				}
			});
		});
		
	})
</script>
	<div id="logTitle">
		<b>Slf4j Logging: </b>
	</div>
<div id="logBox">
	<#list logs as log>
		<div class="logItem" traceId="${(log.traceId? string)!}">
			${(log.createDate ? string('yyyy-MM-dd HH:mm:ss'))!} [<font class="show-${(log.level ? lower_case)!}">${(log.level ? upper_case)!}&nbsp;&nbsp;</font>] - ${(log.log ? html)!}
		</div>
	</#list>
</div>
		
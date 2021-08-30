<#setting number_format="#">
<#include "head.ftl">
<style type="text/css">
	
	#jsonBox {
		width: calc(100% - 20px);
		height: calc(100% - 80px);
		clear: both;
		text-align: left;
		margin: 0 auto;
	}
	
	#jsonEditor {
		width: 100%;
	}
	
	#jsonData {
		width: 100%;
		height: auto;
		display: none;
		background-color: #3C3C3C;
		border: 2px solid #000;
		border-radius: 20px;
		margin-top: 10px;
	}
	
	#jsonData pre{
		margin: 10px;
	}
	
	#editBtn {
		background-color: #0066CC;
		color: #fff;
		display: inline-block;
		float: left;
		margin-left: 10px;
		height: 32px;
		line-height: 32px;
		text-align: center;
		font-weight: 800;
		padding: 2px 10px;
		border-radius: 6px;
		cursor: pointer;
	}

	    
</style>
<link href="${contextPath}/static/css/json-editor.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${contextPath}/static/js/common.js"></script>
<script type="text/javascript" src="${contextPath}/static/js/lib/jquery-json-editor.js"></script>
<script type="text/javascript">
    
	var dataJson; 
    
	$(function(){
	
		var example = JSON.parse('${jobDefinition}');
		$('#jsonData > pre').html(JSON.stringify(JSON.parse('${jobDefinition}'), null, 4));
	
		$('#jsonEditor').jsonEditor(example, { change: function(data) {
				dataJson = JSON.stringify(data);
				$('#jsonData > pre').html(JSON.stringify(JSON.parse(dataJson), null, 4));
			}
	    });
	    
	    $('#saveBtn').click(function(){
	    	var url = '${contextPath}/job/save';
	    	$.ajax({
			    url: url,
				type:'post',
				contentType: "application/json; charset=utf-8",
				dataType:'json',
				data: dataJson,
				success: function(data){
				    if(data.success){
				    	alert('Save OK');
				    	window.location.href = '${contextPath}/job';
				    }else{
				    	alert('Oh, no. Reason: ' + data.msg);
				    }
				}
			});
	    });
	    
	    $('#editBtn').toggle(function(){
	    	$(this).html('Edit Json');
	    	$('#jsonData').show();
	    	$('#jsonEditor').hide();
	    },function(){
	    	$(this).html('Show Json');
	    	$('#jsonData').hide();
	    	$('#jsonEditor').show();
	    });
	    
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
				<div id="searchBox">
					<input type="button" value="Save Your Job" id="saveBtn"></input>
				</div>
				<div id="jsonBox">
					<div style="width: 100%; height: 36px; line-height: 36px; text-align: left;"><span id="editBtn">Show Json</span></div>
					<div id="jsonEditor" class="json-editor"></div>
					<div id="jsonData">
						<pre></pre>
					</div>
					<div style="width: 100%; height: 10px; clear: both;">
					</div>
				</div>
			</div>
		</div>
		<#include "foot.ftl">
</body>
</html>
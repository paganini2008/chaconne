<script type="text/javascript">
	$(function(){
		
		onLoad();
	
		$('#currentClusterName').change(function(){
			var clusterName = $.trim($(this).val());
			var url = '${contextPath}/index/${navIndex!0}?clusterName=' + clusterName;
			window.location.href = url;
		});
	});
	
	function onLoad(){
		$.ajax({
			    url: '${contextPath}/clusters',
				type:'get',
				dataType:'json',
				success: function(data){
					var html = '';
				    if(data.success == true){
				    	$.each(data.data,function(i,item){
				    		html += '<option value="' + item + '">' + item;
				    		html += '</option>';
				    	});
				    }
					$(html).appendTo($('#currentClusterName'));
					$('#currentClusterName option').each(function(){
						if($(this).text() == '${currentClusterName!}'){
							$(this).attr('selected','true');
						}
					});
				}
		});
	}
	
</script>
<a id="logoText" href="https://github.com/paganini2008/chaconne.git" target="_blank">CHACONNE</a>
<a href="https://github.com/paganini2008/chaconne.git"><img style="position: absolute; top: 0; left: 0; border: 0; z-index: 9999;" src="https://camo.githubusercontent.com/82b228a3648bf44fc1163ef44c62fcc60081495e/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f6c6566745f7265645f6161303030302e706e67" alt="Fork me on GitHub" data-canonical-src="https://s3.amazonaws.com/github/ribbons/forkme_left_red_aa0000.png"></a>
<select id="currentClusterName" name="clusterName">
	<option value="sample1">sample1</option>
	<option value="sample2">sample2</option>
</select>
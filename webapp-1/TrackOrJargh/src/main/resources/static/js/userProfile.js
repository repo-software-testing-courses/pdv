function deleteContent(){ 
	var typeContent = $(this).val();
	var nameContent = $(this).parents(".d-flex").find("[name='deleteContentName']").text();
	var nameList = $(this).parents(".form-control").find("[name='deleteListName']").text();
	typeContent = typeContent.substr(1,typeContent.length - 2);
	
	$.ajax({
	    url: "/api/listas/"+nameList+"/"+typeContent+"/"+nameContent,
	    type: 'DELETE',
	    success: function(result) {
	    	if (result == true){
	    		location.reload(true);
	    	
	    	}
	    }
	});
}

function deleteList(){ 
	var nameList = $(this).val();
	$.ajax({
	    url: "/api/listas/"+nameList,
	    type: 'DELETE',
	    success: function(result) {
	    	if (result == true){
	    		location.reload(true);
	    	}
	    }
	});
}

$(function() {
	$("[name='deleteList']").click(deleteList);
	$("[name='deleteContentURL']").click(deleteContent);
});
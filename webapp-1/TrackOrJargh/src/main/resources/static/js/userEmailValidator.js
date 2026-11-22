function validateUserName(){
	if($("[name='name']").val() != ""){
		$.get("/api/comprobarusuario/" + $("[name='name']").val() + "/", function(data) {
			  if(data == true) {
				  $("[name='name']")[0].setCustomValidity("Ese usuario esta en uso.");
			  } else {
				  $("[name='name']")[0].setCustomValidity('');
			  }
		});
	}
}

function validateUserEmail(){	
	if($("[name='email']").val() != ""){
		$.get("/api/comprobaremail/" + $("[name='email']").val() + "/", function(data) {
			  if(data == true) {
				  $("[name='email']")[0].setCustomValidity("Ese email esta en uso.");
			  } else {
				  $("[name='email']")[0].setCustomValidity('');
			  }
		});
	}
}

$(function(){
	$("[name='name']").focusout(validateUserName);
	$("[name='email']").focusout(validateUserEmail);
});
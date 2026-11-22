var targetUser = document.getElementById("targetUser")
  , confirm_user = document.getElementById("deleteUser");

function validateUser(){
  if(targetUser.value != confirm_user.value) {
	    confirm_user.setCustomValidity("Los usuarios no coinciden.");
  } else {
    confirm_user.setCustomValidity('');
  }
}

targetUser.onchange = validateUser;
confirm_user.onkeyup = validateUser;
var targetFilm = document.getElementById("film"), confirm_film = document
		.getElementById("deleteFilm"), targetBook = document
		.getElementById("book"), confirm_book = document
		.getElementById("deleteBook"), targetShow = document
		.getElementById("show"), confirm_show = document
		.getElementById("deleteShow");

function validateUser() {
	if (targetUser.value != confirm_user.value) {
		confirm_user.setCustomValidity("Los nombres no coinciden.");
	} else {
		confirm_user.setCustomValidity('');
	}
}

function validateFilm() {
	if (targetFilm.value != confirm_film.value && confirm_film.value != "") {
		confirm_film.setCustomValidity("Los nombres no coinciden.");
	} else {
		confirm_film.setCustomValidity('');
	}
}

function validateShow() {
	if (targetShow.value != confirm_Show.value && confirm_show.value != "") {
		confirm_show.setCustomValidity("Los nombres no coinciden.");
	} else {
		confirm_show.setCustomValidity('');
	}
}

function validateBook() {
	if (targetBook.value != confirm_book.value && confirm_book.value != "") {
		confirm_book.setCustomValidity("Los nombres no coinciden.");
	} else {
		confirm_book.setCustomValidity('');
	}
}

targetUser.onchange = validateUser;
confirm_user.onkeyup = validateUser;

targetFilm.onchange = validateFilm;
confirm_film.onkeyup = validateFilm;

targetShow.onchange = validateShow;
confirm_show.onkeyup = validateShow;

targetBook.onchange = validateBook;
confirm_book.onkeyup = validateBook;




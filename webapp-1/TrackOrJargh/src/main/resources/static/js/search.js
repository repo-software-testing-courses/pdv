var request = 1;
var numElements = 10;
var totalPages = null;
var listsUser = null;

function loadContent() {
	if(request < totalPages){
		$("#moreContent").attr("class","fa fa-spinner fa-pulse fa-3x fa-fw");
		$("#moreContent").unbind("click");
		
		$.get(typeSearch + "?page=" + request + "&size=" + numElements, function(data) {
			sleep(1000);
			
			jQuery.each(data.content, function(count, item) {
				if(loggedUserJS != "false"){
					var addedLists = '<div class="dropup float-left">' + 
										'<button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenu2" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">' +
											'<i class="ion-plus-round"></i>' +
										'</button>' + 
										'<div class="dropdown-menu" aria-labelledby="dropdownMenu2">';
					
										jQuery.each(listsUser, function(count, item){
											addedLists += '<button name="buttonList" class="dropdown-item" type="button">' + item.name + '</button>'; 
										});
										
					addedLists +=			'<a href="/miperfil" class="dropdown-item">Crear lista</a>' +
										'</div>' + 
									'</div>';
				} else {
					addedLists = "";
				}
				
				$("#elements").append(
						'<div class="col-lg-3 col-md-3 portfolio-item filter-app wow fadeInUp">' +
							'<div class="portfolio-wrap">' +
								'<figure style="background-image: url(' + item.image + ');">' +
									'<p class="textPortfolio">' + item.synopsis + '</p>' +
								'</figure>' +
								'<div class="portfolio-info">' +
									'<h4>' + 
										'<a name="nameFilm" href="' + item.url + item.name + '">' + item.name + '</a>' +
									'</h4>' + 
									'<p>' + item.year + '</p>' +
									addedLists + 
								'</div>' + 
							'</div>' + 
						'</div>');
			});
			
			$("#moreContent").attr("class","fa fa-plus-circle fa-3x fa-fw");
			$("#moreContent").click(loadContent);
			$("[name='buttonList']").click(addFilmToList);
			request++;
			
			if(request == totalPages){
				$("#moreContent").remove();
			}
		});
	}
}

function addFilmToList(){ 
	var nameList = $(this).text();
	var nameFilm = $(this).parents(".portfolio-info").find("[name='nameFilm']").text();
	
	$.ajax({
	    url: "/api/listas/"+nameList+"/"+typePageAddList+"/"+nameFilm,
	    type: 'PUT',
	    success: function(result) {
	    	if (result == true){
	    		messagePopUp("Genial! ha sido añadido correctamente a tu lista.", true);
	    	}else{
	    		messagePopUpError("Ups, parece que ya pertenece a esa lista, intentelo con otra por favor.", true);
	    	}
	    }
	});
}

function sleep(miliseconds) {
	  var start = new Date().getTime();
	  while (true) {
	    if ((new Date().getTime() - start) > miliseconds)
	      break;
	  }
}

function changeLinksInputSearch(){
	var inputSearch = $('[name="inputSearch"]').val();
	var optionSearch = $('[name="optionSearch"] option:selected').val();
	var linkFilm;
	var linkShow;
	var linkBook;
	
	if(inputSearch == ""){
		linkFilm = "/busqueda";
		linkShow = "/busqueda";
		linkBook = "/busqueda";
	} else{
		linkFilm = "/busqueda/" + optionSearch + "/pelicula/" + inputSearch;
		linkShow = "/busqueda/" + optionSearch + "/serie/" + inputSearch;
		linkBook = "/busqueda/" + optionSearch + "/libro/" + inputSearch;
	}
	
	$("#linkFilm").attr("href", linkFilm);
	$("#linkShow").attr("href", linkShow);
	$("#linkBook").attr("href", linkBook);	
}

$(function() {
	$("[name='inputSearch']").keyup(changeLinksInputSearch);
	$("[name='optionSearch']").change(changeLinksInputSearch);
	changeLinksInputSearch();
	
	$("#moreContent").click(loadContent);
	$.get(typeSearch + "?page=" + request + "&size=" + numElements, function(data) {		
		totalPages = data.totalPages;
	});	
	
	$.get("/api/listas", function(data) {	
		listsUser = data;
	});	
	
	$("[name='buttonList']").click(addFilmToList);
});
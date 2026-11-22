var request = 1;
var numElements = 10;
var totalPages = null;
var listsUser = null;

function loadContent() {
	if(request < totalPages){
		$("#moreContent").attr("class","fa fa-spinner fa-pulse fa-3x fa-fw");
		$("#moreContent").unbind("click");
		$("[name='buttonList']").unbind("click");
		
		$.get(typePage +"?page=" + request + "&size=" + numElements, function(data) {
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
										'<a name="nameFilm" href="' + item.url + item.name +'">' + item.name + '</a>' +
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
	var content;
	
	if(typePageAddList === "pelicula"){
		content = {name: nameList, films: [nameFilm]};
	} else if(typePageAddList === "serie"){
		content = {name: nameList, shows: [nameFilm]};
	} else if(typePageAddList === "libro"){
		content = {name: nameList, books: [nameFilm]};
	}	
	
	$.ajax({
	    url: "/api/listas",
	    type: 'PUT',
	    contentType: "application/json; charset=utf-8",
	    data: JSON.stringify(content),
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

$(function() {
	$("#moreContent").click(loadContent);
	$.get(typePage +"?page=" + request + "&size=" + numElements, function(data) {		
		totalPages = data.totalPages;
	});	
	
	$.get("/api/listas", function(data) {	
		listsUser = data;
	});	
	
	$("[name='buttonList']").click(addFilmToList);
});
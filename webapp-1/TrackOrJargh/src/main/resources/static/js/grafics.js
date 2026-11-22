$(function() {
	$.get("/api/peliculas/grafico", function(data) {
		var chart = c3.generate({
			bindto : '#bestFilmsPoints',
			data : {
				json : data,
				type : 'bar',
				keys : {
					x : 'name',
					value : [ 'points' ],
				}
			},
			axis : {
				x : {
					type : 'category'
				}
			}
		});
	});
	
	$.get("/api/series/grafico", function(data) {
		var chart = c3.generate({
			bindto : '#bestShowsPoints',
			data : {
				json : data,
				type : 'bar',
				keys : {
					x : 'name',
					value : [ 'points' ],
				}
			},
			axis : {
				x : {
					type : 'category'
				}
			}
		});
	});
	
	$.get("/api/libros/grafico", function(data) {
		var chart = c3.generate({
			bindto : '#bestBooksPoints',
			data : {
				json : data,
				type : 'bar',
				keys : {
					x : 'name',
					value : [ 'points' ],
				}
			},
			axis : {
				x : {
					type : 'category'
				}
			}
		});
	});
	
	$.get("/api/generos/grafico", function(data) {
		var chart = c3.generate({
			bindto : '#gende',
			data : {
				json : data,
				type : 'bar',
				keys : {
					x : 'name',
					value : [ 'points' ],
				}
			},
			axis : {
				x : {
					type : 'category'
				}
			}
		});
	});
});

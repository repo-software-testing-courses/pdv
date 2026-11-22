import { Component } from '@angular/core';

import { DecoratorService } from '../header/decorator.service';

import { CommonFunction } from '../common-code/commonfunction.component';
import { FilmService } from '../films/film.service';
import { ShowService } from '../shows/show.service';
import { BookService } from '../books/book.service';
import { GenderService } from '../genders/gender.service';

import * as c3 from 'c3';
import { environment } from '../../environments/environment';

@Component({
	selector: 'index',
	templateUrl: './index.html',
	styleUrls: ['../common-css/css/index.css']
})

export class IndexComponent {

	public carousel = [];
	public URL = environment.url.substring(0, environment.url.length - 1);

	constructor(private decorator: DecoratorService, private commonFunction: CommonFunction, private filmService: FilmService, private showService: ShowService, private bookService: BookService, private genderService: GenderService) {
		this.decorator.activeButton("index");
	};

	ngOnInit() {
		this.loadCorousel();
	}

	private loadCorousel() {
		this.filmService.getLastAdded(1).subscribe(
			films => this.loadElementsCarousel(films, "film")
		);

		this.showService.getLastAdded(1).subscribe(
			shows => this.loadElementsCarousel(shows, "show")
		);

		this.bookService.getLastAdded(1).subscribe(
			books => this.loadElementsCarousel(books, "book")
		);
	}

	private loadElementsCarousel(elements, type: string) {
		for (let elem of elements) {
			elem.type = type;
			this.carousel.push(elem);
		}
	}

	ngAfterViewInit() {
		this.filmService.getGrafic().subscribe(
			films => this.graficFilms(this.changeLayoutChar(films, "Puntos"))
		);

		this.showService.getGrafic().subscribe(
			shows => this.graficShows(this.changeLayoutChar(shows, "Puntos"))
		);

		this.bookService.getGrafic().subscribe(
			books => this.graficBooks(this.changeLayoutChar(books, "Puntos"))
		);

		this.genderService.getGrafic().subscribe(
			genders => this.graficGende(this.changeLayoutChar(genders, "Contenido por genero"))
		);
	}

	private changeLayoutChar(list, showName) {
		let newList = [];

		list.forEach(element => {
			let newElement = {};
			newElement["name"] = element.name;
			newElement[showName] = element.points

			newList.push(newElement);
		});

		return newList;
	}

	private graficFilms(films) {
		let chart = c3.generate({
			bindto: '#bestFilmsPoints',
			data: {
				json: films,
				type: 'bar',
				keys: {
					x: 'name',
					value: ['Puntos'],
				}
			},
			legend: {
				show: false
			},
			axis: {
				x: {
					type: 'category'
				}
			}
		});
	}

	private graficShows(shows) {
		let chart = c3.generate({
			bindto: '#bestShowsPoints',
			data: {
				json: shows,
				type: 'bar',
				keys: {
					x: 'name',
					value: ['Puntos'],
				}
			},
			legend: {
				show: false
			},
			axis: {
				x: {
					type: 'category'
				}
			}
		});
  };
	
	private graficBooks(books){
		console.log("Loaded graphic of books: \n" + JSON.stringify(books));

		var chart = c3.generate({
			bindto: '#bestBooksPoints',
			data: {
				json: books,
				type: 'bar',
				keys: {
					x: 'name',
					value: ['Puntos'],
				}
			},
			legend: {
				show: false
			},
			axis: {
				x: {
					type: 'category'
				}
			}
		});
  }
	
	private graficGende(genders){
		console.log("Loaded graphic of genders: \n" + JSON.stringify(genders));

		var chart = c3.generate({
			bindto: '#gende',
			data: {
				json: genders,
				type: 'bar',
				keys: {
					x: 'name',
					value: ['Contenido por genero'],
				}
			},
			legend: {
				show: false
			},
			axis: {
				x: {
					type: 'category'
				}
			}
		});
	}
}
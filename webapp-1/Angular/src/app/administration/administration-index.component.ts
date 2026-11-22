import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { DecoratorService } from '../header/decorator.service';

import { UserComponent } from '../user/user.component';

import { User } from '../Interfaces/User/user.component';
import { Film } from '../Interfaces/Film/film.component';
import { Show } from '../Interfaces/Show/show.component';
import { Book } from '../Interfaces/Book/book.component';

import { FilmService } from '../films/film.service';
import { ShowService } from '../shows/show.service';
import { BookService } from '../books/book.service';
import { UserService } from '../user/user.service';

@Component({
    selector: 'administracion-index',
    templateUrl: './administration-index.html',
    styleUrls: ['./administration.css']
})

export class AdministrationIndexComponent {

    private selectUser: String;
    private selectFilm: String;
    private selectShow: String;
    private selectBook: String;

    private users: User[];
    private films: Film[];
    private books: Book[];
    private shows: Show[];

    constructor(private decorator: DecoratorService, private userComponent: UserComponent, private router:Router, private filmService: FilmService, private showService: ShowService, private bookService: BookService, private userService: UserService) {
        this.decorator.activeButton("admin");
    }

    ngOnInit() {
        this.filmService.getFilmsAdmin().subscribe(
            films => {
                this.films = films;
                this.selectFilm = this.films[0].name;
            }
        );

        this.showService.getShowsAdmin().subscribe(
            shows => {
                this.shows = shows;
                this.selectShow = this.shows[0].name;
            }
        );

        this.bookService.getBooksAdmin().subscribe(
            books => {
                this.books = books;
                this.selectBook = this.books[0].name;
            }
        );

        this.userService.getUsersAdmin().subscribe(
            users => {
                this.users = users;

                for(let x=0; x < this.users.length; x++){
                    if(this.users[x].name === this.userComponent.user.name){
                        this.users.splice(x, 1);
                        break;
                    }
                }

                this.selectUser = this.users[0].name;
            }
        );
    }

    private modifyElem(type: string) {
        switch (type) {
            case "film":
                this.router.navigate(['/administracion/peliculas', this.selectFilm]);
                break;
            case "show":
                this.router.navigate(['/administracion/series', this.selectShow]);
                break;
            case "book":
                this.router.navigate(['/administracion/libros',this.selectBook ]);
                break;
            case "user":
                this.router.navigate(['/administracion/usuarios', this.selectUser]);
                break;
        }
    }
}
import { Component } from '@angular/core';

import { FilmService } from '../films/film.service';
import { ShowService } from '../shows/show.service';
import { BookService } from '../books/book.service';
import { GenderService } from '../genders/gender.service';
import { FormControl, FormBuilder } from '@angular/forms';
import { DecoratorService } from '../header/decorator.service';
import { UserComponent } from '../user/user.component';
import { DomSanitizer } from '@angular/platform-browser';
import { Router, ActivatedRoute } from '@angular/router';
import { UserService } from '../user/user.service';
import { Film } from '../Interfaces/Film/film.component';
import { Show } from '../Interfaces/Show/show.component';
import { Book } from '../Interfaces/Book/book.component';
import { ImageService } from '../uploadimage/image.service';

@Component({
    selector: 'newContent',
    templateUrl: './newContent.html',
    styleUrls: ['./administration.css']
})

export class NewContentComponent {
    private film: Film;
    private show: Show;
    private book: Book;
    private file = null;

    private genres = {
        aventuras: false,
        superheroes: false,
        comedia: false,
        accion: false,
        cienciaFiccion: false,
        terror: false,
        romantica: false,
        thriller: false,
        animacion: false,
        drama: false,
        historica: false,
        musical: false,
        fantasia: false,
    };
    private contentGenres = [];

    constructor(private decorator: DecoratorService, private userComponent: UserComponent, private sanitizer: DomSanitizer, private router: Router, private activatedRoute: ActivatedRoute, private bookService: BookService, private filmService: FilmService, private userService: UserService, private showService: ShowService, private fb: FormBuilder, private imageService: ImageService) {
        this.decorator.activeButton("admin");
    }

    ngOnInit() {
        this.film = {
            directors: null,
            actors: null,
            name: null,
            synopsis: null,
            image: null,
            trailer: null,
            year: null,
        }
        this.show = {
            directors: null,
            actors: null,
            name: null,
            synopsis: null,
            image: null,
            trailer: null,
            year: null,
        }
        this.book = {
            authors: null,
            name: null,
            synopsis: null,
            image: null,
            year: null,

        }
    }

    private uploadContent(contentType: string) {
        this.imageService.uploadImage(this.file).subscribe(
            result => {
                switch (contentType) {
                    case "film":
                        this.film.genders = this.contentGenres;
                        this.film.image = result.text();
                        this.filmService.newFilm(this.film).subscribe(
                            result => {
                                this.router.navigate(['/pelicula', this.film.name]);
                            }
                        );
                        break;
                    case "show":
                        this.show.genders = this.contentGenres;
                        this.show.image = result.text();
                        this.showService.newShow(this.show).subscribe(
                            result => {
                                this.router.navigate(['/serie', this.show.name]);
                            }
                        );

                        break;
                    case "book":
                        this.book.genders = this.contentGenres;
                        this.book.image = result.text();
                        this.bookService.newBook(this.book).subscribe(
                            result => {
                                this.router.navigate(['/libro', result.name]);
                            }
                        );

                        break;
                }

            }
        );
    }

    private updateGenres(gname: string, checked: boolean) {
        if (!checked) {
            let rem = this.contentGenres.indexOf(gname);
            this.contentGenres.splice(rem, 1);
        } else {
            this.contentGenres.push({ name: gname });
        }
        console.log(this.contentGenres);
    }

    private fileChange(inputFile) {
        if (inputFile.files.length > 0) {
            this.file = inputFile.files[0];
        }
    }
}
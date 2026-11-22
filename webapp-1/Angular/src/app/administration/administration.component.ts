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
import { User } from '../Interfaces/User/user.component';

@Component({
    selector: 'administracion',
    templateUrl: './administration.html',
    styleUrls: ['./administration.css']
})

export class AdministrationComponent {
    private contentType: string;
    private contentName: string;
    public film: Film;
    public show: Show;
    public book: Book;
    public user: User;
    private del: boolean;
    private selectedRole: string;
    public selectedTab: string;
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
    private filmGenres = [];
    private file = null;

    constructor(private decorator: DecoratorService, private userComponent: UserComponent, private sanitizer: DomSanitizer, private router: Router, private activatedRoute: ActivatedRoute, private bookService: BookService, private filmService: FilmService, private userService: UserService, private showService: ShowService, private fb: FormBuilder, private imageService: ImageService) {
        this.decorator.activeButton("admin");
    }

    ngOnInit() {
        this.activatedRoute.params.subscribe(
            params => {
                this.contentType = params["type"];
                this.contentName = params["name"];
                switch (this.contentType) {
                    case "peliculas":
                        this.loadFilm();
                        this.selectedTab = "films";
                        break;
                    case "series":
                        this.loadShow();
                        this.selectedTab = "shows";
                        break;
                    case "libros":
                        this.loadBook();
                        this.selectedTab = "books";
                        break;
                    case "usuarios":
                        this.loadUser();
                        this.selectedTab = "users";
                        break;

                }
            }
        );
    }


    private loadFilm() {
        this.filmService.getFilm(this.contentName).subscribe(
            film => {
                this.film = film;
                this.filmGenres = film.genders;
                film.genders.forEach(g => {
                    switch (g.name) {
                        case "Aventuras":
                            this.genres.aventuras = true;
                            break;
                        case "Superheroes":
                            this.genres.superheroes = true;
                            break;
                        case "Comedia":
                            this.genres.comedia = true;
                            break;
                        case "Accion":
                            this.genres.accion = true;
                            break;
                        case "Ciencia-ficcion":
                            this.genres.cienciaFiccion = true;
                            break;
                        case "Terror":
                            this.genres.terror = true;
                            break;
                        case "Thriller":
                            this.genres.thriller = true;
                            break;
                        case "Animacion":
                            this.genres.animacion = true;
                            break;
                        case "Drama":
                            this.genres.drama = true;
                            break;
                        case "Historica":
                            this.genres.historica = true;
                            break;
                        case "Musical":
                            this.genres.musical = true;
                            break;
                        case "Fantasia":
                            this.genres.fantasia = true;
                            break;
                        case "Romantica":
                            this.genres.romantica = true;
                            break;

                    }
                });
            }
        )
    }
    private loadShow() {
        this.showService.getShow(this.contentName).subscribe(
            show => {
                this.show = show;
                this.filmGenres = show.genders;
                show.genders.forEach(g => {
                    switch (g.name) {
                        case "Aventuras":
                            this.genres.aventuras = true;
                            break;
                        case "Superheroes":
                            this.genres.superheroes = true;
                            break;
                        case "Comedia":
                            this.genres.comedia = true;
                            break;
                        case "Accion":
                            this.genres.accion = true;
                            break;
                        case "Ciencia-ficcion":
                            this.genres.cienciaFiccion = true;
                            break;
                        case "Terror":
                            this.genres.terror = true;
                            break;
                        case "Thriller":
                            this.genres.thriller = true;
                            break;
                        case "Animacion":
                            this.genres.animacion = true;
                            break;
                        case "Drama":
                            this.genres.drama = true;
                            break;
                        case "Historica":
                            this.genres.historica = true;
                            break;
                        case "Musical":
                            this.genres.musical = true;
                            break;
                        case "Fantasia":
                            this.genres.fantasia = true;
                            break;
                        case "Romantica":
                            this.genres.romantica = true;
                            break;

                    }
                });
            }
        )
    }
    private loadBook() {
        this.bookService.getBook(this.contentName).subscribe(
            book => {
                this.book = book;
                this.filmGenres = book.genders;
                book.genders.forEach(g => {
                    switch (g.name) {
                        case "Aventuras":
                            this.genres.aventuras = true;
                            break;
                        case "Superheroes":
                            this.genres.superheroes = true;
                            break;
                        case "Comedia":
                            this.genres.comedia = true;
                            break;
                        case "Accion":
                            this.genres.accion = true;
                            break;
                        case "Ciencia-ficcion":
                            this.genres.cienciaFiccion = true;
                            break;
                        case "Terror":
                            this.genres.terror = true;
                            break;
                        case "Thriller":
                            this.genres.thriller = true;
                            break;
                        case "Animacion":
                            this.genres.animacion = true;
                            break;
                        case "Drama":
                            this.genres.drama = true;
                            break;
                        case "Historica":
                            this.genres.historica = true;
                            break;
                        case "Musical":
                            this.genres.musical = true;
                            break;
                        case "Fantasia":
                            this.genres.fantasia = true;
                            break;
                        case "Romantica":
                            this.genres.romantica = true;
                            break;

                    }
                });
            }
        )
    }

    private loadUser() {
        this.userService.getUser(this.contentName).subscribe(
            response => {
                this.user = response;
                console.log(this.user);
                this.selectedRole = this.user.roles[this.user.roles.length - 1];

            }
        );
    }

    private editFilm() {
        if (this.del) {
            console.log("Eliminar");
            this.filmService.deleteFilm(this.film.name).subscribe();
            this.router.navigateByUrl("/");
        } else {
            if (this.file != null) {
                this.imageService.uploadImage(this.file).subscribe(
                    result => {
                        this.film.image = result.text();
                        this.filmService.updateFilm(this.film, this.contentName).subscribe(
                            result => {
                                this.router.navigate(['/pelicula', result.name]);
                            }
                        );
                    });
            } else {
                this.filmService.updateFilm(this.film, this.contentName).subscribe(
                    result => {
                        this.router.navigate(['/pelicula', result.name]);
                    }
                );
            }
        }
    }

    private editShow() {
        if (this.del) {
            console.log("Eliminar");
            this.showService.deleteShow(this.show.name).subscribe();
            this.router.navigateByUrl("/");
        } else {
            if (this.file != null) {
                this.imageService.uploadImage(this.file).subscribe(
                    result => {
                        this.show.image = result.text();
                        this.showService.updateShow(this.show, this.contentName).subscribe(
                            result => {
                                this.router.navigate(['/serie', result.name]);
                            }
                        );
                    });
            } else {
                this.showService.updateShow(this.show, this.contentName).subscribe(
                    result => {
                        this.router.navigate(['/serie', result.name]);
                    }
                );
            }
        }
    }

    private editBook() {
        if (this.del) {
            console.log("Eliminar");
            this.bookService.deleteBook(this.book.name).subscribe();
            this.router.navigateByUrl("/");
        } else {
            if (this.file != null) {
                this.imageService.uploadImage(this.file).subscribe(
                    result => {
                        this.book.image = result.text();
                        this.bookService.updateBook(this.book, this.contentName).subscribe(
                            result => {
                                this.router.navigate(['/libro', result.name]);
                            }
                        );
                    });
            } else {
                this.bookService.updateBook(this.book, this.contentName).subscribe(
                    result => {
                        this.router.navigate(['/libro', result.name]);
                    }
                );
            }
        }
    }

    private editUser() {
        if (this.del) {
            console.log("Eliminar usuario");
            this.userService.deleteUser(this.user.name).subscribe(
                response => {
                    this.router.navigateByUrl("/administracion");
                }
            );
        } else {
            this.userService.editUser(this.user.name, this.user).subscribe(
                response => {
                    this.router.navigateByUrl("/administracion");
                }
            );
        }
    }

    private updateRoles() {
        console.log(this.selectedRole);
        switch (this.selectedRole) {
            case "ROLE_USER":
                this.user.roles = ["ROLE_USER"];
                break;
            case "ROLE_MODERATOR":
                this.user.roles = ["ROLE_USER", "ROLE_MODERATOR"];
                break;
            case "ROLE_ADMIN":
                this.user.roles = ["ROLE_USER", "ROLE_MODERATOR", "ROLE_ADMIN"];
                break;
        }
    }

    private updateGenres(gname: string, checked: boolean) {
        if (!checked) {
            let rem = this.filmGenres.indexOf(gname);
            this.filmGenres.splice(rem, 1);
        } else {
            this.filmGenres.push({ name: gname });
        }
        console.log(this.filmGenres);
    }

    private fileChange(inputFile) {
        if (inputFile.files.length > 0) {
            this.file = inputFile.files[0];
        }
    }
}
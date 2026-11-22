import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Http, RequestOptions, Headers } from '@angular/http';
import 'rxjs/Rx';

import { Film } from '../Interfaces/Film/film.component';
import { CommentFilm } from '../Interfaces/Film/commentfilm.component';
import { PointFilm } from '../Interfaces/Film/pointfilm.component';

const BASE_URL = environment.apiBase + "peliculas/";

const headers = new Headers({
    'X-Requested-With': 'XMLHttpRequest'
});
const options = new RequestOptions({ withCredentials: true, headers });

@Injectable()
export class FilmService {

    constructor(private http: Http) { }
    getFilmsAdmin() {
        return this.http.get(BASE_URL + "administracion", { withCredentials: true, headers }).map(
            response => response.json()
        );
    };

    getFilms(page: number, size: number) {
        return this.http.get(BASE_URL + "?page=" + page + "&size=" + size).map(
            response => response.json()
        );
    };

    getGrafic() {
        console.log("Request GET graphic films");

        return this.http.get(BASE_URL + "grafico").map(
            response => response.json()
        );
    }

    getLastAdded(size: number) {
        return this.http.get(BASE_URL + "ultimas/" + size).map(
            response => response.json()
        );
    }

    getBestFilms(page: number, size: number) {
        return this.http.get(BASE_URL + "mejorvaloradas?page=" + page + "&size=" + size).map(
            response => response.json()
        );
    }

    getRelationFilm(id: number, page: number, size: number) {
        return this.http.get(BASE_URL + "relacionadas/" + id + "/?page=" + page + "&size=" + size).map(
            response => response.json()
        );
    }

    getPointsFilm(name: string) {
        return this.http.get(BASE_URL + "puntos/" + name).map(
            response => response.json()
        );
    };

    getFilm(name: string) {
        return this.http.get(BASE_URL + name).map(
            response => response.json()
        );
    };

    getCommentsFilm(name: string) {
        return this.http.get(BASE_URL + "comentarios/" + name).map(
            response => response.json()
        );
    }

    newFilm(film: Film) {
        return this.http.post(BASE_URL, film, { withCredentials: true, headers }).map(
            response => response.json()
        );
    }

    newCommentFilm(name: string, comment: CommentFilm) {
        return this.http.post(BASE_URL + "comentarios/" + name, comment, options).map(
            response => response.json()
        );
    }

    updateFilm(film: Film, name:string) {
        return this.http.put(BASE_URL + name, film, { withCredentials: true, headers }).map(
            response => response.json()
        );
    }

    updatePoint(name: string, point: PointFilm) {
        return this.http.post(BASE_URL + "puntos/" + name, point, options).map(
            response => response.json()
        );
    }

    deleteFilm(name: String) {
        return this.http.delete(BASE_URL + name, { withCredentials: true, headers }).map(
            response => response.json()
        );
    }

    deleteCommentFilm(id: number) {
        return this.http.delete(BASE_URL + "comentarios/" + id, options).map(
            response => response.json()
        );
    }
}
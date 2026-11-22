import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Http } from '@angular/http';
import 'rxjs/Rx';

const BASE_URL = environment.apiBase + "busqueda/";

@Injectable()
export class SearchService {

    constructor(private http:Http) { }

    getFilms(page: number, size: number, optionSearch: string, name: string){
        return this.http.get(BASE_URL + optionSearch + "/peliculas/" + name + "/page" + "?page=" + page + "&size=" + size).map(
            response => response.json()
        );
    };

    getShows(page: number, size: number, optionSearch: string, name: string){
        return this.http.get(BASE_URL + optionSearch + "/series/" + name + "/page" + "?page=" + page + "&size=" + size).map(
            response => response.json()
        );
    };

    getBooks(page: number, size: number, optionSearch: string, name: string){
        return this.http.get(BASE_URL + optionSearch + "/libros/" + name + "/page" + "?page=" + page + "&size=" + size).map(
            response => response.json()
        );
    };
}
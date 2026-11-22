import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Http, RequestOptions, Headers } from '@angular/http';
import 'rxjs/Rx';

import { Book } from '../Interfaces/Book/book.component';
import { CommentBook } from '../Interfaces/Book/commentbook.component';
import { PointBook } from '../Interfaces/Book/pointbook.component';

const BASE_URL = environment.apiBase + "libros/";

const headers = new Headers({
    'X-Requested-With': 'XMLHttpRequest'
});
const options = new RequestOptions({ withCredentials: true, headers });

@Injectable()
export class BookService {
    constructor(private http:Http) { }

    getBooksAdmin(){
        return this.http.get(BASE_URL + "administracion", { withCredentials: true, headers }).map(
            response => response.json()
        );
    };

    getBooks(page: number, size: number){
        return this.http.get(BASE_URL + "?page=" + page + "&size=" + size).map(
            response => response.json()
        );
    };

    getGrafic(){
        console.log("Request GET graphic books");

        return this.http.get(BASE_URL + "grafico").map(
            response => response.json()
        );
    }

    getLastAdded(size:number){
        return this.http.get(BASE_URL + "ultimas/" + size).map(
            response => response.json()
        );
    }

    getBestBooks(page: number, size: number){
        return this.http.get(BASE_URL + "mejorvalorados?page=" + page + "&size=" + size).map(
            response => response.json()
        );
    }

    getRelationBook(id: number, page: number, size: number){
        return this.http.get(BASE_URL + "relacionadas/" + id + "/?page=" + page + "&size=" + size).map(
            response => response.json()
        );
    }

    getPointsBook(name:string){
        return this.http.get(BASE_URL + "puntos/" + name).map(
            response => response.json()
        );
    };

    getBook(name:string){
        return this.http.get(BASE_URL + name).map(
            response => response.json()
        );
    };

    getCommentsBook(name:string){
        return this.http.get(BASE_URL + "comentarios/" + name).map(
            response => response.json()
        );
    }

    newBook(book:Book){
        return this.http.post(BASE_URL, book, { withCredentials: true, headers }).map(
            response => response.json()
        );
    }

    newCommentBook(name:string, comment:CommentBook){
        return this.http.post(BASE_URL + "comentarios/" + name, comment, options).map(
            response => response.json()
        );
    }

    updateBook(book:Book, name:string){
        return this.http.put(BASE_URL + name, book, { withCredentials: true, headers }).map(
            response => response.json()
        );
    }

    updatePoint(name:string, point:PointBook){
        return this.http.post(BASE_URL + "puntos/" + name, point, options).map(
            response => response.json()
        );
    }

    deleteBook(name:String){
        return this.http.delete(BASE_URL + name, { withCredentials: true, headers }).map(
            response => response.json()
        );
    }

    deleteCommentBook(id:number){
        return this.http.delete(BASE_URL + "comentarios/" + id, options).map(
            response => response.json()
        );
    }
}


import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Http, RequestOptions, Headers } from '@angular/http';
import 'rxjs/Rx';

import { Show } from '../Interfaces/Show/show.component';
import { CommentShow } from '../Interfaces/Show/commentshow.component';
import { PointShow } from '../Interfaces/Show/pointshow.component';

const BASE_URL = environment.apiBase + "series/";

const headers = new Headers({
    'X-Requested-With': 'XMLHttpRequest'
});
const options = new RequestOptions({ withCredentials: true, headers });

@Injectable()
export class ShowService {

    constructor(private http:Http) { }

    getShowsAdmin(){
        return this.http.get(BASE_URL + "administracion", { withCredentials: true, headers }).map(
            response => response.json()
        );
    };

    getShows(page: number, size: number){
        return this.http.get(BASE_URL + "?page=" + page + "&size=" + size).map(
            response => response.json()
        );
    };

    getGrafic(){
        console.log("Request GET graphic shows");
        
        return this.http.get(BASE_URL + "grafico").map(
            response => response.json()
        );
    }

    getLastAdded(size:number){
        return this.http.get(BASE_URL + "ultimas/" + size).map(
            response => response.json()
        );
    }

    getBestShows(page: number, size: number){
        return this.http.get(BASE_URL + "mejorvaloradas?page=" + page + "&size=" + size).map(
            response => response.json()
        );
    }

    getRelationShow(id: number, page: number, size: number){
        return this.http.get(BASE_URL + "relacionadas/" + id + "/?page=" + page + "&size=" + size).map(
            response => response.json()
        );
    }

    getPointsShow(name:string){
        return this.http.get(BASE_URL + "puntos/" + name).map(
            response => response.json()
        );
    };

    getCommentsShow(name:string){
        return this.http.get(BASE_URL + "comentarios/" + name).map(
            response => response.json()
        );
    }

    getShow(name:string){
        return this.http.get(BASE_URL + name).map(
            response => response.json()
        );
    };

    newShow(show:Show){
        return this.http.post(BASE_URL, show, { withCredentials: true, headers }).map(
            response => response.json()
        );
    }

    newCommentShow(name:string, comment:CommentShow){
        return this.http.post(BASE_URL + "comentarios/" + name, comment, options).map(
            response => response.json()
        );
    }

    updateShow(show:Show, name:string){
        return this.http.put(BASE_URL + name, show, { withCredentials: true, headers }).map(
            response => response.json()
        );
    }

    updatePoint(name:string, point:PointShow){
        return this.http.post(BASE_URL + "puntos/" + name, point, options).map(
            response => response.json()
        );
    }

    deleteShow(name:String){
        return this.http.delete(BASE_URL + name, { withCredentials: true, headers }).map(
            response => response.json()
        );
    }

    deleteCommentShow(id:number){
        return this.http.delete(BASE_URL + "comentarios/" + id, options).map(
            response => response.json()
        );
    }
}
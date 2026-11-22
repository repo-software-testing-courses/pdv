import { Injectable, OnInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { Http, RequestOptions, Headers } from '@angular/http';
import 'rxjs/Rx';
import { thresholdSturges } from 'd3';

const BASE_URL = environment.apiBase + "listas/";

@Injectable()
export class ListsService {

    constructor(private http: Http) { }

    public getLists() {
        return this.http.get(BASE_URL, { withCredentials: true }).map(
            response => response.json(),
            error => error
        );
    }

    public createList(name: string) {
        console.log("Request POST list: " + name);

        return this.http.post(BASE_URL, { name: name }, { withCredentials: true }).map(
            response => response,
            error => error
        );
    }

    public deleteList(nameList: string) {
        console.log("Request DELETE list: " + nameList);

        return this.http.delete(BASE_URL + nameList, { withCredentials: true }).map(
            response => response.json(),
            error => error
        )
    }

    public addElement(list) {
        return this.http.put(BASE_URL, list, { withCredentials: true }).map(
            response => response.json(),
            error => error
        )

    }

    public deleteElement(nameList: string, typeContent: string, nameContent: string) {
        return this.http.delete(BASE_URL + nameList + typeContent + nameContent, { withCredentials: true }).map(
            response => response.json(),
            error => error
        )
    }

    public getContentList() {
        return this.http.get(BASE_URL + "contenido", { withCredentials: true }).map(
            response => response.json(),
            error => error
        )
    }
}
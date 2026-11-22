import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Http } from '@angular/http';
import 'rxjs/Rx';

const BASE_URL = environment.apiBase;

@Injectable()
export class ImageService {

    constructor(private http: Http) { }

    uploadImage(image) {
        const formModel = new FormData();
        formModel.append('image', image);

        return this.http.post(BASE_URL + "subirimagen", formModel, { withCredentials: true }).map(
            response => response
        );
    }
}
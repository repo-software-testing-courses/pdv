import { Injectable, OnInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { Http, RequestOptions, Headers } from '@angular/http';
import 'rxjs/Rx';

const BASE_URL = environment.apiBase;

@Injectable()
export class LoginService {

    constructor(private http: Http) { }

    public getLoggedUser() {
        const headers = new Headers({
            'X-Requested-With': 'XMLHttpRequest'
        });

        const options = new RequestOptions({ withCredentials: true, headers });

        return this.http.get(BASE_URL + 'login', options).map(
            response => response.json(),
            error => error
        );
    }

    public login(user: string, pass: string) {
        const userPass = user + ':' + pass;

        const headers = new Headers({
            'Authorization': 'Basic ' + utf8_to_b64(userPass),
            'X-Requested-With': 'XMLHttpRequest'
        });

        const options = new RequestOptions({ withCredentials: true, headers });

        return this.http.get(BASE_URL + 'login', options).map(
            response => response.json()
        );
    }

    public logout() {
        return this.http.get(BASE_URL + "logout", { withCredentials: true }).map(
            response => response.json()
        );
    }
}

function utf8_to_b64(str) {
    return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, function (match, p1) {
        return String.fromCharCode(<any>'0x' + p1);
    }));
}
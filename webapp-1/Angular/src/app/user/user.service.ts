import { Injectable, OnInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { Http, RequestOptions, Headers } from '@angular/http';
import 'rxjs/Rx';

import { User } from '../Interfaces/User/user.component';

const BASE_URL = environment.apiBase + "usuarios/";
const BASE_URL_API = environment.apiBase;

const headers = new Headers({
    'X-Requested-With': 'XMLHttpRequest'
});
const options = new RequestOptions({ withCredentials: true, headers });

@Injectable()
export class UserService {

    constructor(private http: Http) { }

    public getUsersAdmin() {
        return this.http.get(BASE_URL + "administracion", { withCredentials: true, headers }).map(
            response => response.json()
        );
    }

    public newUser(user: User) {
        return this.http.post(BASE_URL, user).map(
            response => response
        );
    }

    public editUser(name: string, user: User){
        return this.http.put(BASE_URL + name, user,  {withCredentials: true}).map(
            response => response.json()
        )
    }

    public activatedUser(name: string) {
        return this.http.get(BASE_URL_API + "activar/" + name).map(
            response => response
        );
    }

    public getForgotPass(code: string) {
        return this.http.get(BASE_URL_API + "restablecer/" + code + "/").map(
            response => response.json()
        );
    }

    public setForgotPass(email: string) {
        return this.http.put(BASE_URL_API + "restablecer", { "email" : email }).map(
            response => response
        );
    }

    public changeForgotPass(code: string, pass: string) {
        return this.http.post(BASE_URL_API + "restablecer/" + code + "/", { "password" : pass }).map(
            response => response
        );
    }

    public getUser(name: string){
        return this.http.get(BASE_URL + name, {withCredentials: true}).map(
            response => response.json()
        );
    }

    public deleteUser(name: string){
        return this.http.delete(BASE_URL + name, {withCredentials: true}).map(
            response => response
        );
    }
}
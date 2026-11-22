import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CanActivate } from '@angular/router';
import { Observable } from 'rxjs/Observable';

import { Http, RequestOptions, Headers } from '@angular/http';
import 'rxjs/Rx';
import { environment } from '../../environments/environment';

const headers = new Headers({
    'X-Requested-With': 'XMLHttpRequest'
});

const options = new RequestOptions({ withCredentials: true, headers });

@Injectable()
export class PreventAdminAccess implements CanActivate {

    private url_api = environment.apiBase;

    constructor(private http: Http, private router: Router) { }

    canActivate() {
        return this.http.get(this.url_api + "login", options).map(e => {
            if (e) {
                let isAdmin = e.json().roles.indexOf('ROLE_MODERATOR') !== -1;

                if (isAdmin) {
                    return true;
                } else {
                    this.router.navigate(['/']);
                    return false;
                }
            }
        }).catch(() => {
            this.router.navigate(['/']);
            return Observable.of(false);
        });
    }
} 
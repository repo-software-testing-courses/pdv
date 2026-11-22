import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { DecoratorService } from '../header/decorator.service';

import { LoginService } from '../login/login.service';
import { ListsService } from '../lists/lists.service';

import { User } from '../Interfaces/User/user.component';
import { Lists } from '../Interfaces/User/lists.component';

@Injectable()
export class UserComponent {
    public user: User;
    public lists: Lists;
    public isLogged = false;
    public isAdmin = false;
    public isModerator = false;

    public userWrongLogin = false;
    public userWronLoginMessage: string;

    constructor(private decorator: DecoratorService, private httpClient: HttpClient, private loginService: LoginService, private listsService: ListsService) {
        this.reqIsLogged();
    }

    private processLogInResponse(user) {
        console.log("Logged succesfuly " + user.name);
        
        this.isLogged = true;
        this.user = user;
        this.isModerator = this.user.roles.indexOf('ROLE_MODERATOR') !== -1;
        this.isAdmin = this.user.roles.indexOf('ROLE_ADMIN') !== -1;
        this.updateDecorator();
        this.reqLists();
    }

    public updateDecorator() {
        this.decorator.loggedUser = this.isLogged;
        this.decorator.userAdmin = this.isAdmin;
    }

    public reqIsLogged() {
        this.loginService.getLoggedUser().subscribe(
            user => this.processLogInResponse(user)
        );
    }

    public reqLists() {
        this.listsService.getLists().subscribe(
            lists => this.lists = lists
        );
    }

    public login(username: string, password: string, route: Router) {
        this.loginService.login(username, password).subscribe(
            user => {
                this.processLogInResponse(user);
                route.navigateByUrl("/");
            },
            error => {
                this.userWrongLogin = true;
                this.userWronLoginMessage = "Contraseña o usuario incorrecto";
            }
        );
    }

    public logout() {
        this.loginService.logout().subscribe(
            user => {
                this.user = undefined;
                this.lists = null;
                this.isLogged = false;
                this.isModerator = false;
                this.isAdmin = false;
                this.updateDecorator();
            }
        );
    }
}
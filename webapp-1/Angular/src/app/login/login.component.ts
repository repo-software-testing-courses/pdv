import { Component, HostListener } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { Location } from '@angular/common';

import { DecoratorService } from '../header/decorator.service';

import { UserComponent } from '../user/user.component';
import { UserService } from '../user/user.service';
import { PARAMETERS } from '@angular/core/src/util/decorators';

@Component({
  selector: 'login',
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})

export class LoginComponent {
  public isCollapsed = true;
  public isEmailOrNameUsed = false;
  public isNewUser = false;
  public isChangePass = false;
  public activateUser = false;
  public userName = "";
  public validationLoginForm: FormGroup;
  public validationRegisterForm: FormGroup;
  public marginFooter = {
    'margin-bottom': '20em'
  }

  constructor(private decorator: DecoratorService, public userComponent: UserComponent, private userService: UserService, private router: Router, private activatedRoute: ActivatedRoute, private fb: FormBuilder, private location: Location) {
    this.decorator.activeButton("login");

    this.validationLoginForm = fb.group({
      'user': [null, Validators.required],
      'pass': [null, Validators.required],
    });

    this.validationRegisterForm = fb.group({
      'name': [null, Validators.required],
      'email': [null, [Validators.required, Validators.pattern('^[a-z0-9]+(\.[_a-z0-9]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,15})$')]],
      'password': [null, Validators.required],
      'passwordConfirm': [null, Validators.required]
    }, {
        validator: this.matchPassword // your validation method
      });
  }

  ngOnInit(): void {
    setTimeout(() => this.closeShowMessageLogin(), 20000);
    setTimeout(() => this.closeIsEmailOrNameUsed(), 20000);
    setTimeout(() => this.closeIsNewUser(), 20000);

    this.activatedRoute.params.subscribe(
      params => {
        let name = params["name"];
        let change = params["change"];

        if (name !== undefined) {
          this.userService.activatedUser(name).subscribe(
            user => {
              this.location.replaceState("/login");

              this.activateUser = true;
              this.userName = name;
              setTimeout(() => this.closeActivateUser(), 20000);

              this.validationLoginForm = this.fb.group({
                'user': [this.userName, Validators.required],
                'pass': [null, Validators.required],
              });
            }
          );
        }

        if (change !== undefined) {
          setTimeout(() => this.closeIsChangePass(), 20000);
          this.location.replaceState("/login");
          this.isChangePass = true;
          this.userName = change;
        }
      }
    );
  }

  ngAfterViewInit() {
    if (this.userName !== "") {
      this.validationLoginForm = this.fb.group({
        'user': [this.userName, Validators.required],
        'pass': [null, Validators.required],
      });
    }
  }

  public changeMarginFooter(){
    if (this.isCollapsed) {
      this.isCollapsed = false;
      this.marginFooter["margin-bottom"] = "40em";
    } else {
      this.isCollapsed = true;
      this.marginFooter["margin-bottom"] = "20em";
    }
  }

  public loginUser(user: string, pass: string) {
    this.userComponent.login(user, pass, this.router);
  }

  public registerUser(name: string, pass: string, email: string) {

    let user = {
      "name": name,
      "password": pass,
      "email": email,
      "activatedUser": false
    };

    this.userService.newUser(user).subscribe(
      response => {
        if (response.status === 226) {
          this.isEmailOrNameUsed = true;
          this.isNewUser = false;
        } else {
          this.isEmailOrNameUsed = false;
          this.isNewUser = true;
        }
      }
    )
  }

  private closeShowMessageLogin() {
    this.userComponent.userWrongLogin = false;
    setTimeout(() => this.closeShowMessageLogin(), 20000);
  }

  private closeIsEmailOrNameUsed() {
    this.isEmailOrNameUsed = false;
    setTimeout(() => this.closeIsEmailOrNameUsed(), 20000);
  }

  private closeIsNewUser() {
    this.isNewUser = false;
    setTimeout(() => this.closeIsNewUser(), 20000);
  }

  private closeActivateUser() {
    this.activateUser = false;
    setTimeout(() => this.closeActivateUser(), 20000);
  }

  private closeIsChangePass() {
    this.isChangePass = false;
  }

  private matchPassword(AC: AbstractControl) {
    let password = AC.get('password').value; // to get value in input tag
    let confirmPassword = AC.get('passwordConfirm').value; // to get value in input tag
    if (password != confirmPassword) {
      AC.get('passwordConfirm').setErrors({ MatchPassword: true })
    } else {
      return null
    }
  }
}

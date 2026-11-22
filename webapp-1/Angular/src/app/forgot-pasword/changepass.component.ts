import { Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { Location } from '@angular/common';

import { DecoratorService } from '../header/decorator.service';

import { UserService } from '../user/user.service';

@Component({
  selector: 'changepass',
  templateUrl: './changepass.html',
  styleUrls: ['./forgot.css']
})

export class ChangePassComponent {

  public validatorPass: FormGroup;

  private code: string;
  public username: string;

  constructor(private decorator: DecoratorService, private fb: FormBuilder, private userService: UserService, private router: Router, private activatedRoute: ActivatedRoute, private location: Location) {
    this.decorator.activeButton("forgotpass");

    this.validatorPass = fb.group({
      'password': [null, Validators.required],
      'passwordConfirm': [null, Validators.required]
    }, {
        validator: this.matchPassword // your validation method
      });
  };

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(
      params => {
        this.code = params["code"];

        this.userService.getForgotPass(this.code).subscribe(
          result => {
            this.location.replaceState("/cambiarcontra");
            this.username = result.user.name;
          }, error => {
            this.router.navigateByUrl("/");
          }
        )
      }
    );
  }

  public changePass(pass: string) {
    this.userService.changeForgotPass(this.code, pass).subscribe(
      response => {
        if (response.status === 200) {
          this.router.navigate(['/contracambiada', response.json().user.name]);
        }
      }
    );
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

import { Component } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';

import { DecoratorService } from '../header/decorator.service';

import { UserService } from '../user/user.service';

@Component({
  selector: 'forgotpass',
  templateUrl: './forgot.html',
  styleUrls: ['./forgot.css']
})

export class ForgotComponent {

  public validatorEmail: FormGroup;
  public isViewSuccesMessage = false;
  public isViewErrorMessage = false;
  public errorMessage = "";

  constructor(private decorator: DecoratorService, private fb: FormBuilder, private userService: UserService) { 
    this.decorator.activeButton("forgotpass");
    
    this.validatorEmail = fb.group({
      'email': [null, [Validators.required, Validators.pattern('^[a-z0-9]+(\.[_a-z0-9]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,15})$')]],
    });
  };
  
  public checkEmail(email: string){
    this.isViewErrorMessage = false;

    this.userService.setForgotPass(email).subscribe(
      response => {
        if (response.status === 200){
          if(response.json() === true){
            this.isViewSuccesMessage = true;
            setTimeout(() => this.closeIsViewSuccesMessage(), 20000);
          } else {
            this.isViewErrorMessage = true;
            this.errorMessage = "Este usuario ya ha pedido el cambio de contraseña, porfavor revise su bandeja";
            setTimeout(() => this.closeIsViewErrorMessage(), 20000);
          }
        }
      }, error => {
        this.isViewErrorMessage = true;
        this.errorMessage = "El email introducido no pertenece a ningun usuario";
        setTimeout(() => this.closeIsViewErrorMessage(), 20000);
      }
    )
  }

  private closeIsViewErrorMessage() {
    this.isViewErrorMessage = false;
  }

  private closeIsViewSuccesMessage() {
    this.isViewSuccesMessage = false;
  }
}

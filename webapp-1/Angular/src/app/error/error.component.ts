import { Component } from '@angular/core';

import { DecoratorService } from '../header/decorator.service';

@Component({
  selector: 'error',
  templateUrl: './error.html',
  styleUrls: ['./error.css']
})

export class ErrorComponent {
  constructor(private decorator: DecoratorService){
    this.decorator.activeButton("forgotpass");
  }
}

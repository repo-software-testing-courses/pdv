import { Component, HostListener } from '@angular/core';
import { Router } from '@angular/router';

import { DecoratorService } from './decorator.service';

import { UserComponent } from '../user/user.component';

@Component({
  selector: 'header',
  templateUrl: './header.html',
  styleUrls: ['./header.css']
})

export class HeaderComponent {
  
  public isNavbarCollapsed;
  
  constructor(public decorator: DecoratorService,  private userComponent: UserComponent, private router: Router) { }

  @HostListener("window:scroll", [])
  onWindowScroll() {
    if (window.pageYOffset > 60) {
      this.decorator.opaque = true;
    } else {
      this.decorator.opaque = false;
    }
  }

  public logout(){
    if(this.router.url === "/perfil" || this.router.url.includes("administracion") || this.router.url.includes("subirContenido")){
      this.router.navigateByUrl("/");
    }
   
    this.userComponent.logout();
  }
}

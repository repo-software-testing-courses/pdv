import { Injectable } from '@angular/core';

@Injectable()
export class DecoratorService {
  public pButton = [];
  public elementActive = 0;

  public loggedUser = false;
  public userAdmin = false;

  public opaque = false;
  public loadSpinner = false;

  constructor() {
    for (var x = 0; x <= 9; x++) {
      this.pButton[x] = {
        "menu-active": false
      }
    }
  }

  public clearActive() {
    for (let button of this.pButton) {
      button["menu-active"] = false;
    }
  }

  public activeButton(type: string) {
    this.opaque = false;
    this.clearActive();

    switch (type) {
      case "index":
        this.pButton[0]["menu-active"] = true;
        break;
      case "search":
        this.pButton[1]["menu-active"] = true;
        break;
      case "films":
        this.pButton[2]["menu-active"] = true;
        break;
      case "shows":
        this.pButton[3]["menu-active"] = true;
        break;
      case "books":
        this.pButton[4]["menu-active"] = true;
        break;
      case "admin":
        this.pButton[5]["menu-active"] = true;
        break;
      case "profile":
        this.pButton[6]["menu-active"] = true;
        break;
      case "login":
        this.pButton[7]["menu-active"] = true;
        break;
      case "forgotpass":
        this.pButton[8]["menu-active"] = true;
        break;
      case "error":
        this.pButton[9]["menu-active"] = true;
        break;
    }
  }

  public setStyleHead() {
    if (this.pButton[1]["menu-active"] || this.pButton[5]["menu-active"] || this.pButton[6]["menu-active"] || this.pButton[7]["menu-active"] || this.pButton[8]["menu-active"] || this.pButton[9]["menu-active"]) {
      this.opaque = true;
    }

    let styles = {
      'background-color': this.opaque ? 'rgba(0, 0, 0, 0.9)' : 'transparent',
      'padding': '20px 0',
      'height': '72px',
      'transition': 'all 0.5s'
    };

    return styles;
  }

  public setStyleMoreContent() {
    let styles

    if (!this.loadSpinner) {
      styles = {
        "fa": true,
        "fa-plus-circle": true,
        "fa-3x": true,
        "fa-fw": true
      };
    } else {
      styles = {
        "fa": true,
        "fa-spinner": true,
        "fa-pulse": true,
        "fa-3x": true,
        "fa-fw": true
      };
    }

    return styles;
  }
}

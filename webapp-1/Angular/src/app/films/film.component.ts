import { Component } from '@angular/core';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { DomSanitizer, SafeResourceUrl, SafeUrl } from '@angular/platform-browser';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { LocationStrategy } from '@angular/common';

import { DecoratorService } from '../header/decorator.service';

import { FilmService } from './film.service';
import { Film } from '../Interfaces/Film/film.component';
import { CommentFilm } from '../Interfaces/Film/commentfilm.component';
import { PointFilm } from '../Interfaces/Film/pointfilm.component';

import { UserComponent } from '../user/user.component';
import { environment } from '../../environments/environment';

@Component({
  selector: 'film-component',
  templateUrl: './film.html',
  styleUrls: ['./film.css']
})

export class FilmComponent {
  public URL = environment.url.substring(0, environment.url.length - 1);
  private isPopState = false;
  private nameFilm: string;
  public film: Film;
  public startFilm: number;
  private startFilmUser = 0;
  public url: SafeResourceUrl;
  public commentsFilm: CommentFilm[] = [];
  public relationFilms: Film[] = [];
  public validationMessageForm: FormGroup;

  constructor(private decorator: DecoratorService, public userComponent: UserComponent, private sanitizer: DomSanitizer, private router: Router, private activatedRoute: ActivatedRoute, private filmService: FilmService, private fb: FormBuilder, private locStrat: LocationStrategy) {
    this.decorator.clearActive();

    this.validationMessageForm = fb.group({
      'message': [null, Validators.required],
    });
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe(
      params => {
        this.nameFilm = params["name"];
        this.loadFilm();
        this.loadComments();
      }
    );

    this.locStrat.onPopState(() => {
      this.isPopState = true;
    });

    this.router.events.subscribe(event => {
      // Scroll to top if accessing a page, not via browser history stack
      if (event instanceof NavigationEnd && !this.isPopState) {
        window.scrollTo(0, 0);
        this.isPopState = false;
      }

      // Ensures that isPopState is reset
      if (event instanceof NavigationEnd) {
        this.isPopState = false;
      }
    });
  }

  private loadFilm() {
    this.filmService.getFilm(this.nameFilm).subscribe(
      film => {
        this.film = film;
        this.url = this.sanitizer.bypassSecurityTrustResourceUrl(this.film.trailer);
        this.loadRealted();
        this.loadPointFilm();
      }
    )
  }

  private loadPointFilm() {
    this.filmService.getPointsFilm(this.nameFilm).subscribe(
      pointFilm => {
        let sum = 0;

        for (let pFilm of pointFilm) {
          if (this.userComponent.user !== undefined && this.userComponent.user.name === pFilm.user.name) {
            this.startFilmUser = pFilm.points;
          }

          sum += pFilm.points;
        }

        sum /= pointFilm.length;
        this.startFilm = sum;
      }
    )
  }

  private loadComments() {
    this.filmService.getCommentsFilm(this.nameFilm).subscribe(
      comments => {
        this.commentsFilm = comments;
      }
    )
  }

  private loadRealted() {
    this.filmService.getRelationFilm(this.film.id, 0, 8).subscribe(
      related => this.relationFilms = related.content
    )
  }

  public changePointUser() {
    this.filmService.updatePoint(this.nameFilm, { points: this.startFilmUser }).subscribe(
      response => {
        this.loadPointFilm();
      }
    )
  }

  public deleteComment(id: number){
    this.filmService.deleteCommentFilm(id).subscribe(
      response => {
        this.loadComments();
      }
    );
  }

  public newComment(message: string) {
    this.validationMessageForm = this.fb.group({
      'message': [null, Validators.required],
    });

    this.filmService.newCommentFilm(this.nameFilm, { comment: message }).subscribe(
      response => {
        this.loadComments();
      }
    )
  }
}

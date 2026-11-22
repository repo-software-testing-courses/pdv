import { Component } from '@angular/core';

import { DecoratorService } from '../header/decorator.service';

import { CommonFunction } from '../common-code/commonfunction.component';
import { FilmService } from './film.service';
import { ListsService } from '../lists/lists.service';
import { Film } from '../Interfaces/Film/film.component';

import { UserComponent } from '../user/user.component';
import { environment } from '../../environments/environment';

const NUM_ELEMENS_FROM_PAGE = 10;

@Component({
  selector: 'films-component',
  templateUrl: './films.html',
  styleUrls: ['./films.css']
})

export class FilmsComponent {
  public URL = environment.url.substring(0, environment.url.length - 1);
  public films: Film[] = [];
  public filmsCarousel: Film[] = [];

  private isShowFilms = true;

  private totalPages: number;
  private countPages = 1;
  public morePages = true;

  public addedContent = false;
  public errorAddedContent = false;

  public allFilms = {
    "filter-active": true
  }

  public bestFilms = {
    "filter-active": false
  }

  constructor(private decorator: DecoratorService, private userComponent: UserComponent, private commonFunction: CommonFunction, private filmService: FilmService, private serviceList: ListsService) {
    this.decorator.activeButton("films");
  }

  ngOnInit() {
    this.loadCorousel();
    this.refresh(0, NUM_ELEMENS_FROM_PAGE);
  }

  private loadCorousel() {
    this.filmService.getLastAdded(4).subscribe(
      films => {
        this.filmsCarousel = films;
        this.filmsCarousel[0].firstInList = true;
      }
    );
  }

  private cleanLets() {
    this.films = [];
    this.countPages = 1;
    this.morePages = true;
    this.allFilms["filter-active"] = false;
    this.bestFilms["filter-active"] = false;
  }

  public showFilms() {
    this.cleanLets();
    this.isShowFilms = true;
    this.allFilms["filter-active"] = true;
    this.totalPages = this.commonFunction.addElementsToArray(this.films, this.filmService.getFilms(0, NUM_ELEMENS_FROM_PAGE));
  }

  public showBestFilms() {
    this.cleanLets();
    this.isShowFilms = false;
    this.bestFilms["filter-active"] = true;
    this.totalPages = this.commonFunction.addElementsToArray(this.films, this.filmService.getBestFilms(0, NUM_ELEMENS_FROM_PAGE));
  }

  private refresh(page: number, size: number) {
    this.totalPages = this.commonFunction.addElementsToArray(this.films, this.filmService.getFilms(page, size));
    this.decorator.loadSpinner = false;
  }

  private refreshBest(page: number, size: number) {
    this.totalPages = this.commonFunction.addElementsToArray(this.films, this.filmService.getBestFilms(page, size));
    this.decorator.loadSpinner = false;
  }

  private loadMore() {
    this.decorator.loadSpinner = true;

    setTimeout(() => {
      if (this.isShowFilms) {
        this.refresh(this.countPages, NUM_ELEMENS_FROM_PAGE);
      } else {
        this.refreshBest(this.countPages, NUM_ELEMENS_FROM_PAGE);
      }
      this.countPages++;
      if (this.countPages >= this.totalPages) {
        this.morePages = false;
      }
    }, 500);
  }

  private addElemToList(nameList: string, filmName: string){
    let list = {
      name: nameList,
      films: [filmName],
    }

    this.serviceList.addElement(list).subscribe(
      result =>{
        if (result == true){
          this.closeAlerts();
          this.addedContent = true;
          setTimeout(() => this.closeAddedContentAlert(), 20000);
        }else{
          this.closeAlerts();
          this.errorAddedContent = true;
          setTimeout(() => this.closeErrorAddedContentAlert(), 20000);
        }
      }
    )
  }

  private closeAddedContentAlert(){
    this.addedContent = false;
  }

  private closeErrorAddedContentAlert(){
    this.errorAddedContent = false;
  }

  private closeAlerts(){
    this.addedContent = false;
    this.errorAddedContent = false;
  }

}

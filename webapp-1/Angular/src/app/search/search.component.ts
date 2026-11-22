import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';

import { DecoratorService } from '../header/decorator.service';

import { CommonFunction } from '../common-code/commonfunction.component';
import { SearchService } from './search.service';

import { UserComponent } from '../user/user.component';

import { ListsService } from '../lists/lists.service';
import { environment } from '../../environments/environment';

const NUM_ELEMENS_FROM_PAGE = 4;

@Component({
  selector: 'search-component',
  templateUrl: './search.html',
  styleUrls: ['./search.css']
})

export class SearchComponent {
  public URL = environment.url.substring(0, environment.url.length - 1);
  public contents = [];
  private countPages = [1, 1, 1];
  private totalPages = [0, 0, 0];
  public morePages = false;
  private termSearch = "titulo";
  public search = "";
  public term = new FormControl();
  public termType = new FormControl();
  public addedContent = false;
  public errorAddedContent = false;

  constructor(private decorator: DecoratorService, private userComponent: UserComponent, private commonFunction: CommonFunction, private searchService: SearchService, private serviceList: ListsService) {
    this.decorator.activeButton("search");

    this.term.valueChanges
      .debounceTime(400)
      .subscribe(search => {
        this.search = search;
        this.contents = [];
        this.loadSearch();
      });

    this.termType.valueChanges.subscribe(type => {
      this.termSearch = type
      this.contents = [];
      this.loadSearch();
    });
  }

  private loadSearch() {
    if (this.search !== "") {
      this.refreshFilm(0, NUM_ELEMENS_FROM_PAGE);
      this.refreshShow(0, NUM_ELEMENS_FROM_PAGE);
      this.refreshBook(0, NUM_ELEMENS_FROM_PAGE);
    } else {
      this.morePages = false;
    }
  }

  private loadElementsArray(elements, type: string) {
    for (let elem of elements) {
      elem.type = type;
      this.contents.push(elem);
    }
  }

  private refreshFilm(page: number, size: number) {
    this.refreshElement(this.searchService.getFilms(page, size, this.termSearch, this.search), "film", 0);
  }

  private refreshShow(page: number, size: number) {
    this.refreshElement(this.searchService.getShows(page, size, this.termSearch, this.search), "show", 1);
  }

  private refreshBook(page: number, size: number) {
    this.refreshElement(this.searchService.getBooks(page, size, this.termSearch, this.search), "book", 2);
  }

  private refreshElement(callback, type: string, itemPage: number) {
    callback.subscribe(
      elems => {
        this.loadElementsArray(elems.content, type);
        this.totalPages[itemPage] = elems.totalPages;
        this.checkLoadMore();
      }
    );
  }

  private loadMore() {
    this.decorator.loadSpinner = true;

    setTimeout(() => {
      if (this.countPages[0] < this.totalPages[0]) {
        this.refreshFilm(this.countPages[0], NUM_ELEMENS_FROM_PAGE);
        this.countPages[0]++;
      }

      if (this.countPages[1] < this.totalPages[1]) {
        this.refreshShow(this.countPages[1], NUM_ELEMENS_FROM_PAGE);
        this.countPages[1]++;
      }

      if (this.countPages[2] < this.totalPages[2]) {
        this.refreshBook(this.countPages[2], NUM_ELEMENS_FROM_PAGE);
        this.countPages[2]++;
      }
    }, 500);
  }

  private checkLoadMore() {
    this.decorator.loadSpinner = false;
    
    if ((this.countPages[0] < this.totalPages[0]) || (this.countPages[1] < this.totalPages[1]) || (this.countPages[2] < this.totalPages[2])) {
      this.morePages = true;
    } else {
      this.morePages = false;
    }
  }

  private addElemToList(nameList: string, typeContent: string, elemName: string){
    let list;

    if (typeContent == "/serie/"){
        list = {
          name: nameList,
          shows: [elemName],
        }
    }else if (typeContent == "/pelicula/"){
      list = {
        name: nameList,
        films: [elemName],
      }
    }else{
      list = {
        name: nameList,
        books: [elemName],
      }
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
    );
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

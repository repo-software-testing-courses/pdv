import { Component } from '@angular/core';

import { DecoratorService } from '../header/decorator.service';

import { CommonFunction } from '../common-code/commonfunction.component';
import { BookService } from './book.service';
import { ListsService } from '../lists/lists.service';
import { Book } from '../Interfaces/Book/book.component';

import { UserComponent } from '../user/user.component';
import { environment } from '../../environments/environment';

const NUM_ELEMENS_FROM_PAGE = 10;

@Component({
  selector: 'books-component',
  templateUrl: './books.html',
  styleUrls: ['./books.css']
})

export class BooksComponent {
  public URL = environment.url;
  public books: Book[] = [];
  public booksCarousel: Book[] = [];

  private isShowBooks = true;

  private totalPages: number;
  private countPages = 1;
  public morePages = true;

  public addedContent = false;
  public errorAddedContent = false;

  public allBooks = {
    "filter-active": true
  }

  public bestBooks = {
    "filter-active": false
  }

  constructor(private decorator: DecoratorService, public userComponent: UserComponent, private commonFunction: CommonFunction, private bookService: BookService, private serviceList: ListsService) { 
    this.decorator.activeButton("books");
  }

  ngOnInit() {
    this.loadCorousel();
    this.refresh(0, NUM_ELEMENS_FROM_PAGE);
  }

  private loadCorousel() {
    this.bookService.getLastAdded(4).subscribe(
      books => {
        this.booksCarousel = books;
        this.booksCarousel[0].firstInList = true;
      }
    );
  }

  private cleanLets() {
    this.books = [];
    this.countPages = 1;
    this.morePages = true;
    this.allBooks["filter-active"] = false;
    this.bestBooks["filter-active"] = false;
  }

  public showBooks() {
    this.cleanLets();
    this.isShowBooks = true;
    this.allBooks["filter-active"] = true;
    this.totalPages = this.commonFunction.addElementsToArray(this.books, this.bookService.getBooks(0, NUM_ELEMENS_FROM_PAGE));
  }

  public showBestBooks() {
    this.cleanLets();
    this.isShowBooks = false;
    this.bestBooks["filter-active"] = true;
    this.totalPages = this.commonFunction.addElementsToArray(this.books, this.bookService.getBestBooks(0, NUM_ELEMENS_FROM_PAGE));
  }

  private refresh(page: number, size: number) {
    this.totalPages = this.commonFunction.addElementsToArray(this.books, this.bookService.getBooks(page, size));
    this.decorator.loadSpinner = false;
  }

  private refreshBest(page: number, size: number) {
    this.totalPages = this.commonFunction.addElementsToArray(this.books, this.bookService.getBestBooks(page, size));
    this.decorator.loadSpinner = false;
  }

  private loadMore() {
    this.decorator.loadSpinner = true;

    setTimeout(() => {
      if (this.isShowBooks) {
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

  public addElemToList(nameList: string, bookName: string){
    let list = {
      name: nameList,
      books: [bookName],
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

import { Component } from '@angular/core';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { DomSanitizer, SafeResourceUrl, SafeUrl } from '@angular/platform-browser';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { LocationStrategy } from '@angular/common';

import { DecoratorService } from '../header/decorator.service';

import { BookService } from './book.service';
import { Book } from '../Interfaces/Book/book.component';
import { CommentBook } from '../Interfaces/Book/commentbook.component';
import { PointBook } from '../Interfaces/Book/pointbook.component';

import { UserComponent } from '../user/user.component';
import { environment } from '../../environments/environment';

@Component({
  selector: 'book-component',
  templateUrl: './book.html',
  styleUrls: ['./book.css']
})

export class BookComponent {
  public URL = environment.url;
  private isPopState = false;
  private nameBook: string;
  public book: Book;
  public startBook: number;
  private startBookUser = 0;
  public commentsBook: CommentBook[] = [];
  public relationBooks: Book[] = [];
  public validationMessageForm: FormGroup;

  constructor(private decorator: DecoratorService, public userComponent: UserComponent, private sanitizer: DomSanitizer, private router: Router, private activatedRoute: ActivatedRoute, private bookService: BookService, private fb: FormBuilder, private locStrat: LocationStrategy) { 
    this.decorator.clearActive();

    this.validationMessageForm = fb.group({
      'message': [null, Validators.required],
    });
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe(
      params => {
        this.nameBook = params["name"];
        this.loadBook();
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

  private loadBook() {
    this.bookService.getBook(this.nameBook).subscribe(
      book => {
        this.book = book;
        this.loadRealted();
        this.loadPointBook();
      }
    );
  }

  private loadPointBook(){
    this.bookService.getPointsBook(this.nameBook).subscribe(
      pointBook => 
      {
        let sum = 0;

        for(let pBook of pointBook){
          if(this.userComponent.user !== undefined && this.userComponent.user.name === pBook.user.name){
            this.startBookUser = pBook.points;
          }

          sum += pBook.points;
        }

        sum /= pointBook.length;
        this.startBook = sum;
      }
    );
  }

  private loadComments() {
    this.bookService.getCommentsBook(this.nameBook).subscribe(
      comments => {
        this.commentsBook = comments;
      }
    );
  }

  private loadRealted() {
    this.bookService.getRelationBook(this.book.id, 0, 8).subscribe(
      related => this.relationBooks = related.content
    );
  }

  public changePointUser() {
    this.bookService.updatePoint(this.nameBook, { points: this.startBookUser }).subscribe(
      response => {
        this.loadPointBook();
      }
    )
  }
  public deleteComment(id: number){
    this.bookService.deleteCommentBook(id).subscribe(
      response => {
        this.loadComments();
      }
    );
  }

  private newComment(message: string) {
    this.validationMessageForm = this.fb.group({
      'message': [null, Validators.required],
    });

    this.bookService.newCommentBook(this.nameBook, { comment: message }).subscribe(
      response => {
        this.loadComments();
      }
    )
  }
}
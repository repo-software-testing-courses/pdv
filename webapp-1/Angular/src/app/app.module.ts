import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule} from '@angular/forms';
import { HttpModule, JsonpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { routing }  from './app.routing';

import { AppComponent } from './app.component';
import { IndexComponent } from './index/index.component';
import { LoginComponent } from './login/login.component';
import { ForgotComponent } from './forgot-pasword/forgot.component';
import { ChangePassComponent } from './forgot-pasword/changepass.component';
import { SearchComponent } from './search/search.component';
import { FilmsComponent } from './films/films.component';
import { FilmComponent } from './films/film.component';
import { ShowsComponent } from './shows/shows.component';
import { ShowComponent } from './shows/show.component';
import { BooksComponent } from './books/books.component';
import { BookComponent } from './books/book.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { ProfileComponent } from './profile/profile.component';
import { ErrorComponent } from './error/error.component';
import { AdministrationIndexComponent } from './administration/administration-index.component';
import { AdministrationComponent } from './administration/administration.component';
import { NewContentComponent } from './administration/newContent.component';

import { PreventLoggedInAccess } from './login/PreventLoggedInAccess.component';
import { PreventProfileInAccess } from './profile/PreventProfileInAccess.component';
import { PreventAdminAccess } from './administration/PreventAdminAccess.component';

import { CommonFunction } from './common-code/commonfunction.component';
import { UserComponent } from './user/user.component';
import { LoginService } from './login/login.service';
import { UserService } from './user/user.service';
import { ListsService } from './lists/lists.service';
import { DecoratorService } from './header/decorator.service';
import { SearchService } from './search/search.service';
import { FilmService } from './films/film.service';
import { ShowService } from './shows/show.service';
import { BookService } from './books/book.service'
import { GenderService } from './genders/gender.service';
import { ImageService } from './uploadimage/image.service';

@NgModule({
  declarations: [
    AppComponent, 
    HeaderComponent, 
    FooterComponent, 
    IndexComponent, 
    LoginComponent,
    ProfileComponent,
    ForgotComponent,
    ChangePassComponent,
    SearchComponent,
    FilmsComponent,
    FilmComponent,
    ShowComponent,
    ShowsComponent,
    BookComponent,
    BooksComponent,
    AdministrationComponent,
    AdministrationIndexComponent,
    NewContentComponent,
    ErrorComponent
  ],
  imports: [
    BrowserModule, 
    FormsModule, 
    ReactiveFormsModule,
    HttpModule, 
    HttpClientModule,
    JsonpModule, 
    routing, 
    NgbModule.forRoot()
  ],
  providers: [
    PreventAdminAccess,
    PreventLoggedInAccess,
    PreventProfileInAccess,
    UserComponent,
    CommonFunction,
    DecoratorService,
    LoginService,
    UserService,
    ListsService,
    SearchService,
    FilmService,
    ShowService,
    BookService,
    GenderService,
    ImageService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

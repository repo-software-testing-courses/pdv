import { Routes, RouterModule } from '@angular/router';

import { PreventLoggedInAccess } from './login/PreventLoggedInAccess.component';
import { PreventProfileInAccess } from './profile/PreventProfileInAccess.component';
import { PreventAdminAccess } from './administration/PreventAdminAccess.component';

import { SearchComponent } from './search/search.component';

import { LoginComponent } from './login/login.component';

import { ProfileComponent } from './profile/profile.component';

import { ForgotComponent } from './forgot-pasword/forgot.component';
import { ChangePassComponent } from './forgot-pasword/changepass.component';

import { FilmsComponent } from './films/films.component';
import { FilmComponent } from './films/film.component';
import { IndexComponent } from './index/index.component';

import { ShowsComponent } from './shows/shows.component';
import { ShowComponent } from './shows/show.component';

import { BooksComponent } from './books/books.component';
import { BookComponent } from './books/book.component';

import { AdministrationIndexComponent } from './administration/administration-index.component';
import { AdministrationComponent } from './administration/administration.component';
import { NewContentComponent } from './administration/newContent.component';

import { ErrorComponent } from './error/error.component';

const appRoutes = [
  { path: '', component: IndexComponent },
  { path: 'login', component: LoginComponent, canActivate: [PreventLoggedInAccess] },
  { path: 'perfil', component: ProfileComponent, canActivate: [PreventProfileInAccess] },
  { path: 'activarusuario/:name', component: LoginComponent, canActivate: [PreventLoggedInAccess] },
  { path: 'olvidocontra', component: ForgotComponent, canActivate: [PreventLoggedInAccess] },
  { path: 'cambiarcontra/:code', component: ChangePassComponent, canActivate: [PreventLoggedInAccess] },
  { path: 'contracambiada/:change', component: LoginComponent, canActivate: [PreventLoggedInAccess] },
  { path: 'busqueda', component: SearchComponent },
  { path: 'peliculas', component: FilmsComponent },
  { path: 'pelicula/:name', component: FilmComponent },
  { path: 'series', component: ShowsComponent },
  { path: 'serie/:name', component: ShowComponent },
  { path: 'libros', component: BooksComponent },
  { path: 'libro/:name', component: BookComponent },
  { path: 'administracion', component: AdministrationIndexComponent, canActivate: [PreventAdminAccess]},
  { path: 'administracion/:type/:name', component: AdministrationComponent, canActivate: [PreventAdminAccess]},
  { path: 'subirContenido', component: NewContentComponent, canActivate: [PreventAdminAccess]},
  { path: '**', component: ErrorComponent }
]

export const routing = RouterModule.forRoot(appRoutes);

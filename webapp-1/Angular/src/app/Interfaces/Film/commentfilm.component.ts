import { Film } from './film.component';
import { User } from '../User/user.component';

export interface CommentFilm {
    id?:number;	
	film?: Film;
    user?: User;
	comment:string;
}
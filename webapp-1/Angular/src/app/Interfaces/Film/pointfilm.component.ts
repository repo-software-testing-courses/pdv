import { Film } from './film.component';
import { User } from '../User/user.component';

export interface PointFilm {
    id?: number;	
	film?: Film;
    user?: User;
	points: number;
}
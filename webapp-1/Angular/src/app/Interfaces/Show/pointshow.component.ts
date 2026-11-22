import { Show } from './show.component';
import { User } from '../User/user.component';

export interface PointShow {
    id?:number;	
	show?: Show;
    user?: User;
	points:number;
}
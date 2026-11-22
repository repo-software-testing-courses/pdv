import { Show } from './show.component';
import { User } from '../User/user.component';

export interface CommentShow {
    id?:number;	
	show?: Show;
    user?: User;
	comment:string;
}
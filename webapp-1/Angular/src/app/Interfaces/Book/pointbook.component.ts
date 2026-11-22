import { Book } from "./book.component";
import { User } from "../User/user.component";

export interface PointBook {
    id?:number;	
	book?: Book;
    user?: User;
	points:number;
}
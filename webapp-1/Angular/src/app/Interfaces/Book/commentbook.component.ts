import { Book } from "./book.component";
import { User } from "../User/user.component";

export interface CommentBook{
    id?: number;
    book?: Book;
    user?: User;
    comment: string;
}
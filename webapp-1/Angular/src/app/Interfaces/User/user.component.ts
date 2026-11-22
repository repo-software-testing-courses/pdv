export interface User {
	id?: number;
	name?: string;
	password?: string,
	email?: string;
	image?: string;
	activatedUser?: boolean;
	roles?: string[];
}
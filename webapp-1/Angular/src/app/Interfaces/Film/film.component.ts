export interface Film {
	id?: number;
	directors: string;
    actors: string;
    name: string;
    synopsis: string;
	image?: string;
	trailer: string;
	year: number;
	firstInList?: boolean;
	genders?: any;
}
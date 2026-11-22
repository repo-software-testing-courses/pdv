export interface Book{
    id?: number;
    authors: string;
    name: string;
    synopsis: string;
    image?: string;
    year: number;
    firstInList?: boolean;
	genders?: any;

}
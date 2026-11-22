import { Injectable } from '@angular/core';

@Injectable()
export class CommonFunction {

    private numPages: number;
    addElementsToArray(array, callback) {
        callback.subscribe(
            elements => {
                for (let film of elements.content) {
                    array.push(film);
                }

                this.numPages = elements.totalPages;
            }
        );

        return this.numPages;
    }
}
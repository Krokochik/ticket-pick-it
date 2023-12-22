import {Injectable} from '@angular/core';

@Injectable({
    providedIn: "root"
})
export class DataService {
    savedState: any = {};
    messages: any = {};

    constructor() {}

}

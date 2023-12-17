import {AfterContentChecked, ChangeDetectorRef, Component, ElementRef, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ComponentDef} from "../component";
import {Router} from "@angular/router";
import {AuthService} from "../../services/AuthService";
import {catchError, filter, Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {FilterPipe} from "../../pipes/filter.pipe";
import {FormsModule} from "@angular/forms";
import {LoaderComponent} from "../loader/loader.component";

export interface Clinic {
    name: string;
    address: string;
}

@Component({
    selector: 'app-clinics',
    standalone: true,
    imports: [CommonModule, FilterPipe, FormsModule, LoaderComponent],
    templateUrl: './clinics.component.html',
    styleUrl: './clinics.component.scss'
})
export class ClinicsComponent extends ComponentDef implements OnInit {
    clinics: Array<Clinic>;
    searchText: string;
    ready = false;

    constructor(elementRef: ElementRef,
                public router: Router,
                private http: HttpClient) {
        super(elementRef);
    }

    ngOnInit() {
        this.http.get<Clinic[]>("/server/clinics")
            .subscribe(data => {
                this.clinics = data as Clinic[];
                this.ready = true;
            });
    }
}

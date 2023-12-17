import {Component, ElementRef, Input, OnInit} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {ComponentDef} from "../component";
import {HttpClient, HttpHeaders, HttpResponse} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {OrderComponent} from "../order/order.component";
import {FilterPipe} from "../../pipes/filter.pipe";
import {FormsModule} from "@angular/forms";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {LoaderComponent} from "../loader/loader.component";
import {Speciality} from "../specialities/specialities.component";

export interface Medic {
    firstName: string,
    lastName: string,
    photo: string
}

@Component({
    selector: 'app-medics',
    standalone: true,
    imports: [CommonModule, FilterPipe, FormsModule, NgOptimizedImage, LoaderComponent],
    templateUrl: './medics.component.html',
    styleUrl: './medics.component.scss'
})
export class MedicsComponents extends ComponentDef implements OnInit {
    clinicId: number;
    ready: boolean;
    medics: Medic[];
    speciality: Speciality;
    imagesLoad = new BehaviorSubject<number>(0);
    specialityLoaded = false;

    constructor(elementRef: ElementRef,
                orderComponent: OrderComponent,
                route: ActivatedRoute,
                protected router: Router,
                protected http: HttpClient) {
        super(elementRef);
        this.clinicId = orderComponent.id;
        const specialityCode = route.snapshot.paramMap.get("code") || "";
        this.speciality = {
            code: specialityCode, name: specialityCode
        };
    }

    ngOnInit() {
        this.http.get<Medic[]>(`/server/clinic/${this.clinicId}/speciality/${this.speciality.code}/medics`)
            .subscribe(medics => {
                this.medics = medics;
            });
        this.http.get(`/server/speciality/${this.speciality.code}/name`,
            {responseType: "text"})
            .subscribe(name => {
                this.speciality.name = name;
                this.specialityLoaded = true;
            });
        this.imagesLoad.subscribe(v => {
            console.log(v)
            if (this.medics && this.specialityLoaded && (v == this.medics?.length)) {
                console.log(this.ready, v)
                this.ready = true;
            }
        })
    }
}

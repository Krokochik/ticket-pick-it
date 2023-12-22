import {ChangeDetectorRef, Component, ElementRef, Input, NgZone, OnInit} from '@angular/core';
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
import {ChangeDetection} from "@angular/cli/lib/config/workspace-schema";
import {NetService} from "../../services/NetService";

export interface Medic {
    firstName: string,
    lastName: string,
    photo: string,
    office: number
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

    constructor(elementRef: ElementRef,
                orderComponent: OrderComponent,
                route: ActivatedRoute,
                protected router: Router,
                protected netService: NetService) {
        super(elementRef);
        this.clinicId = orderComponent.id;
        const specialityCode = route.snapshot.paramMap.get("specialityCode") || "";
        this.speciality = {
            code: specialityCode, name: ""
        };
    }

    checkReadyAndSet() {
        this.ready =  this.medics?.length > 0 &&
            this.speciality.name != "";
    }

    ngOnInit() {
        this.netService.getMedics(this.clinicId, this.speciality)
            .subscribe(medics => {
                this.medics = medics;
                let photos: string[] = [];
                medics.forEach(medic => photos.push(medic.photo));
                this.checkReadyAndSet();
            });
        this.netService.getSpecialityName(this.speciality.code)
            .subscribe(name => {
                this.speciality.name = name;
                this.checkReadyAndSet();
            });
    }
}

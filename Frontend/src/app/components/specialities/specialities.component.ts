import {Component, ElementRef, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {ComponentDef} from "../component";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {OrderComponent} from "../order/order.component";
import {FilterPipe} from "../../pipes/filter.pipe";
import {FormsModule} from "@angular/forms";

export interface Speciality {
    name: string,
    code: string
}

@Component({
  selector: 'app-specialities',
  standalone: true,
    imports: [CommonModule, FilterPipe, FormsModule],
  templateUrl: './specialities.component.html',
  styleUrl: './specialities.component.scss'
})
export class SpecialitiesComponent extends ComponentDef implements OnInit {
    id: number;
    ready: boolean;
    specialities: Speciality[];

    constructor(elementRef: ElementRef,
                orderComponent: OrderComponent,
                public router: Router,
                private http: HttpClient) {
        super(elementRef);
        this.id = orderComponent.id;
    }

    ngOnInit() {
        this.http.get<Speciality[]>(`/server/clinic/${this.id}/specialities`)
            .subscribe(data => {
                this.specialities = data;
                this.ready = true;
            });
    }

}

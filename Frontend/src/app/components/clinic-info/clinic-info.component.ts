import {Component, ElementRef, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {ComponentDef} from "../component";
import {HttpClient} from "@angular/common/http";
import {Observable, Subject} from "rxjs";
import {OrderComponent} from "../order/order.component";

export interface ClinicInfo {
    name: string
    address: string
    phone: string
}

@Component({
  selector: 'app-clinic-info',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './clinic-info.component.html',
  styleUrl: './clinic-info.component.scss'
})
export class ClinicInfoComponent extends ComponentDef implements OnInit {
    @Input() id: number;
    ready: boolean;
    info: ClinicInfo;

    constructor(elementRef: ElementRef,
                private orderComponent: OrderComponent,
                private http: HttpClient) {
        super(elementRef);
    }


    ngOnInit() {
        this.http.get<ClinicInfo>(`/server/clinic/${this.id}/info`)
            .subscribe(data => {
                this.info = data;
                this.ready = true;
                this.orderComponent.ready[1] = true;
            });
    }
}

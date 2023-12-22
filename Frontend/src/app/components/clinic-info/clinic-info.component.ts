import {Component, ElementRef, inject, Input, OnInit, Optional} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ComponentDef} from "../component";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {Observable, Subject} from "rxjs";
import {OrderComponent} from "../order/order.component";
import {NetService} from "../../services/NetService";

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
                private netService: NetService,
                private orderComponent: OrderComponent) {
        super(elementRef);
    }


    ngOnInit() {
        this.netService.getClinicInfo(this.id)
            .subscribe(data => {
                this.info = data;
                this.ready = true;
                if (this.orderComponent)
                    this.orderComponent.ready[1] = true;
            });
    }
}

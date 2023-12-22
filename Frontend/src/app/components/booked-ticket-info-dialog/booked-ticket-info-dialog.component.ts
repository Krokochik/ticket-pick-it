import {Component, ElementRef, OnInit} from '@angular/core';
import {CommonModule, formatDate, NgOptimizedImage} from '@angular/common';
import {DialogRef} from "@ngneat/dialog";
import {Speciality} from "../specialities/specialities.component";
import {Ticket} from "../time-select/time-select.component";
import {ComponentDef} from "../component";
import {RouterLink} from "@angular/router";
import {ClinicInfo, ClinicInfoComponent} from "../clinic-info/clinic-info.component";

@Component({
    selector: 'app-booked-ticket-info-dialog',
    standalone: true,
    imports: [
        CommonModule,
        NgOptimizedImage,
        RouterLink,
        ClinicInfoComponent
    ],
    templateUrl: './booked-ticket-info-dialog.component.html',
    styleUrl: './booked-ticket-info-dialog.component.scss'
})
export class BookedTicketInfoDialogComponent extends ComponentDef {
    constructor(elementRef: ElementRef,
                protected ref: DialogRef<{
                    clinic: ClinicInfo,
                    speciality: Speciality
                    medic: any,
                    date: Date
                }, boolean>) {
        super(elementRef);
        ref.updateConfig({
            maxWidth: "500px"
        })
    }
}

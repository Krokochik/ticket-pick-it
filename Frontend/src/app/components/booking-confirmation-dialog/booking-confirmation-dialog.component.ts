import {Component, ElementRef, OnInit, ViewEncapsulation} from '@angular/core';
import {CommonModule, DatePipe, formatDate, NgOptimizedImage} from '@angular/common';
import {DialogRef} from "@ngneat/dialog";
import {ComponentDef} from "../component";
import {Speciality} from "../specialities/specialities.component";
import {da} from "date-fns/locale";
import {Ticket} from "../time-select/time-select.component";
import {RouterLink} from "@angular/router";

@Component({
    selector: 'app-booking-confirmation-dialog',
    standalone: true,
    imports: [CommonModule, NgOptimizedImage, RouterLink],
    templateUrl: './booking-confirmation-dialog.component.html',
    styleUrl: './booking-confirmation-dialog.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class BookingConfirmationDialogComponent extends ComponentDef implements OnInit {
    warning: boolean;
    warningType: string;
    warningData: any;

    constructor(elementRef: ElementRef,
                protected ref: DialogRef<{
                    clinic: string,
                    speciality: Speciality
                    medic: string,
                    date: Date,
                    myTickets: Ticket[]
                }, boolean>) {
        super(elementRef);
        ref.updateConfig({
            maxWidth: "400px",
            enableClose: false
        })
    }

    areDatesClose(d1: Date, d2: Date): boolean {
        const timeDifference = Math.abs(d1.getTime() - d2.getTime());
        const fourHoursInMilliseconds = 4 * 60 * 60 * 1000;
        return timeDifference <= fourHoursInMilliseconds;
    }

    ngOnInit() {
        this.ref.data.myTickets.forEach(ticket => {
            if (this.areDatesClose(ticket.date, this.ref.data.date)) {
                this.warningType = "date";
                this.warningData = formatDate(ticket.date, "h:mm a, MMM dd", "en-US");
                this.warning = true;
            } else if (ticket.speciality.code.toLowerCase() ==
                this.ref.data.speciality.code.toLowerCase()) {
                this.warningType = "speciality";
                this.warningData = ticket.speciality.name
                this.warning = true;
            }
        })
    }
}

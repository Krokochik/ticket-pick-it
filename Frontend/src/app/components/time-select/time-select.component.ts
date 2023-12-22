import {Component, ElementRef, inject, OnDestroy, OnInit} from '@angular/core';
import {CommonModule, DatePipe, formatDate} from '@angular/common';
import {ComponentDef} from "../component";
import {Speciality} from "../specialities/specialities.component";
import {ActivatedRoute, Router} from "@angular/router";
import {NetService} from "../../services/NetService";
import {OrderComponent} from "../order/order.component";
import {AuthService} from "../../services/AuthService";
import {DataService} from "../../services/DataService";
import {DialogRef, DialogService} from "@ngneat/dialog";
import {BookingConfirmationDialogComponent} from "../booking-confirmation-dialog/booking-confirmation-dialog.component";
import {ClinicInfo} from "../clinic-info/clinic-info.component";
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, retry, Subject} from "rxjs";

export interface Ticket {
    clinicId: number,
    speciality: Speciality,
    medicId: number,
    date: Date
}

@Component({
    selector: 'app-time-select',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './time-select.component.html',
    styleUrl: './time-select.component.scss'
})
export class TimeSelectComponent extends ComponentDef implements OnInit {
    ready: boolean;
    speciality: Speciality;
    medic: {
        id: number,
        firstName: string,
        lastName: string
    };
    clinic: number;
    date: string;
    availableTickets: Date[] = [];
    myTickets$ = new BehaviorSubject<Ticket[]>([]);

    constructor(elementRef: ElementRef,
                route: ActivatedRoute,
                orderComponent: OrderComponent,
                private netService: NetService,
                private authService: AuthService,
                private router: Router,
                private dataService: DataService,
                private dialog: DialogService,
                private http: HttpClient) {
        super(elementRef);
        this.clinic = orderComponent.id;
        this.speciality = {
            code: route.snapshot.paramMap.get("specialityCode") || "",
            name: ""
        };
        this.medic = {
            id: +(route.snapshot.paramMap.get("medicId") || ""),
            firstName: "", lastName: ""
        };
        this.date = route.snapshot.paramMap.get("date") || "";
    }

    checkIfReady(): boolean {
        return this.availableTickets?.length > 0 &&
            this.medic.firstName != "" &&
            this.medic.lastName != "" &&
            this.speciality.name != "";
    }

    ngOnInit() {
        this.netService.getTickets(this.clinic, this.speciality, this.medic.id)
            .subscribe(tickets => {
                tickets.forEach(ticket => {
                    if (formatDate(new Date(Date.parse(ticket)),
                        "M-d-yy", "en-US") === this.date) {
                        this.availableTickets.push(new Date(Date.parse(ticket)));
                    }
                })
                this.ready = this.checkIfReady();
            })

        this.netService.getSpecialityName(this.speciality.code)
            .subscribe(name => {
                this.speciality.name = name;
                this.ready = this.checkIfReady();
            });

        this.netService.getMedicName(this.clinic, this.speciality, this.medic.id)
            .subscribe(medic => {
                this.medic.firstName = medic.firstName;
                this.medic.lastName = medic.lastName;
                this.ready = this.checkIfReady();
            })

        this.netService.getMyTickets(this.myTickets$);
    }

    capitalize(s: string) {
        return (s.at(0) || "").toUpperCase() + s.substring(1);
    }

    bookTicket(date: Date) {
        const sub = this.authService.auth$.subscribe(auth => {
            if (auth) {
                this.http.get<ClinicInfo>(`/server/clinic/${this.clinic}/info`)
                    .subscribe(info => {
                        const ref = this.dialog.open(BookingConfirmationDialogComponent, {
                            data: {
                                clinic: this.capitalize(info.name.toLowerCase()),
                                medic: this.capitalize(this.medic.firstName) + " " +
                                    this.capitalize(this.medic.lastName) + ", " +
                                    this.speciality.name.toLowerCase(),
                                date: date,
                                myTickets: this.myTickets$.value,
                                speciality: this.speciality
                            }
                        });
                        ref.afterClosed$.subscribe(result => {
                            if (result == true) {
                                this.http.post("/server/book-ticket", {
                                    clinicId: this.clinic,
                                    speciality: this.speciality.code,
                                    medicId: this.medic.id,
                                    date: formatDate(date, "y-MM-ddTHH:mm", "en-US")
                                }, {responseType: "text"}).subscribe(() => {
                                    this.router.navigate(["/account"]);
                                })
                                this.availableTickets.splice(
                                    this.availableTickets.indexOf(date));
                            }
                        })
                    });
            } else {
                this.dataService.messages["ticket-booking"] = window.location.pathname;
                this.router.navigate(["/login"], {
                    queryParams: {
                        origin: "ticket-booking"
                    }
                })
            }
            sub.unsubscribe();
        })
        this.authService.checkAuthentication();
    }
}

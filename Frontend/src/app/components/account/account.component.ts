import {Component, ElementRef, OnDestroy, OnInit} from '@angular/core';
import {CommonModule, formatDate} from '@angular/common';
import {ComponentDef} from "../component";
import {NetService} from "../../services/NetService";
import {catchError, Observable, Subject, Subscription} from "rxjs";
import {Ticket} from "../time-select/time-select.component";
import {Router, RouterLink} from "@angular/router";
import {ClinicInfo} from "../clinic-info/clinic-info.component";
import {tick} from "@angular/core/testing";
import {DialogService} from "@ngneat/dialog";
import {BookedTicketInfoDialogComponent} from "../booked-ticket-info-dialog/booked-ticket-info-dialog.component";
import {AuthService} from "../../services/AuthService";
import {HttpClient} from "@angular/common/http";
import {DataService} from "../../services/DataService";

@Component({
    selector: 'app-account',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './account.component.html',
    styleUrl: './account.component.scss'
})
export class AccountComponent extends ComponentDef implements OnInit, OnDestroy {
    ready: boolean;
    tickets$ = new Subject<Ticket[]>();
    tickets: any[] = [];
    sub: Subscription;

    constructor(elementRef: ElementRef,
                protected router: Router,
                private authService: AuthService,
                private netService: NetService,
                private dialog: DialogService,
                private http: HttpClient,
                private dataService: DataService) {
        super(elementRef);
    }

    ngOnDestroy() {
        this.sub.unsubscribe();
    }

    ngOnInit() {
        this.sub = this.authService.auth$.subscribe(auth => {
            if (!auth) {
                this.dataService.messages["account"] = "/account";
                this.router.navigate(["/login"], {
                    queryParams: {
                        origin: "account"
                    }
                })
            }
        });
        this.authService.checkAuthentication();
        this.tickets$.subscribe(tickets => {
            if (tickets.length == 0) this.ready = true;
            let ticketsCompiled = 0;
            for (let ticket of tickets) {
                let requestsComplete = 0;
                let medic: any, clinic: any;
                this.netService.getMedics(ticket.clinicId, ticket.speciality)
                    .subscribe(medics => {
                        medic = medics[ticket.medicId];
                        if (++requestsComplete == 2)
                            pushTicket();
                    })
                this.netService.getClinicInfo(ticket.clinicId)
                    .subscribe(info => {
                        clinic = Object.assign(info, {
                            id: ticket.clinicId
                        });
                        if (++requestsComplete == 2)
                            pushTicket();
                    })

                const pushTicket = () => {
                    if (++ticketsCompiled >= tickets.length)
                        this.ready = true;

                    this.tickets.push({
                        medic: {
                            id: ticket.medicId,
                            name: `${medic.firstName} ${medic.lastName}`,
                            office: medic.office
                        },
                        speciality: ticket.speciality,
                        date: ticket.date,
                        clinic: clinic
                    })
                }
            }
        });
        this.netService.getMyTickets(this.tickets$);
    }

    ticketClicked(ticket: any) {
        this.dialog.open(BookedTicketInfoDialogComponent, {
            data: {
                clinic: ticket.clinic,
                speciality: ticket.speciality,
                medic: ticket.medic,
                date: ticket.date
            }
        }).afterClosed$.subscribe(unbook => {
            if (unbook == true) {
                this.http.post("/server/unbook-ticket", {
                    clinicId: ticket.clinic.id,
                    speciality: ticket.speciality.code,
                    medicId: ticket.medic.id,
                    date: formatDate(ticket.date, "y-MM-ddTHH:mm", "en-US")
                }, {responseType: "text"}).subscribe(status => {
                    this.tickets.splice(
                        this.tickets.indexOf(ticket), 1
                    );
                })
            }
        })
    }

    logout() {
        this.authService.logout().pipe(
            catchError(err => {
                console.log("err")
                this.router.navigate(['/']);
                this.authService.checkAuthentication()
                return new Observable();
            })
        )
            .subscribe(() => {
                console.log("sub")
                this.router.navigate(['/'])
                this.authService.checkAuthentication()
            });
    }
}

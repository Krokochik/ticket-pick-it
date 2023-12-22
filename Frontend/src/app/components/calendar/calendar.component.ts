import {Component, ElementRef, HostListener, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {CommonModule, DatePipe, formatDate, NgOptimizedImage} from '@angular/common';
import {
    CalendarCommonModule,
    CalendarDayModule,
    CalendarEvent,
    CalendarMonthModule,
    CalendarWeekModule
} from "angular-calendar";
import {ComponentDef} from "../component";
import {addMonths} from "date-fns";
import {HttpClient} from "@angular/common/http";
import {OrderComponent} from "../order/order.component";
import {ActivatedRoute, Router} from "@angular/router";
import {Speciality} from "../specialities/specialities.component";
import {routes} from "../../app.routes";
import {da} from "date-fns/locale";
import {NetService} from "../../services/NetService";
import {DataService} from "../../services/DataService";

@Component({
    selector: 'app-calendar',
    standalone: true,
    imports: [CommonModule, CalendarMonthModule, CalendarWeekModule, CalendarDayModule, CalendarCommonModule, NgOptimizedImage],
    templateUrl: './calendar.component.html',
    styleUrl: './calendar.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class CalendarComponent extends ComponentDef implements OnInit, OnDestroy {
    ready: boolean;
    days: any[];
    month: Date = new Date();
    today: Date = new Date();
    events: CalendarEvent[] = [];
    tickets: Array<Date> = [];
    clinicId: number;
    speciality: Speciality;
    medicId: number;
    medic: any;

    constructor(elementRef: ElementRef,
                orderComponent: OrderComponent,
                route: ActivatedRoute,
                private cacheService: DataService,
                private netService: NetService,
                private router: Router) {
        super(elementRef);
        this.clinicId = orderComponent.id;
        this.speciality = {
            code: route.snapshot.paramMap.get("specialityCode") || "",
            name: ""
        };
        this.medicId = +(route.snapshot.paramMap.get("medicId") || "0");

        if (this.cacheService.savedState.calendar instanceof CalendarComponent) {
            const savedState = this.cacheService.savedState.calendar;
            if (savedState.clinicId == this.clinicId &&
                savedState.medicId == this.medicId &&
                savedState.speciality.code == this.speciality.code) {
                if (savedState.month instanceof Date) {
                    this.month = savedState.month;
                }
            }
        }
    }

    checkIfReady(): boolean {
        return this.speciality.name.length > 0 &&
            this.events.length > 0 &&
            this.medic?.firstName;
    }

    ngOnInit() {
        this.netService.getTickets(this.clinicId, this.speciality, this.medicId)
            .subscribe(dates => {
                dates.forEach(date => {
                    if (new Date(Date.parse(date)).setHours(12) >=
                        this.today.setHours(12)) {
                        this.events.push({
                            start: new Date(Date.parse(date)), color: {
                                primary: "", secondary: ""
                            }, title: ""
                        })
                        this.tickets.push(new Date(Date.parse(date)));
                    }
                })
                this.ready = this.checkIfReady();
            })

        this.netService.getSpecialityName(this.speciality.code)
            .subscribe(name => {
                this.speciality.name = name;
                this.ready = this.checkIfReady();
            });

        this.netService.getMedicName(this.clinicId, this.speciality, this.medicId)
            .subscribe(medic => {
                this.medic = medic;
                this.ready = this.checkIfReady();
            })
    }

    ngOnDestroy() {
        this.cacheService.savedState.calendar = this;
    }

    @HostListener("window:resize")
    resizeListener() {
        this.formatDays();
    }

    beforeViewRender(event: any) {
        this.days = event.header;
        this.formatDays();
    }

    dayClicked(event: {
        day: {
            date: Date,
            badgeTotal: number
        }
    }) {
        if (event.day.badgeTotal > 0) {
            const dateStr = formatDate(event.day.date, "M-d-yy", "en-US");
            this.router.navigate([
                `/order/clinic/${this.clinicId}/speciality/` +
                `${this.speciality.code}/medic/${this.medicId}/${dateStr}`
            ])
        }
    }

    formatDays() {
        if (document.body.offsetWidth > 768) {
            this.days.forEach((day: any) => {
                day.viewDate = formatDate(day.date, "EEEE", "en-US"); // Tuesday
            });
        } else if (document.body.offsetWidth > 320) {
            this.days.forEach((day: any) => {
                day.viewDate = formatDate(day.date, "E", "en-US"); // Tue
            });
        } else {
            this.days.forEach((day: any) => {
                day.viewDate = formatDate(day.date, "EEEEEE", "en-US"); // Tu
            });
        }
    }

    protected readonly addMonths = addMonths;
}

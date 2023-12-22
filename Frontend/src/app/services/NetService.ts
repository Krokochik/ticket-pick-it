import {HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable, retry, Subject} from "rxjs";
import {Speciality} from "../components/specialities/specialities.component";
import {Ticket} from "../components/time-select/time-select.component";
import {ClinicInfo} from "../components/clinic-info/clinic-info.component";
import {Medic} from "../components/medics/medics.component";

@Injectable({
    providedIn: "root"
})
export class NetService {
    constructor(private http: HttpClient) {
    }

    getSpecialityName(code: string) {
        return this.http.get(
            `/server/speciality/${code}/name`,
            {responseType: "text"}
        );
    }

    getMedicName(clinicId: number,
                 speciality: Speciality | String,
                 medicId: number) {
        if (!(speciality instanceof String)) {
            speciality = speciality.code;
        }
        return this.http.get<{
            firstName: string,
            lastName: string
        }>("/server/medic/name", {
            params: {
                "clinicId": clinicId,
                "speciality": speciality.toString(),
                "id": medicId
            }
        });
    }

    getTickets(clinicId: number,
               speciality: Speciality | String,
               medicId: number) {
        if (!(speciality instanceof String)) {
            speciality = speciality.code;
        }
        return this.http.get<string[]>("/server/" +
            `clinic/${clinicId}/` +
            `speciality/${speciality.toString()}/` +
            `medic/${medicId}/tickets`);
    }

    getMedics(clinicId: number,
              speciality: Speciality | String) {
        if (!(speciality instanceof String)) {
            speciality = speciality.code;
        }
        return this.http.get<Medic[]>(
            `/server/clinic/${clinicId}/` +
            `speciality/${speciality.toString()}/medics`)

    }

    getMyTickets(buff: Subject<Ticket[]>) {
        return this.http.post<Array<{
            clinicId: number,
            speciality: string,
            medicId: number,
            date: string
        }>>("/server/my-tickets", {}).pipe(retry(1))
            .subscribe(tickets => {
                const ticketArr: Ticket[] = [];
                if (tickets.length == 0)
                    buff.next([]);
                let ticketsLoaded = 0;
                for (const ticket of tickets) {
                    this.getSpecialityName(ticket.speciality)
                        .subscribe(name => {
                            ticketArr.push({
                                clinicId: ticket.clinicId,
                                date: new Date(Date.parse(ticket.date)),
                                medicId: ticket.medicId,
                                speciality: {
                                    code: ticket.speciality,
                                    name: name
                                }
                            })
                            if (++ticketsLoaded == tickets.length)
                                buff.next(ticketArr);
                        });
                }
            });
    }

    getClinicInfo(id: number) {
        return this.http.get<ClinicInfo>(
            `/server/clinic/${id}/info`);
    }
}

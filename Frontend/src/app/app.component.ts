import {Component, Inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterLink, RouterOutlet} from '@angular/router';
import {HeaderComponent} from "./components/header/header.component";
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule, HttpParams} from "@angular/common/http";

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [CommonModule, RouterOutlet,
        HeaderComponent, RouterLink,
        HttpClientModule],
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
    title = 'TicketPickIt';

    constructor(private http: HttpClient) {
    }

    ngOnInit() {
        this.http.post<any>("/server/auth/login", new HttpParams()
            .set("username", "God")
            .set("password", "root"))
            .subscribe(data => console.log(data));
    }
}

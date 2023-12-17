import {Component, Inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {HeaderComponent} from "./components/header/header.component";
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {AuthService} from "./services/AuthService";

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [CommonModule, RouterOutlet,
        HeaderComponent, RouterLink,
        HttpClientModule],
    providers: [
        HttpClientModule, AuthService
    ],
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
    title = 'TicketPickIt';

    constructor(public router: Router,
                private authService: AuthService) {
        authService.checkAuthentication();
    }

    ngOnInit() {
    }
}

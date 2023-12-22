import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {HeaderComponent} from "./components/header/header.component";
import {HttpClientModule} from "@angular/common/http";
import {AuthService} from "./services/AuthService";
import {NetService} from "./services/NetService";
import {DataService} from "./services/DataService";

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [
        CommonModule, RouterOutlet,
        HeaderComponent, RouterLink,
        HttpClientModule
    ],
    providers: [
        HttpClientModule,
        AuthService,
        NetService,
        DataService
    ],
    templateUrl: './app.component.html'
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

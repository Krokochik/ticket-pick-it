import {Component, ElementRef, Input, OnChanges, OnInit, ViewEncapsulation} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router, RouterLink, RouterOutlet} from "@angular/router";
import {AuthService} from "../../services/AuthService";
import {ComponentDef} from "../component"

@Component({
    selector: 'app-header',
    standalone: true,
    imports: [CommonModule, RouterLink, RouterOutlet],
    templateUrl: './header.component.html',
    styleUrl: './header.component.scss'
})
export class HeaderComponent extends ComponentDef implements OnInit {
    @Input() title: string = "App";
    authenticated = false;
    ready = false;

    constructor(elementRef: ElementRef,
                public router: Router,
                public authService: AuthService) {
        super(elementRef);
    }

    async ngOnInit() {
        console.log("init")
        this.authService.auth.subscribe(authenticated => {
            this.authenticated = authenticated;
            console.log("auth", authenticated)
            this.ready = true;
        })
    }
}

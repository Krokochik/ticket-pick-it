import {Component, ElementRef, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {ComponentDef} from "../component";
import {ActivatedRoute, Router, RouterOutlet} from "@angular/router";
import {AuthService} from "../../services/AuthService";
import {ClinicInfoComponent} from "../clinic-info/clinic-info.component";
import {routes} from "../../app.routes";
import {Subject} from "rxjs";

@Component({
  selector: 'app-order',
  standalone: true,
    imports: [CommonModule, RouterOutlet, ClinicInfoComponent],
  templateUrl: './order.component.html',
  styleUrl: './order.component.scss'
})
export class OrderComponent extends ComponentDef implements OnInit {
    ready: boolean[] = [];
    id: number;

    constructor(elementRef: ElementRef,
                private router: Router,
                private route: ActivatedRoute) {
        super(elementRef);
    }

    ngOnInit() {
        const id = Number.parseInt(this.route.snapshot.paramMap.get('id') || '');
        if (Number.isInteger(id)) {
            this.id = id;
            this.ready[0] = true;
        } else this.router.navigate(["/"]);
    }
}

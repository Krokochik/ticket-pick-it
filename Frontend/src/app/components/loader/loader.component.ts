import {Component, ElementRef, Input} from '@angular/core';
import { CommonModule } from '@angular/common';
import {ComponentDef} from "../component";
import {OrderComponent} from "../order/order.component";
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-loader',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loader.component.html',
  styleUrl: './loader.component.scss'
})
export class LoaderComponent extends ComponentDef {
    @Input() show = true;
    @Input() showDots = true;
    @Input() showText = true;

    constructor(elementRef: ElementRef) {
        super(elementRef);
    }
}

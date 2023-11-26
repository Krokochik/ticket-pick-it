import {Component, ElementRef, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterLink, RouterOutlet} from "@angular/router";

@Component({
    selector: 'app-header',
    standalone: true,
    imports: [CommonModule, RouterLink, RouterOutlet],
    templateUrl: './header.component.html',
    styleUrl: './header.component.scss',
    encapsulation: ViewEncapsulation.Emulated
})
export class HeaderComponent implements OnInit {
    @Input() title: string = "Header";
    @Input() authorized = false;

    [x: string]: any;
    rootClass: string;
    classMap: any;

    constructor(private elementRef: ElementRef) {}

    ngOnInit() {
        this.rootClass = this.elementRef.nativeElement.tagName.toLowerCase();
        console.log(this.rootClass)
        this.updateClassMap();
    }

    updateClassMap() {
    }
}

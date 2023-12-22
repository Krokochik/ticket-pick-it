import {ElementRef} from "@angular/core";

export class ComponentDef {
    rootClass: string;

    constructor(private elementRef: ElementRef) {
        this.rootClass = this.elementRef.nativeElement.
            tagName.toString().toLowerCase();
    }
}

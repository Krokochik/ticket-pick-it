import {Pipe, PipeTransform} from '@angular/core';
import {Clinic} from "../components/clinics/clinics.component";

@Pipe({
    name: 'filter',
    standalone: true
})
export class FilterPipe implements PipeTransform {

    transform(array: Clinic[], search: string): Clinic[] {
        if (!array) return [];
        if (!search) return array;

        search = search.toLowerCase();
        return array.filter(clinic =>
            clinic.name.toLowerCase().includes(search) ||
            clinic.address.toLowerCase().includes(search)
        );
    }

}

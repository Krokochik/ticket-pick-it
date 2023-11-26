import { Routes } from '@angular/router';
import {AppComponent} from "./app.component";

export const routes: Routes = [
    {
        // path: "main", component: ""
    },
    {
        path: "", redirectTo: "/main", pathMatch: "full"
    }
];

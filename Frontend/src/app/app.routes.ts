import {Routes} from '@angular/router';
import {AppComponent} from "./app.component";
import {HeaderComponent} from "./components/header/header.component";
import {AuthComponent} from "./components/auth/auth.component";
import {OrderComponent} from "./components/order/order.component";
import {AccountComponent} from "./components/account/account.component";
import {ClinicsComponent} from "./components/clinics/clinics.component";
import {SpecialitiesComponent} from "./components/specialities/specialities.component";
import {MedicsComponents} from "./components/medics/medics.component";

export const routes: Routes = [
    {
        path: "login", component: AuthComponent
    },
    {
        path: "order/clinic/:id", component: OrderComponent,
        children: [
            {
                path: "specialities", component: SpecialitiesComponent
            },
            {
              path: "speciality/:code/medics", component: MedicsComponents
            },
            {
                path: "", redirectTo: "clinics", pathMatch: "full"
            }
        ]
    },
    {
        path: "clinics", component: ClinicsComponent
    },
    {
        path: "account", component: AccountComponent
    },
    {
        path: "main", redirectTo: "clinics"
    },
    {
        path: "", redirectTo: "clinics", pathMatch: "full"
    }
];

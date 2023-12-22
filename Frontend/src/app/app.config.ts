import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {HTTP_INTERCEPTORS, HttpClient} from "@angular/common/http";
import {AuthInterceptor} from "./services/AuthInterceptor";
import {CalendarDateFormatter, CalendarModule, CalendarMomentDateFormatter, DateAdapter} from "angular-calendar";
import {adapterFactory} from "angular-calendar/date-adapters/date-fns";

export const appConfig: ApplicationConfig = {
    providers: [
        provideRouter(routes),
        importProvidersFrom(
            CalendarModule.forRoot(
                {
                    provide: DateAdapter,
                    useFactory: adapterFactory,
                }
            )
        )
    ]
};

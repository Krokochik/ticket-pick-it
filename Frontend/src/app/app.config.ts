import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {AuthInterceptor} from "./services/AuthInterceptor";

export const appConfig: ApplicationConfig = {
  providers: [
      provideRouter(routes)
  ]
};

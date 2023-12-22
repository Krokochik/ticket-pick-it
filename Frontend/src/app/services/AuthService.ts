import {
    HttpClient,
    HttpParams,
} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {
    BehaviorSubject,
    catchError,
    Observable, retry, Subject,
} from "rxjs";
import {log} from "@angular-devkit/build-angular/src/builders/ssr-dev-server";
import {da} from "date-fns/locale";

@Injectable({
    providedIn: "root"
})
export class AuthService {
    public auth$ = new Subject<boolean>();

    constructor(private http: HttpClient) { }
    checkAuthentication() {
        this.http.get<any>("/server/auth/info")
            .pipe(retry(1))
            .subscribe(data => {
                this.auth$.next(data?.isAuth || false);
            });
    }

    login(username: string, password: string, remember: unknown) {
        this.http
            .post("/server/auth/login", new HttpParams()
                .set("username", username)
                .set("password", password)
                .set("remember", !!remember))
            .pipe(catchError(err => {
                    const auth = err.url!.endsWith("/auth/success") || false;
                    this.auth$.next(auth);
                    return new Observable();
                })
            ).subscribe();
    }

    logout() {
        return this.http.get("/server/logout");
    }

    register(username: string, password: string) {
        return this.http
            .post("/server/auth/signup", {
                username: username,
                password: password
            });
    }

}

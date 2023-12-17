import {
    HttpClient,
    HttpParams,
} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {
    BehaviorSubject,
    catchError,
    Observable, Subject,
} from "rxjs";
import {log} from "@angular-devkit/build-angular/src/builders/ssr-dev-server";

interface AuthInfo {
    isAuth: boolean
}

@Injectable({
    providedIn: "root"
})
export class AuthService {
    public auth = new Subject<boolean>();

    constructor(private http: HttpClient) {
    }

    async checkAuthentication() {
        console.log("is1")
        this.http.get<any>("/server/auth/info")
            .subscribe(data => {
                console.log("is3")
                this.auth.next(data.isAuth || false);
            });
        console.log("is2")
    }

    login(username: string, password: string, remember: unknown) {
        this.http
            .post("/server/auth/login", new HttpParams()
                .set("username", username)
                .set("password", password)
                .set("remember", !!remember))
            .pipe(catchError(err => {
                    console.log("log");
                    this.auth.next(err.url!.endsWith("/auth/success") || false);
                    return new Observable();
                })
            ).subscribe();
    }

    register(username: string, password: string) {
        return this.http
            .post("/server/auth/signup", {
                username: username,
                password: password
            });
    }

}

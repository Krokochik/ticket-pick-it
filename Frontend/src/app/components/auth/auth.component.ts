import {Component, ElementRef, Input, OnDestroy, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ComponentDef} from "../component";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from "../../services/AuthService";
import {catchError, Observable, Subscription} from "rxjs";
import {LOG_ERROR} from "karma/lib/constants";
import {ActivatedRoute, Router} from "@angular/router";
import {log} from "@angular-devkit/build-angular/src/builders/ssr-dev-server";
import {DataService} from "../../services/DataService";

@Component({
    selector: 'app-sign-in',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './auth.component.html',
    styleUrl: './auth.component.scss'
})
export class AuthComponent extends ComponentDef implements OnInit, OnDestroy {
    ready = false;
    sub: Subscription;

    private LOG_ERR_MSG = "Wrong username or password";
    private subscribed = false;

    form = new FormGroup({
        username: new FormControl("", [
            Validators.required,
            Validators.minLength(4)
        ]),
        password: new FormControl("", [
            control => {
                const ok = new RegExp("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
                    .test(control.value);
                return ok ? null : {badPass: true};
            }
        ])
    })

    remember = true;
    showPass: boolean;
    error: string;

    constructor(elementRef: ElementRef,
                private authService: AuthService,
                private router: Router,
                private route: ActivatedRoute,
                private dataService: DataService) {
        super(elementRef);
    }

    navigateNext() {
        const origin = this.route.snapshot.queryParamMap.get("origin");
        if (origin) {
            const originData = this.dataService.messages[origin];
            if (originData) this.router.navigate([originData]);
            else this.router.navigate(["/"]);
        } else {
            this.router.navigate(["/"]);
        }
    }

    ngOnInit() {
        this.sub = this.authService.auth$.subscribe(auth => {
            console.log("auth sub")
            if (auth) {
                this.navigateNext();
            }
            this.ready = true;
        });
        this.authService.checkAuthentication();
    }

    ngOnDestroy() {
        this.sub!.unsubscribe();
    }

    private subscribe() {
        if (!this.subscribed) {
            this.subscribed = true;
            this.sub!.unsubscribe();
            this.sub = this.authService.auth$.subscribe(authenticated => {
                if (authenticated) {
                    this.error = "";
                    this.navigateNext();
                } else {
                    this.error = this.LOG_ERR_MSG;
                }
            });
        }
    }

    username() {
        return this.form.get("username") as FormControl;
    }

    password() {
        return this.form.get("password") as FormControl;
    }

    login() {
        if (this.form.valid) {
            this.subscribe();
            this.authService.login(
                this.username().value,
                this.password().value,
                this.remember
            );
        } else {
            this.error = "Please enter your username and password"
            if (this.password().errors) {
                this.password().markAsTouched();
            }
            if (this.username().errors) {
                this.username().markAsTouched();
            }
        }
    }

    register() {
        if (this.form.valid) {
            this.authService.register(this.username().value, this.password().value)
                .pipe(catchError(err => {
                    if (err.status === 201) this.login();
                    else this.error = err.error;
                    return new Observable();
                })).subscribe();
        } else {
            if (this.password().getError("badPass")) {
                this.password().markAsTouched();
                this.error = "Password must contain a letter, a number and be at least 8 symbols length"
            }

            if (this.username().getError("required")) {
                this.username().markAsTouched();
            }

            if (this.username().getError("minlength")) {
                this.error = "Min username length is 4"
            }
        }
    }
}

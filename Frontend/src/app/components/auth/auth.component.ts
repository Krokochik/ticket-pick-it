import {Component, ElementRef, Input, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ComponentDef} from "../component";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from "../../services/AuthService";
import {catchError, Observable} from "rxjs";
import {LOG_ERROR} from "karma/lib/constants";
import {Router} from "@angular/router";
import {log} from "@angular-devkit/build-angular/src/builders/ssr-dev-server";

@Component({
    selector: 'app-sign-in',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './auth.component.html',
    styleUrl: './auth.component.scss'
})
export class AuthComponent extends ComponentDef implements OnInit {
    ready = false;

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
                private router: Router) {
        super(elementRef);
    }

    ngOnInit() {
        this.authService.auth.subscribe(auth => {
            if (auth) {
                this.router.navigate(["/"]);
            }
            this.ready = true;
        });
        this.authService.checkAuthentication();
    }

    private subscribe() {
        if (!this.subscribed) {
            this.subscribed = true;
            this.authService.auth.subscribe(authenticated => {
                if (authenticated) {
                    this.error = "";
                    this.router.navigate(["/"]);
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

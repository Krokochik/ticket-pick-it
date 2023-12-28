# TicketPickIt

## Content
1. [Project Description](#project-description)
   * [Overview](#overview)
   * [Backend Features](#backend-features)
   * [Frontend Features](#frontend-features)
2. [Project Structure](#project-structure)
   * [Backend Structure](#backend-structure)
   * [Frontend Structure](#frontend-structure)
3. [Getting Started](#getting-started)
   * [Installation](#installation)
   * [Run](#run)

## Project Description
### Overview
This project comprises a Backend written in Java Spring MVC 5, leveraging technologies such as Faker, REST, and the Concurrency API. The Backend generates a list of fictional clinics with real-sounding names, addresses, and phone numbers. It also simulates clinic staff and provides the functionality for online appointment booking. Data about users, tokens, and clinics is persisted to files, with an optional feature for parallel reading and writing.

The Frontend is developed in Angular 17, offering a user interface for authentication and appointment booking in clinics, as well as management of booked appointments.

## Features
### Backend Features
* **REST API:** Implements a RESTful API to enable communication between the Frontend and Backend, facilitating user interactions and appointment management.
* **Concurrency API:** Incorporates the Concurrency API for efficient and concurrent handling of data, enhancing the system's responsiveness and scalability.
* **Data Generation:** Utilizes the Faker library to generate realistic-looking data for clinics, including names, addresses, and phone numbers.
* **Clinic Simulation:** Simulates the behavior of real clinics, including staff management and the overall infrastructure required for online appointment scheduling.
* **Data Persistance**:
  * **File Storage:** User data, tokens, and clinic information are persisted in files, ensuring data integrity and enabling data retrieval between system restarts.
  * **Parallel Read/Write:** Offers the flexibility of parallel reading and writing to optimize file I/O operations, enhancing system performance.

### Frontend Features
* **User Interface:** Developed using Angular 17, providing a modern and intuitive user interface for seamless interaction with the system.
* **Authentication:** Allows users to securely log in, ensuring the privacy and security of user information.
* **Appointment Booking:** Enables users to conveniently book appointments in clinics, fostering a user-friendly experience.
* **Appointment Management:** Empowers users to manage their booked appointments, enhancing control and convenience.

## Project Structure
### Backend Structure
```
├── src
│   └── main
│       ├── java
│       │   └── krokochik
│       │       └── backend
│       │           ├── config
│       │           │   ├── FakerConfigurer.java
│       │           │   ├── GsonConfigurer.java
│       │           │   ├── RunConfigurer.java
│       │           │   └── SecurityConfigurer.java
│       │           ├── controller
│       │           │   ├── AuthController.java
│       │           │   ├── OrderController.java
│       │           │   ├── OrderInfoController.java
│       │           │   ├── PingController.java
│       │           │   ├── ServiceInfoController.java
│       │           │   └── UserController.java
│       │           ├── model
│       │           │   ├── Clinic.java
│       │           │   ├── Employee.java
│       │           │   ├── Medic.java
│       │           │   ├── SerializableUser.java
│       │           │   ├── Specialist.java
│       │           │   ├── Speciality.java
│       │           │   ├── Ticket.java
│       │           │   └── WorkingHours.java
│       │           ├── repo
│       │           │   ├── BaseRepository.java
│       │           │   ├── BaseRepositoryImpl.java
│       │           │   ├── ClinicRepository.java
│       │           │   ├── RememberMeTokenRepository.java
│       │           │   └── UserRepository.java
│       │           ├── service
│       │           │   └── ClinicGenerator.java
│       │           ├── BackendApplication.java
│       │           └── ServletInitializer.java
│       └── resources
│           └── application.properties
├── dependency-reduced-pom.xml
└── pom.xml
```
### Frontend Structure
```
├── src
│   ├── app
│   │   ├── components
│   │   │   ├── account
│   │   │   │   ├── account.component.html
│   │   │   │   ├── account.component.scss
│   │   │   │   └── account.component.ts
│   │   │   ├── auth
│   │   │   │   ├── auth.component.html
│   │   │   │   ├── auth.component.scss
│   │   │   │   └── auth.component.ts
│   │   │   ├── booked-ticket-info-dialog
│   │   │   │   ├── booked-ticket-info-dialog.component.html
│   │   │   │   ├── booked-ticket-info-dialog.component.scss
│   │   │   │   └── booked-ticket-info-dialog.component.ts
│   │   │   ├── booking-confirmation-dialog
│   │   │   │   ├── booking-confirmation-dialog.component.html
│   │   │   │   ├── booking-confirmation-dialog.component.scss
│   │   │   │   └── booking-confirmation-dialog.component.ts
│   │   │   ├── calendar
│   │   │   │   ├── angular-calendar-override.scss
│   │   │   │   ├── calendar.component.html
│   │   │   │   ├── calendar.component.scss
│   │   │   │   └── calendar.component.ts
│   │   │   ├── clinic-info
│   │   │   │   ├── clinic-info.component.html
│   │   │   │   ├── clinic-info.component.scss
│   │   │   │   └── clinic-info.component.ts
│   │   │   ├── clinics
│   │   │   │   ├── clinics.component.html
│   │   │   │   ├── clinics.component.scss
│   │   │   │   └── clinics.component.ts
│   │   │   ├── header
│   │   │   │   ├── header.component.html
│   │   │   │   ├── header.component.scss
│   │   │   │   └── header.component.ts
│   │   │   ├── loader
│   │   │   │   ├── loader.component.html
│   │   │   │   ├── loader.component.scss
│   │   │   │   └── loader.component.ts
│   │   │   ├── medics
│   │   │   │   ├── medics.component.html
│   │   │   │   ├── medics.component.scss
│   │   │   │   └── medics.component.ts
│   │   │   ├── order
│   │   │   │   ├── order.component.html
│   │   │   │   ├── order.component.scss
│   │   │   │   └── order.component.ts
│   │   │   ├── specialities
│   │   │   │   ├── specialities.component.html
│   │   │   │   ├── specialities.component.scss
│   │   │   │   └── specialities.component.ts
│   │   │   ├── time-select
│   │   │   │   ├── time-select.component.html
│   │   │   │   ├── time-select.component.scss
│   │   │   │   └── time-select.component.ts
│   │   │   └── component.ts
│   │   ├── pipes
│   │   │   └── filter.pipe.ts
│   │   ├── services
│   │   │   ├── AuthInterceptor.ts
│   │   │   ├── AuthService.ts
│   │   │   ├── DataService.ts
│   │   │   └── NetService.ts
│   │   ├── app.component.html
│   │   ├── app.component.ts
│   │   ├── app.config.ts
│   │   └── app.routes.ts
│   ├── assets
│   │   └── images
│   │       ├── left-arrow.svg
│   │       ├── right-arrow.svg
│   │       └── warning.svg
│   ├── favicon.ico
│   ├── imports.sass
│   ├── index.html
│   ├── main.ts
│   ├── reset.scss
│   ├── shortcuts.sass
│   ├── styles.scss
│   └── variables.scss
├── angular.json
├── package-lock.json
├── package.json
├── proxy.conf.json
├── tsconfig.app.json
├── tsconfig.json
└── tsconfig.spec.json
```

## Getting started
### Installation

#### Backend
* [Install](https://www.oracle.com/java/technologies/downloads/) JDK 17 or higher
* Download the jarfile from a release [manually](https://github.com/Krokochik/ticket-pick-it/releases/latest/) or with a command further 

#### Frontend 
* [Install](https://nodejs.org/en/download) Node.js
* [Install](https://www.npmjs.com/package/npm) npm
* For Windows:
   * Download the exe from a release [manually](https://github.com/Krokochik/ticket-pick-it/releases/latest/) or with a command further
* For others:
   * Download the archive from a release [manually](https://github.com/Krokochik/ticket-pick-it/releases/latest/) or with a command further
   * Unpack it, e.g. `unzip -q -d ./Frontend Frontend.zip`

Downloading from cmd temtplates:
```
wget https://github.com/Krokochik/ticket-pick-it/releases/latest/download/Backend.jar
```
```
axel https://github.com/Krokochik/ticket-pick-it/releases/latest/download/Frontend.zip
```
```
curl -LJO https://github.com/Krokochik/ticket-pick-it/releases/latest/download/Frontend.exe
```


### Run
#### Backend
Run jarfile with java. The app applies the next options:

| Option           | Type      | Applies                                       | Function                                                                               | Example                           |
|------------------|:---------:|:---------------------------------------------:|----------------------------------------------------------------------------------------|-----------------------------------|
| -Dserver.port    | parameter | Numeric port (default 8080)                   | Changes app's port; *Specify immediately after "-jar"                                  | -Dserver.port=1234                |
| --renew          | parameter | One (or more) of: clinics, users, remember-me | Parametrized -renew                                                                    | --renew=clinics&users             |
| --storage        | parameter | String path (default %temp%)                  | Specifies data storage path                                                            | --storage="C:\Users"              |
| --clinics-amount | parameter | Number                                        | Specifies clinic generation amount                                                     | --clinics-amount=150              |
| -async           | flag      | -                                             | Enables async mode, that provides up to 3 times faster work, but uses more CPU and RAM | -async                            |
| --thread-count   | parameter | Number                                        | Specifies the number of threads available for use by the app                           | --thread-count=16                 |
| -cors            | flag      | -                                             | Enables CORS                                                                           | -cors                             |
| -csrf            | flag      | -                                             | Enables CSRF                                                                           | -csrf                             |
| -renew           | flag      | -                                             | Forces the app to ignore stored data                                                   | -renew                            |

Run example:
```
java -jar -Dserver.port=8080 \path\to\Backend.jar --storage="C:\Users\User\Desktop" -async
```
*Enormous amount of clinics (e.g. 1500; exactly depends on your RAM) may be written only at synchronous mode.

#### Frontend

*If you specified backend port, also change it at proxy.conf.json

##### Windows
Run installed exe, e.g. `./frontend.exe`, next await for unpacking and opening server in terminal.

##### Others
Go into unpacked archive directory and run app:
```
npm start
```
or 
```
ng serve
```

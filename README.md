# TicketPickIt
A two-part project for ordering tickets at clinics, which is made up of the back- and front- ends.

## Content
1. [Technologies](#technologies)
2. [Installation](#installation)
3. [Run](#run)

## Technologies
- Backend: Java, Spring MVC 5
- Frontend: TypeScript, Angular 17

## Installation
### Backend
- Install jdk 17^.
- Install jar file from the latest release:
```
wget https://github.com/Krokochik/ticket-pick-it/releases/download/v1.0.0/Backend.jar
```
or
```
curl -LJO https://github.com/Krokochik/ticket-pick-it/releases/download/v1.0.0/Backend.jar
```
### Frontend
#### Windows
Install exe:
```
wget https://github.com/Krokochik/ticket-pick-it/releases/download/v1.0.0/Frontend.exe
```
or 
```
curl -LJO https://github.com/Krokochik/ticket-pick-it/releases/download/v1.0.0/Frontend.exe
```
#### Other
Install zip:
```
wget https://github.com/Krokochik/ticket-pick-it/releases/download/v1.0.0/Frontend.zip
```
or 
```
curl -LJO https://github.com/Krokochik/ticket-pick-it/releases/download/v1.0.0/Frontend.zip
```
Unpack it e.g.
```
unzip -q -d ./frontend frontend.zip
```

## Run
### Backend
Run jarfile with java. The app applies the next options:

| Option           | Type      | Applies                                       | Function                                                                               | Example                           |
|------------------|:---------:|:---------------------------------------------:|----------------------------------------------------------------------------------------|-----------------------------------|
| -Dserver.port    | parameter | Numeric port (default 8080)                   | Changes app's port; *Specify immediately after "-jar"                   | -Dserver.port=1234                |
| --renew          | parameter | One (or more) of: clinics, users, remember-me | Parametrized -renew                                                                    | --renew=clinics&users             |
| --storage        | parameter | String path (default %temp%)                  | Specifies data storage path                                                            | --storage="C:\Users"              |
| --clinics-amount | parameter | Number                                        | Specifies clinic generation amount                                                     | --clinics-amount=150              |
| -async           | flag      | -                                             | Enables async mode, that provides up to 3 times faster work, but uses more CPU and RAM | -async                            |
| -cors            | flag      | -                                             | Enables CORS                                                                           | -cors                             |
| -csrf            | flag      | -                                             | Enables CSRF                                                                           | -csrf                             |
| -renew           | flag      | -                                             | Forces the app to ignore stored data                                                   | -renew                            |

Run example:
```
java -jar -Dserver.port=8080 \path\to\Backend.jar --storage="C:\Users\User\Desktop" -async
```

### Frontend

If necessary, specify backend server port at \path\to\proxy.conf.json

#### Windows
Run installed exe, e.g. `./frontend.exe`, next await for unpacking and opening server in terminal.

#### Other
Go into unpacked archive directory and run app:
```
npm start
```
or 
```
ng serve
```

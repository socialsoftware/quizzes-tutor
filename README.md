# Tutor System - Impress project
<table>
  <tr>
    <td><img src="https://math.tecnico.ulisboa.pt/img/Tecnico_logo.svg" alt="IST Logo"/></td>
    <td><img src="https://www.inesc-id.pt/wp-content/uploads/2018/01/impress_logo_703x316.png" alt="IMPRESS Logo"/></td>
    <td><img src="http://gaips.inesc-id.pt/sapient/wp-content/uploads/2014/11/logo_inesc.png" alt="INESC Logo"/></td>
  </tr>
</table>

## Instalation

### Install Postgresql for database(version 10.8)
### Install Java 11 for spring-boot API
### Install Node.js for frontend (version 10.15.3)

```
sudo apt update
sudo apt install postgresql
sudo apt install nodejs npm
```

## Create DB by loading dump

Change to postgres user and create DB
```
sudo su -l postgres
dropdb tutordb
createdb tutordb
```

Create user to access db and load dump
```
psql tutordb
CREATE USER your-username WITH SUPERUSER LOGIN PASSWORD 'yourpassword';
psql tutordb < postgres/dump.bak
```

## Run spring-boot API on http://localhost:8080

Firstly, replace .example property files located in spring-api/src/main/resources

```
cd spring-api
mvn clean spring-boot:run
```

See documentation on http://localhost:8080/swagger-ui.html

## Run frontend on http://localhost:8081

Firstly, create .env and .env.production files located in frontend/

```
cd frontend
npm i
npm start
```

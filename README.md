# Tutor System

## Prerequisites

Install postgresql

```
sudo apt install postgresql
```

Install psycopg2 module

```
sudo apt install python3-pip
pip3 install psycopg2-binary
```

Install npm

```
sudo apt install npm
```

## Create DB and extract questions data


Create DB
```
sudo su -l postgres
createdb tutorDB
```

Create user
```
psql tutorDB
CREATE USER pedro WITH SUPERUSER LOGIN PASSWORD 'foobar123';
```

Create tables
```
psql tutorDB -f CreateTables.sql
```
 
Populate tables
```
python3 populateDB.py
```

## Run node api server
use node version 9.11.1
Change config/db.json and run
```
npm i
npm start
```

Running on http://localhost:3000

## Run spring api server
Change as-tutor/spring-api/src/main/resources/application.properties

```
mvn clean spring-boot:run
```
Running on http://localhost:8080
See documentation on http://localhost:8080/swagger-ui.html

## Run frontend

use node version 10.15.3
vue version 3.5.1

```
npm i
npm start
```

Running on http://localhost:8081
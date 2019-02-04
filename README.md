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
createdb seGamification
```

Create user
```
psql seGamification
CREATE USER pedro WITH SUPERUSER LOGIN PASSWORD 'foobar123';
```

Create tables
```
psql seGamification -f CreateTables.sql
```
 
Populate tables
```
python3 populateDB.py
```

## Run tests server

Change config/db.json and run
```
npm i
npm start
```

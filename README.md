<h1 align="center">
  Quizzes Tutor
  <br>
  <img src="https://quizzes-tutor.tecnico.ulisboa.pt/logo_optimized.png" alt="Quizzes Tutor" width="200">
  <br>
</h1>

<h4 align="center">A multiple choice Quiz system for students with quiz management tools for teachers</h4>

<p align="center">
  <img src="https://img.shields.io/github/workflow/status/socialsoftware/as-tutor/build" alt="Build">
  <img src="https://img.shields.io/website?url=https%3A%2F%2Fquizzes-tutor.tecnico.ulisboa.pt" alt="Website">
  <img src="https://snyk.io/test/github/socialsoftware/as-tutor/badge.svg?targetFile=spring-api/pom.xml" alt="Backend vulnerabilities">
  <img src="https://snyk.io/test/github/socialsoftware/as-tutor/badge.svg?targetFile=frontend/package.json" alt="Frontend vulnerabilities">
  <img src="https://codecov.io/gh/socialsoftware/as-tutor/branch/master/graph/badge.svg" alt="Code Coverage">
  <img src="https://img.shields.io/github/license/socialsoftware/as-tutor"alt="License">
</p>

<p align="center">
  <a href="#about">About</a> •
  <a href="#technologies">Technologies</a> •
  <a href="#installation">Installation</a> •
  <a href="#patreons">Patreons</a> •
  <a href="#contributing">Contributing</a> •
  <a href="#license">License</a>
</p>

![screenshot](https://impress-project.eu/wp-content/uploads/2019/11/0-1024x518.jpeg)

# About

# Technologies

* Require download
  * [Postgres 10 or 12](https://www.postgresql.org/)
  * [Java 11](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
  * [Maven](https://maven.apache.org/download.cgi)
  * [Node 12.14](https://nodejs.org/en/)
  * [Docker](https://www.docker.com/)
* No download required
  * [Spring-boot](https://spring.io/)
  * [Vue.js](https://vuejs.org/)

# Installation

* **Install**
```
sudo apt update
sudo apt install postgresql nodejs npm
```
* **Change to postgres user and create DB**
```
sudo su -l postgres
dropdb tutordb
createdb tutordb
```
* **Create user to access db and load dump**
```
psql tutordb
CREATE USER your-username WITH SUPERUSER LOGIN PASSWORD 'yourpassword';
psql tutordb < dump.sql
```
* **Rename `spring-api/src/main/resources/application-dev.properties.example` to `application-dev.properties`**
* **Run server**
```
cd spring-api
mvn clean spring-boot:run
```
* **See documentation on http://localhost:8080/swagger-ui.html**
* **Create .env file in `frontend`**
* **Run frontend**
```
cd frontend
npm i
npm start
```
* **Access http://localhost:8081**

# Patreons

A special thanks to our patreons for supporting this project:

<table>
  <tr>
    <td><img src="https://math.tecnico.ulisboa.pt/img/Tecnico_logo.svg" alt="IST Logo"/></td>
    <td><img src="https://www.inesc-id.pt/wp-content/uploads/2018/01/impress_logo_703x316.png" alt="IMPRESS Logo"/></td>
    <td><img src="http://gaips.inesc-id.pt/sapient/wp-content/uploads/2014/11/logo_inesc.png" alt="INESC Logo"/></td>
  </tr>
</table>

# Contributing

Your contributions are always welcome! Please have a look at the [contribution guidelines](https://github.com/socialsoftware/as-tutor/wiki/Guidelines) first.

# License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

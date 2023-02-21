<h1 align="center">Quizzes Tutor</h1>

<p align="center">
  <a href="https://github.com/socialsoftware/quizzes-tutor/actions">
    <img src="https://img.shields.io/github/workflow/status/socialsoftware/quizzes-tutor/build" alt="Build">
  </a>
  <a href="https://quizzes-tutor.tecnico.ulisboa.pt/">
    <img src="https://img.shields.io/website?url=https%3A%2F%2Fquizzes-tutor.tecnico.ulisboa.pt" alt="Website">
  </a>
  <!-- Uncomment when sslbadge updates its ssl certificates lol -->
  <!--a href="https://www.ssllabs.com/ssltest/analyze.html?d=quizzes-tutor.tecnico.ulisboa.pt">
    <img src="https://sslbadge.org/?domain=quizzes-tutor.tecnico.ulisboa.pt" alt="SSL configuration">
  </a-->
  <a href="https://github.com/socialsoftware/quizzes-tutor/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/socialsoftware/quizzes-tutor"alt="License">
  </a>
</p>

<p align="center">
  <!-- Snyk badge takes too long to load -->
  <!-- a href="https://snyk.io/test/github/socialsoftware/quizzes-tutor?targetFile=backend/pom.xml">
    <img src="https://snyk.io/test/github/socialsoftware/quizzes-tutor/badge.svg?targetFile=backend/pom.xml" alt="Backend vulnerabilities">
  </a>
  <a href="https://snyk.io/test/github/socialsoftware/quizzes-tutor?targetFile=frontend/package.json">
    <img src="https://snyk.io/test/github/socialsoftware/quizzes-tutor/badge.svg?targetFile=frontend/package.json" alt="Frontend vulnerabilities">
  </a-->

  <a href="https://github.com/prettier/prettier">
    <img src="https://img.shields.io/badge/code_style-prettier-ff69b4.svg?" alt="code style: prettier">
  </a>
  <a href="https://codecov.io/gh/socialsoftware/quizzes-tutor/branch/master">
    <img src="https://codecov.io/gh/socialsoftware/quizzes-tutor/branch/master/graph/badge.svg" alt="Code Coverage">
  </a>
</p>

<p align="center">
  <a href="https://quizzes-tecnico.slack.com">
    <img src="https://upload.wikimedia.org/wikipedia/commons/d/d5/Slack_icon_2019.svg" width="40"> Slack Discussion Group
  </a>
</p>

<p align="center">
  <a href="https://quizzes-tutor.tecnico.ulisboa.pt/">
    <img src="https://github.com/socialsoftware/quizzes-tutor/blob/master/frontend/public/logo_optimized.png" width="40"> Quizzes Tutor Service
  </a>
</p>

<p align="left"><i><b>Vue.js</b> web application and <b>Spring-boot</b> API</i></p>
<p align="center"><i>with <b>multiple choice questions</b> for students</i></p>
<p align="right"><i>and <b>quiz management</b> for teachers</i></p>

<p align="center">
  <a href="#about">About</a> •
  <a href="#technologies">Technologies</a> •
  <a href="#installation">Installation</a> •
  <a href="#patreons">Patreons</a> •
  <a href="#contributing">Contributing</a> •
  <a href="#license">License</a>
</p>

# About

<img align="right" src="./frontend/public/Screenshot.png" height="250">

<br/>

**Quizzes Tutor** allows teachers to create and reuse, multiple-choice questions with images and topics which can be inserted in assessments and quizzes. Its development started as an effort of project IMPRESS to share and reuse questions and quizzes of software engineering. 

<br/>
 
It is currently integrated with IST authentication such that it can be used for any course.

<br/>

Students can then answer those questions in sugested quizzes or generated quizzes (pseudo-random) providing them with a useful **self-assessment tool** to improve their learning.

 <br/>

# Technologies

* Require download
  * [Postgres >= 14](https://www.postgresql.org/)
  * [Java 11](https://openjdk.org/projects/jdk/17/)
  * [Maven](https://maven.apache.org/download.cgi)
  * [Node 16.14](https://nodejs.org/en/) ([Node Version Manager](https://github.com/nvm-sh/nvm) recommended)
  * [Docker](https://www.docker.com/)
* No download required
  * [Spring-boot](https://spring.io/)
  * [Vue.js](https://vuejs.org/)

# Installation

* **Install**
```
sudo apt update && sudo apt upgrade
sudo apt install openjdk-17-jdk postgresql
```
* **Start db, change to postgres user and create DB**
```
sudo service postgresql start
sudo su -l postgres
dropdb tutordb
createdb tutordb
```
* **Create user to access db**
```
psql tutordb
CREATE USER your-username WITH SUPERUSER LOGIN PASSWORD 'yourpassword';
\q
exit
```
* **Go to [API Service Agreement](https://fenix.tecnico.ulisboa.pt/personal/external-applications/api-service-agreement) and then to [Applications](https://fenix.tecnico.ulisboa.pt/personal/external-applications/#/applications)  and create an application that redirects to http://localhost:8081/login and accessess curricular and information**
* **Rename `backend/src/main/resources/application-dev.properties.example` to `application-dev.properties` and fill its fields**
* **Run server**
```
cd backend
mvn clean spring-boot:run
```
* **See documentation on http://localhost:8080/swagger-ui.html**
* **Rename `frontend/example.env` to `.env` and fill its fields**
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
    <td width="30%">
      <a href="https://tecnico.ulisboa.pt/pt/" target="_blank">
        <img width="100%" src="https://math.tecnico.ulisboa.pt/img/Tecnico_logo.svg" alt="Técnico Logo"/>
      </a>
    </td>
    <td width="30%">
      <a href="https://impress-project.eu/" target="_blank">
        <img width="100%" src="https://www.inesc-id.pt/wp-content/uploads/2018/01/impress_logo_703x316.png" alt="IMPRESS Logo"/>
      </a>
    </td>
    <td width="30%">
      <a href="https://www.inesc-id.pt" target="_blank">
      <img width="100%" src="https://www.inesc-id.pt/wp-content/uploads/2019/06/INESC-ID-logo_01.png" alt="INESC Logo"/>
    </a>
  </td>
  </tr>
</table>

# Contributing

Your contributions are always welcome! Please have a look at the [contribution guidelines](https://github.com/socialsoftware/quizzes-tutor/wiki/Guidelines) first.

# License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/socialsoftware/quizzes-tutor/blob/master/LICENSE) file for details.

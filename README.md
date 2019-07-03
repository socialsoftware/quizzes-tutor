# Tutor System - Impress project

![](http://gaips.inesc-id.pt/sapient/wp-content/uploads/2014/11/logo_inesc.png)

## Conteúdo

1. [Sobre o Tutor System]
2. [Comunicação](#comunicação)
3. [Roadmap de tecnologia](#roadmap-de-tecnologia)
4. [Como contribuir](#como-contribuir)
5. [Instalação](#instalação)
6. [FAQ](#perguntas-frequentes-(FAQ))

## Sobre o Tutor System

## Comunicação

Acreditamos que o sucesso do projeto depende diretamente da interação clara e
objetiva entre os membros. Por isso, estamos definindo algumas
políticas para que estas interações nos ajudem a crescer juntos! Você pode
consultar algumas destas boas práticas em nosso código de conduta 

Além disso, gostamos de meios de comunicação assíncrona, onde não há necessidade de
respostas em tempo real. Isso facilita a produtividade individual dos
colaboradores do projeto.

| Canal de comunicação | Objetivos |
|----------------------|-----------|
| [Issues do Github](https://github.com/socialsoftware/as-tutor/issues) | - Sugestão de novas funcionalidades<br> - Reportar bugs<br> - Discussões técnicas |
| [Telegram](https:// ) | - Comunicar novidades sobre o projeto<br> - Movimentar a comunidade<br>  - Falar tópicos que **não** demandem discussões profundas |


## Roadmap de tecnologia

### Passos iniciais

- Adoção do [PSR1](https://)
- Adoção do [PSR2](https://)
- Adoção do [PSR4](https://)

### Planeamento Técnico

Em nossa wiki você encontra um planejamento mais técnico de como devemos
prosseguir com as melhorias e evoluções do nosso projeto.
[Clique aqui](https://github.com/)
para ler mais a respeito.

## Como contribuir

Contribuições são **super bem vindas**! Se você tem vontade de construir o
Tutor System junto conosco, veja o nosso guia de contribuição
onde explicamos detalhadamente como trabalhamos e de que formas você pode nos
ajudar a alcançar nossos objetivos.

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
psql tutordb < dump/tutordb.bak
```

## Run spring-boot API on http://localhost:8080

Firstly, change as-tutor/spring-api/src/main/resources/application.properties

```
cd spring-api
mvn clean spring-boot:run
```

See documentation on http://localhost:8080/swagger-ui.html

## Run frontend on http://localhost:8081

```
cd frontend
npm i
npm start
```
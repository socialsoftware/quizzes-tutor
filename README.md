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

### Planejamento Técnico

Em nossa wiki você encontra um planejamento mais técnico de como devemos
prosseguir com as melhorias e evoluções do nosso projeto.
[Clique aqui](https://github.com/)
para ler mais a respeito.

## Como contribuir

Contribuições são **super bem vindas**! Se você tem vontade de construir o
Tutor System junto conosco, veja o nosso guia de contribuição
onde explicamos detalhadamente como trabalhamos e de que formas você pode nos
ajudar a alcançar nossos objetivos.

## Instalação

> ATENÇÃO: Essa forma de instação tem o objetivo de facilitar demonstrações e desenvolvimento. Não é recomendado para ambientes de produção!

Antes de começar você vai precisar instalar:

- [Postgresql](https://www.postgresql.org/) (> versão)

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


Change to postgresuser and create DB
```
sudo su -l postgres
createdb tutordb
```

Create user to access db
```
psql tutordb
CREATE USER pedro WITH SUPERUSER LOGIN PASSWORD 'foobar123';
```

Create tables
```
psql tutordb -f CreateTables.sql
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

To run api spring it is important to be with version 11 of java

Change as-tutor/spring-api/src/main/resources/application.properties

```
mvn clean spring-boot:run
```
Running on http://localhost:8080

See documentation on http://localhost:8080/swagger-ui.html

## Run frontend

to run the frontend it is important to be with version 10 of the node

use node version 10.15.3
vue version 3.5.1

```
npm i
npm start
```

Running on http://localhost:8081

Você também vai precisar do [Git](https://git-scm.com/downloads) caso ainda não
o tenha instalado.

### Inicializando o banco de dados

**Atenção:**

## Perguntas frequentes (FAQ)

Algumas perguntas aparecem recorrentemente. Olhe primeiro por aqui: FAQ

---

Powered by 


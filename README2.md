# Run data extraction

## Instalation

Install python3, pip and psycopg2

```
sudo apt install python3-pip
pip3 install psycopg2-binary
```

Install Java 8 and PDI

```
download from https://sourceforge.net/projects/pentaho/files/Data%20Integration/7.1/pdi-ce-7.1.0.0-12.zip/download
```


## Create database 

Change to postgresuser and create DB
```
sudo su -l postgres
dropdb tutordb
createdb tutordb
```

Create user to access db
```
psql tutordb
CREATE USER your-username WITH SUPERUSER LOGIN PASSWORD 'yourpassword';
```


```
dropdb tutordb
createdb tutordb
psql tutordb -f sql/CreateTables.sql
```

## Extract Data

```
python3 data-extraction/populateDB.py
pg_dump tutordb > dump/0_original.bak
python3 dump/removeLateX.py
dropdb tutordb
createdb tutordb
psql tutordb < dump/1_withoutLateX.bak
```

##Clean data with PDI

Open .ktr files.
Change source files path and database configurations
run pentaho/job.ktr

pg_dump tutordb > dump/2_withAnswers.bak


### Useful queries:

view unique questions
```
select count(*) from questions where new_id is null;
```

view if all questions have 4 options
```
select * from (select count(*), question_id from options group by question_id) as a where count <> 4;
```

copy to file list of quizzes
```
\copy (select title, count from quizzes, (select quiz_id, count(question_id) from quiz_has_question group by quiz_id) as q where id = quiz_id) To 'test.csv' With CSV
```

view most repeated questions
```
select count, content from questions, (select question_id, count(*) as count from quiz_has_question group by question_id having count(*)>5) as t where id = question_id;
```

view most unanswered questions
```
select * from (select question_id, count(*) as c from answers where option is null group by question_id) as a where c > 40;
```
add column
```
alter table students add column username varchar(255);
```

rename column
```
ALTER TABLE students RENAME COLUMN type TO role;
```

set value
```
update students set role = 'student' where true;
```

delete record
```
DELETE FROM table WHERE condition;
```

Order students by percentage of correct questions
```
select u.id, u.year, SUM(CASE WHEN o.correct = true THEN 1 ELSE 0 END) * 100 / count(*) as percentage, count(*) as total, SUM(CASE WHEN o.correct = true THEN 1 ELSE 0 END) as corrects from users as u, answers as a, options as o where o.option = a.option and a.question_id = o.question_id and u.id = a.user_id group by u.id order by percentage DESC;
```
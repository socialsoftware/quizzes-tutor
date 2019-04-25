## Run PDI

```
download from https://sourceforge.net/projects/pentaho/files/Data%20Integration/7.1/pdi-ce-7.1.0.0-12.zip/download
```

Open .ktr files.

Change source files path and database configuration

Run

##Useful queries:

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
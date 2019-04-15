# Tutor System

## Create dump

```
pg_dump tutordb > tutordb.bak
```


## Load Dump

```
dropdb tutordb
createdb tutordb
psql tutordb < tutordb.bak
```
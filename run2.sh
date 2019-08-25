python3 data-extraction/populateDB.py
pg_dump tutordb > postgres/dumps/0_original.bak
python3 postgres/removeLateX.py
dropdb tutordb
createdb tutordb
psql tutordb < postgres/dumps/1_withoutLateX.bak
export JAVA_HOME=/usr/lib/jvm/java-8-oracle
~/pentaho/kitchen.sh  -file=pentaho/Job.kjb
pg_dump tutordb > postgres/dumps/dump.sql
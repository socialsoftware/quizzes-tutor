python3 data-extraction/populateDB.py
pg_dump tutordb > dump/0_original.bak
python3 dump/removeLateX.py
dropdb tutordb
createdb tutordb
psql tutordb < dump/1_withoutLateX.bak
export JAVA_HOME=/usr/lib/jvm/java-8-oracle
~/pentaho/kitchen.sh  -file=pentaho/Job.kjb
pg_dump tutordb > dump/2_withAnswers.bak
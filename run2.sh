python3 data-extraction/populateDB.py
pg_dump tutordb > postgres/dumps/dump0.sql
export JAVA_HOME=/usr/lib/jvm/java-8-oracle
~/pentaho/kitchen.sh  -file=pentaho/Job.kjb
~/pentaho/kitchen.sh  -file=pentaho/Job2.kjb
~/pentaho/kitchen.sh  -file=pentaho/Job3.kjb
pg_dump tutordb > postgres/dumps/dump.sql
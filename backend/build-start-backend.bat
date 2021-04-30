:: Cleans project and installs dependencies
call mvn clean install -DskipTests=true

:: Starts microservices
call start-services.bat
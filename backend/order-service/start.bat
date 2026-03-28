@echo off
echo Starting Order Service...
cd /d %~dp0
mvn clean spring-boot:run -DskipTests
pause

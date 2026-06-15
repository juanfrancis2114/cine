@echo off
REM Compila y ejecuta el sistema (Windows).
set DRIVER=lib\postgresql-42.7.2.jar
echo Compilando...
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
del sources.txt
echo Ejecutando...
java -cp "out;%DRIVER%" cine.main.Main

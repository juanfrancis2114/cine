#!/usr/bin/env bash
# Compila y ejecuta el sistema (Linux/macOS).
set -e
DRIVER="lib/postgresql-42.7.2.jar"
echo "Compilando..."
mkdir -p out
javac -d out $(find src -name "*.java")
echo "Ejecutando..."
java -cp "out:$DRIVER" cine.main.Main

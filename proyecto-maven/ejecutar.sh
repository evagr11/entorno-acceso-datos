#!/usr/bin/env bash
# Compila el proyecto (la primera vez descarga las librerías) y ejecuta una clase.
# Uso:  ./ejecutar.sh <Clase> [argumentos]
#   ./ejecutar.sh Principal          -> ejemplo del libro (informe de departamentos)
#   ./ejecutar.sh Actividad2_13      -> resumen de departamentos apaisado
#   ./ejecutar.sh Ejercicio10        -> ventana Access + informes
#   ./ejecutar.sh Ejercicio11        -> XML con JAXB
#   ./ejecutar.sh Ejercicio13        -> XML con JAXB a partir del XSD
cd "$(dirname "$0")" || exit 1
[ -z "${1:-}" ] && { sed -n '3,9p' "$0"; exit 1; }
CLASE="$1"; shift
export DISPLAY="${DISPLAY:-:1}"
mvn -q compile exec:exec -Dexec.executable=java -Dexec.args="-cp %classpath $CLASE $*"

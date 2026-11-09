#!/usr/bin/env bash
# Borra y vuelve a crear las bases de datos 'ejemplo' con los datos iniciales
read -r -p "Esto borra las BD 'ejemplo' de SQLite, Derby, HSQLDB y H2. ¿Seguro? (s/N) " r
[ "$r" = "s" ] || exit 0
rm -rf "$HOME/DB/SQLITE/ejemplo.db" "$HOME/DB/DERBY/ejemplo" "$HOME/DB/HSQLDB/ejemplo."* "$HOME/DB/H2/ejemplo"
bash "$(dirname "$0")/../.devcontainer/setup.sh"

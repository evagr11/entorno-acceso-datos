#!/usr/bin/env bash
# Comprueba que todas las bases de datos 'ejemplo' responden por JDBC
cd "$(dirname "$0")" || exit 1
java ListarDepartamentos.java "jdbc:sqlite:$HOME/DB/SQLITE/ejemplo.db"
java ListarDepartamentos.java "jdbc:derby:$HOME/DB/DERBY/ejemplo"
java ListarDepartamentos.java "jdbc:hsqldb:file:$HOME/DB/HSQLDB/ejemplo" SA ""
java ListarDepartamentos.java "jdbc:h2:$HOME/DB/H2/ejemplo/ejemplo" sa ""
java ListarDepartamentos.java "jdbc:mysql://localhost/ejemplo" ejemplo ejemplo
rm -f derby.log

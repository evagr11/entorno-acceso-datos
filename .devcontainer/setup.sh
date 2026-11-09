#!/usr/bin/env bash
# Se ejecuta automáticamente al crear el Codespace.
# Prepara: conectores JDBC, variables de entorno, bases de datos 'ejemplo'
# (SQLite, Derby, HSQLDB, H2), ODBC y lanzadores. Se puede repetir sin problema.
set -u

REPO="$(cd "$(dirname "$0")/.." && pwd)"
DB="$REPO/DB"
JARS="$DB/jars"
SCRIPTS="$REPO/scripts"
MAVEN="https://repo1.maven.org/maven2"

ok()   { echo -e "\e[32m[OK]\e[0m $*"; }
info() { echo -e "\n\e[36m==> $*\e[0m"; }
warn() { echo -e "\e[33m[AVISO]\e[0m $*"; }

info "Carpetas"
mkdir -p "$DB"/{SQLITE,DERBY,HSQLDB,H2,DB4O} "$JARS"
ln -sfn "$DB" "$HOME/DB"          # ~/DB igual que en el libro (/home/usuario/DB)
ok "$DB  (también accesible como ~/DB)"

info "Conectores JDBC en DB/jars"
bajar() {
  if [ -s "$JARS/$2" ]; then ok "$2 (ya estaba)"; return; fi
  if wget -q -O "$JARS/$2" "$MAVEN/$1"; then ok "$2"; else rm -f "$JARS/$2"; warn "No se pudo descargar $2"; fi
}
bajar org/apache/derby/derby/10.16.1.1/derby-10.16.1.1.jar                derby.jar
bajar org/apache/derby/derbytools/10.16.1.1/derbytools-10.16.1.1.jar      derbytools.jar
bajar org/apache/derby/derbyshared/10.16.1.1/derbyshared-10.16.1.1.jar    derbyshared.jar
bajar org/hsqldb/hsqldb/2.7.4/hsqldb-2.7.4.jar                            hsqldb.jar
bajar com/h2database/h2/2.3.232/h2-2.3.232.jar                            h2.jar
bajar org/xerial/sqlite-jdbc/3.46.1.3/sqlite-jdbc-3.46.1.3.jar            sqlite-jdbc.jar
bajar org/slf4j/slf4j-api/2.0.16/slf4j-api-2.0.16.jar                     slf4j-api.jar
bajar org/slf4j/slf4j-nop/2.0.16/slf4j-nop-2.0.16.jar                     slf4j-nop.jar
bajar com/mysql/mysql-connector-j/9.1.0/mysql-connector-j-9.1.0.jar       mysql-connector-j.jar
ls "$JARS"/db4o*.jar >/dev/null 2>&1 && ok "db4o encontrado" \
  || warn "Falta el jar de Db4o: súbelo a DB/jars (arrástralo al explorador de archivos)"

info "Variables de entorno (CLASSPATH, PATH)"
sed -i '/# >>> tema2-acceso-datos >>>/,/# <<< tema2-acceso-datos <<</d' "$HOME/.bashrc"
cat >> "$HOME/.bashrc" <<EOF
# >>> tema2-acceso-datos >>>
export DB_JARS="$JARS"
export DERBY_HOME="$JARS"
export PATH="$SCRIPTS:\$PATH"
CLASSPATH="."
for j in "\$DB_JARS"/*.jar; do [ -e "\$j" ] && CLASSPATH="\$CLASSPATH:\$j"; done
export CLASSPATH
# <<< tema2-acceso-datos <<<
EOF
CLASSPATH="."; for j in "$JARS"/*.jar; do [ -e "$j" ] && CLASSPATH="$CLASSPATH:$j"; done; export CLASSPATH
chmod +x "$SCRIPTS"/*.sh "$SCRIPTS"/ij "$SCRIPTS"/hsqldb-manager "$SCRIPTS"/h2-console
ok "Listo"

info "Bases de datos 'ejemplo' (solo si no existen, para no borrar tu trabajo)"
cd "$SCRIPTS" || exit 1
[ -f "$DB/SQLITE/ejemplo.db" ] || sqlite3 "$DB/SQLITE/ejemplo.db" ".read $SCRIPTS/ejemplo-sqlite.sql"
ok "SQLite -> DB/SQLITE/ejemplo.db"
[ -d "$DB/DERBY/ejemplo" ] || java CargarSQL.java "jdbc:derby:$DB/DERBY/ejemplo;create=true" "" "" ejemplo-java-dbs.sql
ok "Derby  -> DB/DERBY/ejemplo"
[ -f "$DB/HSQLDB/ejemplo.script" ] || java CargarSQL.java "jdbc:hsqldb:file:$DB/HSQLDB/ejemplo" SA "" ejemplo-java-dbs.sql
ok "HSQLDB -> DB/HSQLDB/ejemplo"
[ -f "$DB/H2/ejemplo/ejemplo.mv.db" ] || java CargarSQL.java "jdbc:h2:$DB/H2/ejemplo/ejemplo" sa "" ejemplo-java-dbs.sql
ok "H2     -> DB/H2/ejemplo/ejemplo"
rm -f "$SCRIPTS/derby.log"

info "ODBC: origen de datos 'Mysql-odbc'"
LIBODBC=$(find /usr/lib -name 'libmaodbc.so' 2>/dev/null | head -1)
if [ -n "$LIBODBC" ]; then
  sudo tee /etc/odbcinst.ini >/dev/null <<EOF
[MariaDB]
Description = Driver ODBC MariaDB/MySQL
Driver      = $LIBODBC
EOF
  sudo tee /etc/odbc.ini >/dev/null <<'EOF'
[Mysql-odbc]
Description = MySQL base de datos ejemplo
Driver      = MariaDB
Server      = 127.0.0.1
Port        = 3306
Database    = ejemplo
User        = ejemplo
Password    = ejemplo
EOF
  ok "DSN Mysql-odbc creado"
else
  warn "No se encontró el driver ODBC"
fi

info "Esperando a MySQL"
for i in $(seq 1 60); do
  mysqladmin ping -h 127.0.0.1 -u ejemplo -pejemplo --silent 2>/dev/null && break
  sleep 2
done
mysqladmin ping -h 127.0.0.1 -u ejemplo -pejemplo --silent 2>/dev/null && ok "MySQL responde" || warn "MySQL aún no responde (espera un poco y ejecuta probar-todo.sh)"

info "Comprobación"
"$SCRIPTS/probar-todo.sh"
echo -e "\n\e[32mEntorno listo. Lee el README.md para ver cómo usar cada base de datos.\e[0m"

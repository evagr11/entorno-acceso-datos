# Entorno Acceso a Datos · Tema 2 (Manejo de conectores)

Entorno Linux listo para los ejemplos y actividades del **Capítulo 2** del libro, en **GitHub Codespaces**.
Se abre desde el navegador en cualquier ordenador (clase o casa), sin instalar nada ni usar máquinas virtuales.

## Cómo abrirlo

1. En esta página del repo: botón verde **Code** → pestaña **Codespaces** → **Create codespace on main**.
2. La primera vez tarda unos minutos: se prepara todo solo (verás el progreso en la terminal).
3. Las siguientes veces: **Code → Codespaces →** elige el que ya creaste. Todo lo que hiciste sigue ahí.

> Al terminar, para el Codespace (menú ☰ → *Stop Current Codespace* o se para solo a los 30 min sin uso).
> El plan gratuito incluye unas 60 horas al mes.

## Qué incluye

| Base de datos | Cómo se usa (en la terminal) | BD de ejemplo |
|---|---|---|
| **SQLite** | `sqlite3 ~/DB/SQLITE/ejemplo.db` | `~/DB/SQLITE/ejemplo.db` |
| **Apache Derby** | `ij` → `connect 'jdbc:derby:/home/vscode/DB/DERBY/ejemplo';` | `~/DB/DERBY/ejemplo` |
| **HSQLDB** | `hsqldb-manager` (se ve en el Escritorio, ver abajo) | `jdbc:hsqldb:file:/home/vscode/DB/HSQLDB/ejemplo` · usuario `SA` |
| **H2** | `h2-console` (se abre en el navegador, puerto 8082) | `jdbc:h2:~/DB/H2/ejemplo/ejemplo` · usuario `sa` |
| **MySQL** | `mysql -h 127.0.0.1 -u ejemplo -pejemplo ejemplo` | BD `ejemplo`, usuario `ejemplo`, clave `ejemplo` |
| **ODBC** | `isql Mysql-odbc` | origen de datos `Mysql-odbc` (apartado 2.5) |
| **Db4o** | sube el jar `db4o-8.0.276.16149-all-java5.jar` a `DB/jars` | — |

Todas las BD `ejemplo` traen las tablas **departamentos** y **empleados** con los datos del libro.

- `~/DB` es la carpeta `DB` de este repo (igual que `/home/usuario/DB` en el libro).
- Los conectores JDBC están en `DB/jars` y ya están en el `CLASSPATH`, así que los comandos del libro
  (`java org.apache.derby.tools.ij`, `java org.hsqldb.util.DatabaseManager`…) funcionan tal cual.
- `ver-bd <url> [usuario] [clave]` muestra todas las tablas (columnas y filas) de cualquier BD, por ejemplo
  `ver-bd jdbc:sqlite:DB/SQLITE/ejemplo.db` o `ver-bd jdbc:mysql://localhost/ejemplo ejemplo ejemplo`.
- `probar-todo.sh` comprueba que las 5 bases de datos responden por JDBC.
- `recrear-ejemplos.sh` deja las BD `ejemplo` como al principio.

### Ventanas gráficas (HSQLDB Database Manager)

Pestaña **PUERTOS** (abajo) → puerto **6080 (Escritorio)** → icono del globo → **Connect** → clave `vscode`.
Ahí aparece la ventana de HSQLDB al ejecutar `hsqldb-manager`.

### Programas Java

- Ejemplo de la pág. 89 (JDBC con MySQL): `cd ejemplos/java && java Main.java`
- En VS Code puedes abrir cualquier `.java` y pulsar **Run**: los jars de `DB/jars` ya están enlazados.

### Programa C con ODBC (pág. 84)

```bash
cd ejemplos/c
gcc conexion.c -lodbc -std=gnu99 && ./a.out
```

## Carpetas

```
.devcontainer/   configuración del Codespace (no hace falta tocarla)
DB/              bases de datos (SQLITE, DERBY, HSQLDB, H2, DB4O) y conectores (jars)
scripts/         lanzadores y scripts SQL de las BD ejemplo
ejemplos/        programas del libro (Java y C)
actividades/              actividades 2.1 a 2.13 del tema
comprueba-tu-aprendizaje/ ejercicios 1 a 13 de "Comprueba tu aprendizaje"
ampliacion/               actividad de ampliación 1
proyecto-maven/           programas con JasperReports, Access y JAXB (2.13, ej. 10, 11 y 13)
```

**Todas las actividades y ejercicios del tema están resueltos.** En
[`actividades/README.md`](actividades/README.md) tienes la lista con el comando para ejecutar cada uno.

Para guardar tu trabajo en GitHub: pestaña **Control de código fuente** → escribe un mensaje → **Commit** → **Sync**.

## Limitaciones

- **Actividad 2.7 (puente JDBC-ODBC):** solo existe hasta Java 7, como avisa el libro (pág. 95). Con Java 21 no se puede hacer.
- **Oracle:** el Codespace no trae Oracle. El código de las partes de Oracle está hecho, pero hay que ejecutarlo en un equipo con Oracle XE.
- **MySQL** tiene además la BD `unidad2` (usuario `unidad2`, clave `unidad2`) para los ejercicios 5, 6, 7 y 10.

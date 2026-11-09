/*
 * ACTIVIDAD 2.7 (versión que funciona con un sistema actual)
 * La Actividad 2.6 hecha a través de ODBC, usando el origen de datos Mysql-odbc
 * igual que el programa conexion.c del libro (pág. 84).
 *
 * Compilar y ejecutar:
 *   gcc actividad2_7.c -lodbc -std=gnu99 -o actividad2_7 && ./actividad2_7
 */
#include <stdio.h>
#include <sql.h>
#include <sqlext.h>

/* Ejecuta una consulta y muestra todas las filas, columnas separadas por comas */
static void consulta(SQLHDBC dbc, const char *titulo, const char *sql) {
    SQLHSTMT stmt;
    SQLSMALLINT nCols = 0;
    SQLLEN nIndicator = 0;
    SQLCHAR buf[256];
    SQLRETURN ret;

    printf("\n%s\n", titulo);
    printf("==================================\n");
    SQLAllocHandle(SQL_HANDLE_STMT, dbc, &stmt);
    ret = SQLExecDirect(stmt, (SQLCHAR *) sql, SQL_NTS);
    if (!SQL_SUCCEEDED(ret)) {
        printf("ERROR al ejecutar: %s\n", sql);
        SQLFreeHandle(SQL_HANDLE_STMT, stmt);
        return;
    }
    SQLNumResultCols(stmt, &nCols);
    while (SQL_SUCCEEDED(SQLFetch(stmt))) {
        for (int i = 1; i <= nCols; ++i) {
            ret = SQLGetData(stmt, i, SQL_C_CHAR, buf, sizeof(buf), &nIndicator);
            if (SQL_SUCCEEDED(ret))
                printf("%s%s", buf, i < nCols ? ", " : "\n");
        }
    }
    SQLFreeHandle(SQL_HANDLE_STMT, stmt);
}

int main() {
    SQLHENV env;
    SQLHDBC dbc;
    SQLRETURN ret;

    SQLAllocHandle(SQL_HANDLE_ENV, SQL_NULL_HANDLE, &env);
    SQLSetEnvAttr(env, SQL_ATTR_ODBC_VERSION, (void *) SQL_OV_ODBC3, 0);
    SQLAllocHandle(SQL_HANDLE_DBC, env, &dbc);
    ret = SQLDriverConnect(dbc, NULL, (SQLCHAR *) "DSN=Mysql-odbc;", SQL_NTS,
                           NULL, 0, NULL, SQL_DRIVER_COMPLETE);
    if (!SQL_SUCCEEDED(ret)) {
        printf("NO HA SIDO POSIBLE LA CONEXIÓN\n");
        return 1;
    }
    printf("Conectado\n");

    consulta(dbc, "EMPLEADOS DEL DEPARTAMENTO 10",
             "SELECT apellido, oficio, salario FROM empleados WHERE dept_no = 10");

    consulta(dbc, "EMPLEADO CON MÁXIMO SALARIO",
             "SELECT e.apellido, e.salario, d.dnombre "
             "FROM empleados e JOIN departamentos d ON e.dept_no = d.dept_no "
             "WHERE e.salario = (SELECT MAX(salario) FROM empleados)");

    SQLDisconnect(dbc);
    SQLFreeHandle(SQL_HANDLE_DBC, dbc);
    SQLFreeHandle(SQL_HANDLE_ENV, env);
    return 0;
}

#include <stdio.h>
#include <sql.h>
#include <sqlext.h>

int main() {
    SQLHENV env; SQLHDBC dbc; SQLHSTMT stmt; SQLRETURN ret;
    SQLAllocHandle(SQL_HANDLE_ENV, SQL_NULL_HANDLE, &env);
    SQLSetEnvAttr(env, SQL_ATTR_ODBC_VERSION, (void *) SQL_OV_ODBC3, 0);
    SQLAllocHandle(SQL_HANDLE_DBC, env, &dbc);
    ret = SQLDriverConnect(dbc, NULL, (SQLCHAR *) "DSN=Mysql-odbc;", SQL_NTS,
                           NULL, 0, NULL, SQL_DRIVER_COMPLETE);
    char *consulta = "SELECT * FROM departamentos";
    SQLSMALLINT nCols = 0; SQLLEN nFilas = 0; SQLLEN nIndicator = 0;
    SQLCHAR buf[1024] = {0};
    if (SQL_SUCCEEDED(ret)) {
        printf("Conectado\n");
        SQLAllocHandle(SQL_HANDLE_STMT, dbc, &stmt);
        ret = SQLExecDirect(stmt, (SQLCHAR *) consulta, SQL_NTS);
        SQLNumResultCols(stmt, &nCols);
        SQLRowCount(stmt, &nFilas);
        printf("* Número de Columnas: %d\n", nCols);
        printf("* Número de Filas: %ld\n", (long) nFilas);
        while (SQL_SUCCEEDED(ret = SQLFetch(stmt))) {
            printf("\n");
            for (int i = 1; i <= nCols; ++i) {
                ret = SQLGetData(stmt, i, SQL_C_CHAR, buf, 1024, &nIndicator);
                if (SQL_SUCCEEDED(ret)) printf("* Columna %s", buf);
            }
        }
        printf("\n");
        SQLFreeHandle(SQL_HANDLE_STMT, stmt);
        SQLDisconnect(dbc);
    } else {
        printf("NO HA SIDO POSIBLE LA CONEXIÓN\n");
    }
    return 1;
}

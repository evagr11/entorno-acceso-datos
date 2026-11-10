FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm

# La imagen trae el repositorio de Yarn con la clave GPG caducada y hace fallar apt-get update.
# Yarn no se usa en este entorno, así que se quita.
RUN rm -f /etc/apt/sources.list.d/yarn*

# SQLite, cliente MySQL, ODBC (unixODBC + driver MariaDB/MySQL) y gcc para el ejemplo en C
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get install -y --no-install-recommends \
       sqlite3 default-mysql-client unixodbc unixodbc-dev odbc-mariadb \
       build-essential wget unzip maven \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

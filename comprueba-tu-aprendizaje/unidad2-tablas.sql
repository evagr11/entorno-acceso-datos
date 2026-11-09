-- Ejercicio 5: tablas PRODUCTOS, CLIENTES y VENTAS (válido para MySQL, Oracle y SQLite).
-- El programa Ejercicio5.java ya las crea si no existen; este script es por si quieres
-- crearlas a mano:
--   MySQL : mysql -h 127.0.0.1 -u unidad2 -punidad2 unidad2 < unidad2-tablas.sql
--   SQLite: sqlite3 ~/DB/SQLITE/UNIDAD2.DB ".read unidad2-tablas.sql"
--   Oracle: conectado como unidad2 en SQL*Plus:  @unidad2-tablas.sql
--   (en Oracle, antes, como SYSTEM: CREATE USER unidad2 IDENTIFIED BY unidad2;
--                                   GRANT CONNECT, RESOURCE, UNLIMITED TABLESPACE TO unidad2;)
CREATE TABLE PRODUCTOS (
  ID          INTEGER PRIMARY KEY,
  DESCRIPCION VARCHAR(50) NOT NULL,
  STOCKACTUAL INTEGER,
  STOCKMINIMO INTEGER,
  PVP         FLOAT
);
CREATE TABLE CLIENTES (
  ID        INTEGER PRIMARY KEY,
  NOMBRE    VARCHAR(50) NOT NULL,
  DIRECCION VARCHAR(50),
  POBLACION VARCHAR(50),
  TELEF     VARCHAR(20),
  NIF       VARCHAR(10)
);
CREATE TABLE VENTAS (
  IDVENTA    INTEGER PRIMARY KEY,
  FECHAVENTA DATE NOT NULL,
  IDCLIENTE  INTEGER,
  IDPRODUCTO INTEGER,
  CANTIDAD   INTEGER,
  CONSTRAINT FK_VENTAS_CLI FOREIGN KEY (IDCLIENTE)  REFERENCES CLIENTES(ID),
  CONSTRAINT FK_VENTAS_PRO FOREIGN KEY (IDPRODUCTO) REFERENCES PRODUCTOS(ID)
);

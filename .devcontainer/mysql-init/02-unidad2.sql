-- Base de datos UNIDAD2 (Comprueba tu aprendizaje, ejercicios 5, 6, 7 y 10)
CREATE DATABASE IF NOT EXISTS unidad2 CHARACTER SET utf8mb4;
CREATE USER IF NOT EXISTS 'unidad2'@'%' IDENTIFIED WITH mysql_native_password BY 'unidad2';
GRANT ALL PRIVILEGES ON unidad2.* TO 'unidad2'@'%';
FLUSH PRIVILEGES;

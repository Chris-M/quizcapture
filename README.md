quizcapture
======
Simply quiz capturing tool

Prerequisite
======
- [Play][playframeworksetup] => 2.6.x
- [MySQL][mysqlsetup] => 5.x

Setup
======
- Create Database for quizcapture

  `CREATE DATABASE dbname;`
- Create SQL user for quizcpature
  `GRANT ALL PRIVILEGES ON dbname.* TO 'username'@'localhost' IDENTIFIED BY 'password';`
- Update quizcapture/conf/application.conf With the appropriate databasename,username, password for the mysql db
- Ebean Should auto create the database schema uption page startup

[playframeworksetup]: <https://www.playframework.com/documentation/2.6.x/Installing>
[mysqlsetup]: <https://dev.mysql.com/doc/refman/5.7/en/installing.html>

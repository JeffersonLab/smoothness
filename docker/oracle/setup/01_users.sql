alter session set container = XEPDB1;

ALTER SYSTEM SET db_create_file_dest = '/opt/oracle/oradata';

create tablespace SMOOTHNESS;

create user "SMOOTHNESS_OWNER" profile "DEFAULT" identified by "password" default tablespace "SMOOTHNESS" account unlock;

grant connect to SMOOTHNESS_OWNER;
grant unlimited tablespace to SMOOTHNESS_OWNER;

grant create view to SMOOTHNESS_OWNER;
grant create sequence to SMOOTHNESS_OWNER;
grant create table to SMOOTHNESS_OWNER;
grant create procedure to SMOOTHNESS_OWNER;
grant create type to SMOOTHNESS_OWNER;
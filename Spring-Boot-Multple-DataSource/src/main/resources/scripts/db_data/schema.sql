DROP table if exists data01;
DROP table if exists data02;
create table data01(name varchar(50) not null primary key, enabled boolean not null);
create table data02(name varchar(50) not null primary key, enabled boolean not null);
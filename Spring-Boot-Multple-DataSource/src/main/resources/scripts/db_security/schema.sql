DROP table if exists authorities;
DROP table if exists users;");
create table users(username varchar(50) not null primary key,
	password varchar(500) not null,
	enabled boolean not null);
create table authorities (
    username varchar(50) not null,
    authority varchar(50) not null,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username));
create unique index ix_auth_username on authorities (username,authority);
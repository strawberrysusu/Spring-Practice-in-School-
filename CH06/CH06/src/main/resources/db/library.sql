CREATE DATABASE IF NOT EXISTS library;

USE library;

CREATE TABLE IF NOT EXISTS TB_ADMIN_ACCOUNT
(
    no       int auto_increment,
    approval int default 0 not null,
    id       varchar(20)   not null,
    password varchar(100)  not null,
    name     varchar(20)   not null,
    gender   char          not null,
    part     varchar(20)   not null,
    position varchar(20)   not null,
    email    varchar(50)   not null,
    phone    varchar(20)   not null,
    regDate  datetime      null,
    modDate  datetime      null,
    constraint TB_ADMIN_ACCOUNT_pk
        primary key (no)
);

drop table blog if exists;

create table blog (
    id int primary key,
    title varchar(255) not null
);

insert into blog (id, title) values (1, 'Java');
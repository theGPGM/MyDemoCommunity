create table AUTH_USER(
    id bigint primary key auto_increment,
    user_id bigint not null,
    identity_type int,
    identifier varchar(100),
    credential varchar(256)
);
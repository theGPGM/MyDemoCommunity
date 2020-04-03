create table NOTIFICATION (
    id bigint primary key auto_increment,
    notifier_id bigint not null,
    receiver_id bigint not null,
    outer_id bigint not null,
    type int not null,
    gmt_create bigint not null,
    status int default 0
);
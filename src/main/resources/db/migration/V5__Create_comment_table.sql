create table comment (

    id bigint primary key auto_increment,
    parent_id bigint not null,
    type int not null,
    commentator_id long not null,
    gmt_create bigint not null,
    gmt_modified bigint not null,
    like_count bigint not null,
    content varchar(1024) not null
);

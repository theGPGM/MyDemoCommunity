create table COMMENT (

    id bigint primary key auto_increment,
    parent_id bigint not null,
    type int not null,
    commentator_id bigint not null,
    gmt_create bigint,
    gmt_modified bigint,
    like_count int default 0,
    comment_count  int default 0,
    content varchar(1024) not null
);

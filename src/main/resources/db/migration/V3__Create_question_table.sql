create table question (

    id bigint auto_increment primary key not null,
    title varchar(50),
    description text,
    gmt_create bigint,
    gmt_modified bigint,
    creator_id bigint,
    comment_count int default 0,
    view_count int default 0,
    like_count int default 0,
    tag varchar(256
    )
);
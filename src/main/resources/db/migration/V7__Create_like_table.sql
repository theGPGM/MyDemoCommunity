create table LIKE_COMMENT (
    id bigint primary key auto_increment,
    liker_id bigint not null,
    comment_id bigint not null
);
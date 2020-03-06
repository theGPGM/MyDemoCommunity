create table comment (

    id bigint primary key auto_increment,
    question_id bigint not null,
    type int not null,
    commentator_id long not null,
    gmt_create bigint not null,
    gmt_modified bigint not null,
    like_count bigint not null,
    comment text not null
);

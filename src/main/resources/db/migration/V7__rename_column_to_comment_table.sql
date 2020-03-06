alter table comment drop column question_id;
alter table comment add parent_id bigint not null;
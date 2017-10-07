# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table quiz_result (
  id                            bigint auto_increment not null,
  student_id                    bigint,
  attempt                       integer not null,
  correct                       integer not null,
  total                         integer not null,
  constraint uq_quiz_result_student_id_attempt unique (student_id,attempt),
  constraint pk_quiz_result primary key (id)
);

create table student (
  id                            bigint auto_increment not null,
  name                          varchar(255) not null,
  constraint uq_student_name unique (name),
  constraint pk_student primary key (id)
);

alter table quiz_result add constraint fk_quiz_result_student_id foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_quiz_result_student_id on quiz_result (student_id);


# --- !Downs

alter table quiz_result drop foreign key fk_quiz_result_student_id;
drop index ix_quiz_result_student_id on quiz_result;

drop table if exists quiz_result;

drop table if exists student;


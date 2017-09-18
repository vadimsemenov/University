-- Execute:
-- $ createdb ctd && psql -d ctd -f hw1.sql

create table Groups (
    group_id    int,
    group_no    char(5)
);

create table Students (
    student_id  int,
    name        varchar(30),
    group_id    int
);

insert into Groups
    (group_id, group_no) values
        (1, 'M3438'),
        (2, 'M3439');

insert into Students
    (student_id, name, group_id) values
        (1, 'Ruslan Ahundov', 1),
        (2, 'Pavel Asadchy', 2),
        (3, 'Eugene Varlamov', 2);

select group_id, group_no from Groups; -- List of groups
select student_id, name, group_id from Students; -- List of students

-- List of students and groups
select name, group_no 
    from Students natural join Groups;
select Students.name, Groups.group_no
    from Students
        inner join Groups
        on Students.group_id = Groups.group_id;

-- Permit inserting existing group_id
alter table Groups
    add constraint group_id_unique unique (group_id);

-- Permit inserting Student with nonexisting group_id
alter table Students
    add foreign key (group_id) references Groups (group_id);


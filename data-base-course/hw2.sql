-- Vadim Semenov, Postgresql 9.5

-- Creating

create table if not exists Groups (
    Id      int not null,
    Name    varchar(10) not null,
    primary key (id)
);

create table if not exists Students (
    Id      int not null,
    Name    varchar(50) not null,
    GroupId int not null,
    primary key (Id),
    constraint group_fk
        foreign key (GroupId)
        references Groups (Id)
);

create table if not exists Courses (
    Id      int not null,
    Name    varchar(50) not null,
    primary key (Id)
);

create table if not exists Teachers (
    Id      int not null,
    Name    varchar(50) not null,
    primary key (Id)
);

create table if not exists Marks (
    StudentId   int,
    CourseId    int,
    Mark        int,
    constraint student_fk
        foreign key (StudentId)
        references Students (Id),
    constraint course_fk
        foreign key (CourseId)
        references Courses (Id)
);

create table if not exists CourseTeacher (
    CourseId    int,
    TeacherId   int,
    constraint course_fk
        foreign key (CourseId)
        references Courses (Id),
    constraint teacher_fk
        foreign key (TeacherId)
        references Teachers (Id)
);


-- Filling

insert into Groups
    (Id, Name) values
    (1, 'M3439'),
    (2, 'M3438');

insert into Students
    (Id, Name, GroupId) values
    (1, 'Vadim Semenov', 1);

insert into Courses
    (Id, Name) values
    (1, 'Database course');

insert into Teachers
    (Id, Name) values
    (1, 'Georgiy Korneev');

insert into CourseTeacher
    (CourseId, TeacherId) values
    (1, 1);

insert into Marks
    (StudentId, CourseId, Mark) values
    (1, 1, 100);


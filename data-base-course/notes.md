# Intro

## Homework 1
1. Установите систему управления реляционными базами данных. 
2. Узнайте, как в вашей СУБД исполнять SQL в интерактивном режиме. 
3. Узнайте, как в вашей СУБД исполнять SQL в пакетном режиме. 
4. Разберитесь, как в вашей СУБД осуществляется поддержка русского языка. 
5. Создайте базу данных и наполните ее в соответствии с разобранным примерами.


Done in PostgreSQL v9.5

* (2) `psql --help`
* (3) `psql -d $dbname -f $filename`
* (4) [Link to email](https://www.postgresql.org/message-id/20060329203545.M43728@narrowpathinc.com)  
`psql -e UTF8`
* (5) See [solution](./hw1.sql)


## Homework 2
Спроектируйте базу данных «Деканат», позволяющую хранить информацию о студентах, 
группах, преподавателях, предметах, оценках. 
1. Составьте модель сущность-связь. 
2. Преобразуйте модель сущность-связь в физическую модель. 
3. Запишите физическую модель на языке SQL. Модель должна включать объявления ограничений. 
4. Создайте базу данных по спроектированной модели. 
5. Запишите операторы SQL, заполняющие базу тестовыми данными.


Done in PostgreSQL v9.5
* (1-2) [link to lucidchart](https://www.lucidchart.com/invitations/accept/0cc2d5a7-0828-4da4-91b5-b9843b2c7130)
* (3-5) See [solution](./hw2.sql)


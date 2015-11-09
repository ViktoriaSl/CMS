INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(1, 'dorogan.olga.test@gmail.com', '123', 'Ольга', 'teacherSecondName_1', 'в роли препод.', 'TEACHER');
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(2, 'codedealerb@gmail.com', '123', 'teacherName_1', 'teacherSecondName_1', 'teacherSurname_1', 'TEACHER');
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(3, 'selivanovavika1993@gmail.com', '123', 'teacherName_2', 'teacherSecondName_2', 'teacherSurname_2', 'TEACHER');

INSERT INTO courses(id, name, startdate, enddate, description, calendar_id) VALUES (1, 'Java SE', '2015-07-01', '2016-07-31','Стандартная версия платформы Java, предназначенная для создания и исполнения апплетов и приложений, рассчитанных на индивидуальное пользование или на использование в масштабах малого предприятия. Не включает в себя многие возможности, предоставляемые более мощной и расширенной платформой Java 2 Enterprise Edition (J2EE), рассчитанной на создание коммерческих приложений масштаба крупных и средних предприятий.', 'an7o3b1l94eetmuo0i8ueskvf0@group.calendar.google.com');
INSERT INTO courses(id, name, startdate, enddate, description, calendar_id) VALUES (2, 'Java EE', '2014-01-10', '2015-07-31',' Набор спецификаций и соответствующей документации для языка Java, описывающей архитектуру серверной платформы для задач средних и крупных предприятий.', 'an7o3b1l94eetmuo0i8ueskvf0@group.calendar.google.com');

INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (1, 1, 1, 'SIGNED');

INSERT INTO news(id, title, description, date, course_id) VALUES (1, 'Java SE', 'Курс Java SE начался', '2015-07-08 18:48:05',1);
INSERT INTO news(id, title, description, date, course_id) VALUES (2, 'Java EE', 'Курс Java EE начался', '2015-09-01 17:35:01',2);

-- Persons to test approving / rejecting course status
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(4, 'leo@gmail.com', '123', 'Leonard', 'Leakey', 'Hofstadter', 'STUDENT');
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(5, 'shelly@gmail.com', '123', 'Sheldon', 'Lee', 'Cooper', 'STUDENT');
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(6, 'howie@gmail.com', '123', 'Howard', 'Joel', 'Wolowitz', 'STUDENT');
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(7, 'raj@gmail.com', '123', 'Rajesh', 'Ramayan', 'Koothrappali', 'STUDENT');

INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (5, 1, 4, 'REQUESTED');
INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (6, 1, 5, 'REQUESTED');
INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (7, 1, 6, 'REQUESTED');
INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (8, 1, 7, 'REQUESTED');

INSERT INTO lesson(id, date, topic, order_num, description, type, course_id) VALUES(1, '2015-10-01', 'Введение', 1, 'Краткое содержимое курса', null, 1);
INSERT INTO lesson(id, date, topic, order_num, description, type, course_id) VALUES(2, '2015-10-08', 'Коллеции', 2, 'Знакомство с Java Collections Framework', null, 1);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (1, 1, 'Установить Ubuntu 15.01', 1);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (2, 2, 'Установить JDK8, Maven и Intellij Idea', 1);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (3, 1, 'Выбрать один из интерфейсов коллекций (List, Set or Map) и написать собственную его реализацию', 2);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (4, 2, 'Написать тесты к методам выбранного интерфейса', 2);

INSERT INTO mark(id, mark, lesson_id, person_id) VALUES (1, 100, 1, 4);
INSERT INTO mark(id, mark, lesson_id, person_id) VALUES (2, 90, 3, 4);

-- Persons to test student progress
-- INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(8, 'dorogan.olga.n@gmail.com', '123', 'Olga', '', 'olga', 'STUDENT');
-- INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (9, 1, 8, 'SIGNED');
-- INSERT INTO mark(id, mark, lesson_id, person_id) VALUES (3, 10, 1, 8);
-- INSERT INTO mark(id, mark, lesson_id, person_id) VALUES (4, 20, 3, 8);
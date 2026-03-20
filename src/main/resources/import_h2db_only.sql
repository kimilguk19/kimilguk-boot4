DELETE FROM SIMPLE_USERS
INSERT INTO SIMPLE_USERS(create_date,modified_date,id, enabled, password, role, username) VALUES (CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,1, true, '$2a$12$gZ.yvR4/mbCM6ev5L88NOeCh3VeH07K3c5kErn02NwrlsCRyX6fMi', 'ADMIN', 'admin')
INSERT INTO SIMPLE_USERS(create_date,modified_date,id, enabled, password, role, username) VALUES (CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,2, true, '$2a$12$gZ.yvR4/mbCM6ev5L88NOeCh3VeH07K3c5kErn02NwrlsCRyX6fMi', 'USER', 'user')
DELETE FROM POSTS
INSERT INTO POSTS(create_date,modified_date,id,title,author,content) VALUES (CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,1,'공지사항','admin','공지사항 테스트 입니다.'),(CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,2,'겔러리','admin','갤러리 테스트 입니다.')

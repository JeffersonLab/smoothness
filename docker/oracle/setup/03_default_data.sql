alter session set container = XEPDB1;

insert into SMOOTHNESS_OWNER.MOVIE (MOVIE_ID, TITLE, RELEASE_DATE, DESCRIPTION, MPAA_RATING, DURATION_MINUTES) values (SMOOTHNESS_OWNER.MOVIE_ID.nextval, 'Ghostbusters', TO_DATE('1984/06/07', 'yyyy/mm/dd'), 'Three parapsychologists forced out of their university funding set up shop as a unique ghost removal service in New York City, attracting frightened yet skeptical customers.', 'PG', 105);
insert into SMOOTHNESS_OWNER.MOVIE (MOVIE_ID, TITLE, RELEASE_DATE, DESCRIPTION, MPAA_RATING, DURATION_MINUTES) values (SMOOTHNESS_OWNER.MOVIE_ID.nextval, 'Star Wars: Episode IV - A New Hope', TO_DATE('1977/05/25', 'yyyy/mm/dd'), 'Luke Skywalker joins forces with a Jedi Knight, a cocky pilot, a Wookiee and two droids to save the galaxy from the Empire''s world-destroying battle station, while also attempting to rescue Princess Leia from the mysterious Darth Vader.', 'PG', 121);
insert into SMOOTHNESS_OWNER.MOVIE (MOVIE_ID, TITLE, RELEASE_DATE, DESCRIPTION, MPAA_RATING, DURATION_MINUTES) values (SMOOTHNESS_OWNER.MOVIE_ID.nextval, 'Jaws', TO_DATE('1975/06/20', 'yyyy/mm/dd'), 'When a killer shark unleashes chaos on a beach community off Long Island, it''s up to a local sheriff, a marine biologist, and an old seafarer to hunt the beast down.', 'PG', 124);



insert into SMOOTHNESS_OWNER.STAFF (STAFF_ID, USERNAME, FIRSTNAME, LASTNAME) values (SMOOTHNESS_OWNER.STAFF_ID.nextval, 'tester1', 'firstname1', 'lastname1');
insert into SMOOTHNESS_OWNER.STAFF (STAFF_ID, USERNAME, FIRSTNAME, LASTNAME) values (SMOOTHNESS_OWNER.STAFF_ID.nextval, 'tester2', 'firstname2', 'lastname2');
insert into SMOOTHNESS_OWNER.STAFF (STAFF_ID, USERNAME, FIRSTNAME, LASTNAME) values (SMOOTHNESS_OWNER.STAFF_ID.nextval, 'tester3', 'firstname3', 'lastname3');

commit;
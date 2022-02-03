alter session set container = XEPDB1;

-- Populate Movies
insert into SMOOTHNESS_OWNER.MOIVE (MOVIE_ID, TITLE, RELEASE_DATE, DESCRIPTION, MPAA_RATING, DURATION_MINUTES) values (SMOOTHNESS_OWNER.MOVIE_ID.nextval, 'Ghostbusters', TO_DATE('1984/06/07', 'yyyy/mm/dd'), 'Three parapsychologists forced out of their university funding set up shop as a unique ghost removal service in New York City, attracting frightened yet skeptical customers.', 'PG', 105);
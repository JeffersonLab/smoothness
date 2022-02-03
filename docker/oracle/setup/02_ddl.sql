alter session set container = XEPDB1;

DROP SEQUENCE SMOOTHNESS_OWNER.MOVIE_ID;
DROP SEQUENCE SMOOTHNESS_OWNER.STAFF_ID;


DROP TABLE SMOOTHNESS_OWNER.MOVIE CASCADE CONSTRAINTS PURGE;
DROP TABLE SMOOTHNESS_OWNER.STAFF CASCADE CONSTRAINTS PURGE;

CREATE SEQUENCE SMOOTHNESS_OWNER.MOVIE_ID;
CREATE SEQUENCE SMOOTHNESS_OWNER.STAFF_ID;

CREATE TABLE SMOOTHNESS_OWNER.MOVIE
(
    MOVIE_ID             INTEGER NOT NULL ,
    TITLE                VARCHAR2(128 CHAR) NULL ,
    RELEASE_DATE         DATE NULL ,
    DESCRIPTION          VARCHAR2(512 CHAR) NULL ,
    MPAA_RATING          VARCHAR2(5 CHAR) NULL  CONSTRAINT  MOVIE_CK1 CHECK (MPAA_RATING IN ('G', 'PG', 'PG-13', 'R', 'NC-17')),
    DURATION_MINUTES     INTEGER NULL ,
    CONSTRAINT  MOVIE_PK PRIMARY KEY (MOVIE_ID)
);

CREATE TABLE SMOOTHNESS_OWNER.STAFF
(
    STAFF_ID          INTEGER NOT NULL ,
    USERNAME          VARCHAR2(32 CHAR) NOT NULL ,
    FIRSTNAME         VARCHAR2(32 CHAR) NOT NULL ,
    LASTNAME          VARCHAR2(32 CHAR) NOT NULL ,
    CONSTRAINT STAFF_PK PRIMARY KEY (STAFF_ID)
);
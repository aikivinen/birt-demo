-- the database schema for spring data jdbc to initialize



CREATE USER birtuser WITH PASSWORD 'birt';

CREATE DATABASE birtdemo;
ALTER DATABASE birtdemo OWNER TO birtuser;

\connect birtdemo

CREATE TABLE audit_log
(
  date date,
  log text,
  id serial NOT NULL,
  CONSTRAINT log_id PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

ALTER TABLE audit_log
  OWNER TO birtuser;
  
CREATE TABLE users
(
  username character varying(50) NOT NULL,
  password character varying(50) NOT NULL,
  enabled boolean NOT NULL DEFAULT true,
  email character varying(256),
  phone character varying(50),
  authority character varying(50),
  right_add_report boolean NOT NULL DEFAULT false,
  right_print_report boolean NOT NULL DEFAULT false,
  right_remove_report boolean NOT NULL DEFAULT false,
  right_edit_report boolean NOT NULL DEFAULT false,
  CONSTRAINT pk_users PRIMARY KEY (username)
);
ALTER TABLE users OWNER TO birtuser;


CREATE TABLE values1
(
  optlock integer,
  id serial NOT NULL,
  text_val1 text,
  text_val2 text,
  text_val3 text,
  int_val1 integer,
  int_val2 integer,
  int_val3 integer,
  CONSTRAINT values1_id PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE values1
  OWNER TO birtuser;

  CREATE TABLE values2
(
  optlock integer,
  id serial NOT NULL,
  text_val1 text,
  text_val2 text,
  text_val3 text,
  int_val1 integer,
  int_val2 integer,
  int_val3 integer,
  CONSTRAINT values2_id PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE values2
  OWNER TO birtuser;
  
CREATE TABLE values3
(
  optlock integer,
  id serial NOT NULL,
  text_val1 text,
  text_val2 text,
  text_val3 text,
  int_val1 integer,
  int_val2 integer,
  int_val3 integer,
  CONSTRAINT values3_id PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE values3
  OWNER TO birtuser;

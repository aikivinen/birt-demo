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
ALTER TABLE values1
  OWNER TO birtuser;

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

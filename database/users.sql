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
)

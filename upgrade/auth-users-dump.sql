CREATE TABLE auth_users (
	id integer NOT NULL,
	user_id integer,
	type character varying(255),
	username character varying(255),
	email character varying(255),
  password character varying(255),
  confirmation_token character varying(255),
  token_generation_date timestamp without time zone,
	last_access timestamp without time zone,
	enrolled_courses_acronyms text,
  active boolean,
	PRIMARY KEY (id),	
	CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);


ALTER TABLE users  ADD COLUMN auth_user_id integer;
ALTER TABLE users ADD CONSTRAINT fk_auth_user FOREIGN KEY (auth_user_id) REFERENCES auth_users(id); 

CREATE SEQUENCE auth_users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE auth_users_id_seq OWNED BY auth_users.id;

ALTER TABLE ONLY auth_users ALTER COLUMN id SET DEFAULT nextval('auth_users_id_seq'::regclass);

SELECT pg_catalog.setval('public.auth_users_id_seq', 737, true);


INSERT INTO auth_users(user_id, username, enrolled_courses_acronyms, last_access)
  SELECT id, username, enrolled_courses_acronyms, last_access
  FROM users;

UPDATE auth_users SET Auth_type = CASE
  WHEN username LIKE 'ist%' THEN 'TECNICO'
  WHEN username LIKE 'Demo-%' THEN 'DEMO'
  ELSE 'EXTERNAL'
END;


DROP INDEX users_indx_0;

ALTER TABLE users
  DROP COLUMN username, 
  DROP COLUMN enrolled_courses_acronyms,
  DROP COLUMN last_access;


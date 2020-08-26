ALTER TABLE Users ADD COLUMN Active boolean;
ALTER TABLE Users ADD COLUMN password character varying(255);
ALTER TABLE Users ADD COLUMN email character varying(255);
--ALTER TABLE Users ADD COLUMN confirmation_token character varying(255);



UPDATE Users SET Active = CASE
  WHEN username LIKE 'ist%' THEN TRUE
  ELSE FALSE
END;

 	


CREATE TABLE auth_users (
	id integer NOT NULL,
	user_id integer,
	type character varying(255),
	username character varying(255),
	email character varying(255),
  password character varying(255),
  --confirmation_token character varying(255),
  --token_generation_date timestamp without time zone,
	--last_access timestamp without time zone,
	--enrolled_courses_acronyms text,
  --active boolean;
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



INSERT INTO auth_users(user_id, type)
  SELECT id, (CASE
                WHEN password <> '' THEN 'TECNICO' 
                ELSE 'EXTERNAL' 
              END)
    FROM users
    WHERE active;

UPDATE auth_users
SET username = u.username,
    user_id = u.id , 
    email = u.email,
    password = u.password
    --confirmation_token = u.confirmation_token,
    --token_generation_date = u.token_generation_date,
    --last_access = u.last_access,
    --enrolled_courses_acronyms = u.enrolled_courses_acronyms,
    --active = u.active
FROM users u
WHERE u.id = user_id;

ALTER TABLE users
  DROP COLUMN username, 
  DROP COLUMN email,
  DROP COLUMN password;
  --DROP COLUMN confirmation_token,
  --DROP COLUMN token_generation_date,
  --DROP COLUMN last_access,
  --DROP COLUMN enrolled_courses_acronyms,
  --DROP COLUMN active;

DROP INDEX users_indx_0;

ALTER TABLE auth_users
  ADD COLUMN username character varying(255),
  ADD COLUMN email character varying(255),
  ADD COLUMN password character varying(255),
  ADD COLUMN confirmation_token character varying(255),
  ADD COLUMN token_generation_date timestamp without time zone,
  ADD COLUMN last_access timestamp without time zone,
  ADD COLUMN enrolled_courses_acronyms text,
  ADD COLUMN active boolean;
  
UPDATE auth_users
SET username = u.username,
    email = u.email,
    password = u.password,
    confirmation_token = u.confirmation_token,
    token_generation_date = u.token_generation_date,
    last_access = u.last_access,
    enrolled_courses_acronyms = u.enrolled_courses_acronyms,
    active = u.active,
FROM users u
WHERE u.id = user_id;

ALTER TABLE users
  DROP COLUMN username, 
  DROP COLUMN email,
  DROP COLUMN password,
  DROP COLUMN confirmation_token,
  DROP COLUMN token_generation_date,
  DROP COLUMN last_access,
  DROP COLUMN enrolled_courses_acronyms,
  DROP COLUMN active;

ALTER TABLE ONLY public.users
ADD user_type character varying(32);

UPDATE public.users u
SET user_type = 'student'
WHERE role = 'STUDENT';

UPDATE public.users u
SET user_type = 'teacher'
WHERE role = 'TEACHER';

UPDATE public.users u
SET user_type = 'demo_admin'
WHERE role = 'DEMO_ADMIN';


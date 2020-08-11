ALTER TABLE Users  ADD COLUMN State character varying(255);
UPDATE Users SET State = 'ACTIVE';
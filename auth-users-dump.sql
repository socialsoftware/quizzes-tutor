ALTER TABLE Users  ADD COLUMN Active character varying(255);
UPDATE Users SET Active = true;
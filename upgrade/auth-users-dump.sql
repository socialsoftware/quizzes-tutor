ALTER TABLE Users ADD COLUMN Active boolean;
UPDATE Users SET Active = CASE
							  WHEN username LIKE 'ist%' THEN TRUE
							  ELSE FALSE
						  END;

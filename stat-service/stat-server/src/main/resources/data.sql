DELETE FROM statistics;
ALTER TABLE statistics ALTER COLUMN id RESTART WITH 1;
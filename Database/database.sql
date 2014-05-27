DROP TABLE interval;
DROP TABLE seasonal;
DROP TABLE event;
DROP TABLE pub;
DROP FUNCTION is_open(time);

CREATE TABLE pub(
pub_id SERIAL NOT NULL,
PRIMARY KEY (pub_id)
);

CREATE TABLE event(
event_id SERIAL NOT NULL,
pub_id SERIAL NOT NULL,
name TEXT NOT NULL,
open BOOLEAN,
PRIMARY KEY (event_id),
FOREIGN KEY (pub_id) REFERENCES pub
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE interval(
interval_id SERIAL NOT NULL,
event_id SERIAL NOT NULL,
periodic TEXT,
start_interval TIMESTAMP,
end_interval TIMESTAMP,
PRIMARY KEY (interval_id),
FOREIGN KEY (event_id) REFERENCES event
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE seasonal(
seasonal_id SERIAL NOT NULL,
event_id SERIAL NOT NULL,
start_seasonal TIMESTAMP,
end_seasonal TIMESTAMP,
PRIMARY KEY (seasonal_id),
FOREIGN KEY (event_id) REFERENCES event
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE FUNCTION is_open(time) RETURNS BOOLEAN AS '
	BEGIN
	  IF current_timestamp BETWEEN interval.start_interval and interval.end_interval && current_timestamp BETWEEN seasonal.start_seasonal and seasonal.start_seasonal
	  THEN
		RETURN TRUE;
	  ELSE
		RETURN FALSE;
	  END IF;
	END;
' LANGUAGE 'plpgsql';
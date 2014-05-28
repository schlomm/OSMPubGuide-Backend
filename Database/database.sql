DROP TABLE closed;
DROP TABLE opened;
DROP TABLE temporal_event;
DROP TABLE pub;
DROP FUNCTION is_open(p_id integer, ts timestamp);

CREATE TABLE pub(
pub_id SERIAL NOT NULL,
PRIMARY KEY (pub_id)
);

CREATE TABLE temporal_event(
event_id SERIAL NOT NULL,
pub_id SERIAL NOT NULL,
name TEXT NOT NULL,
description TEXT,
PRIMARY KEY (event_id),
FOREIGN KEY (pub_id) REFERENCES pub
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE closed(
closed_id SERIAL NOT NULL,
event_id SERIAL NOT NULL,
start_closed TIMESTAMP,
end_closed TIMESTAMP,
PRIMARY KEY (closed_id),
FOREIGN KEY (event_id) REFERENCES temporal_event
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE opened(
opened_id SERIAL NOT NULL,
event_id SERIAL NOT NULL,
periodic TEXT,
occurences INTEGER,
start_opened TIMESTAMP,
end_opened TIMESTAMP,
PRIMARY KEY (opened_id),
FOREIGN KEY (event_id) REFERENCES temporal_event
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE FUNCTION is_open(p_id integer, ts timestamp) RETURNS BOOLEAN AS $$
	DECLARE result BOOLEAN := FALSE;
	DECLARE number integer;
	BEGIN
	 SELECT COUNT(event_id) INTO number FROM pub NATURAL INNER JOIN temporal_event NATURAL INNER JOIN opened WHERE pub.pub_id = p_id AND (ts BETWEEN opened.start_opened AND opened.end_opened);
	 IF number != 0
	    THEN result := TRUE;
	 END IF;
	 SELECT COUNT(event_id) INTO number FROM pub NATURAL INNER JOIN temporal_event NATURAL INNER JOIN closed WHERE pub.pub_id = p_id AND (ts BETWEEN closed.start_closed AND closed.end_closed);
	 IF number != 0
		THEN result := FALSE;
	 END IF;
	 RETURN result;
	END;
$$ LANGUAGE 'plpgsql';
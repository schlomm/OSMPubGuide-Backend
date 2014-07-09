DROP TABLE closed;
DROP TABLE opened;
DROP TABLE temporal_event;
DROP TABLE pub;
DROP FUNCTION is_open(p_id integer, ts timestamp with time zone);
DROP FUNCTION is_open(p_id integer, startTs timestamp with time zone, stopTS timestamp with time zone);
DROP FUNCTION insert_periodic(event_id integer, state TEXT, start_date timestamp with time zone, end_date timestamp with time zone, end_periodic timestamp with time zone, periodic integer);
DROP FUNCTION delete_periodic(event_id integer, start_date timestamp with time zone, end_date timestamp with time zone, end_periodic timestamp with time zone, periodic integer);

CREATE TABLE pub(
pub_ref BIGSERIAL NOT NULL,
beer_price FLOAT,
happy_hour BOOLEAN,
PRIMARY KEY (pub_ref)
);

CREATE TABLE temporal_event(
event_id BIGSERIAL NOT NULL,
pub_ref BIGSERIAL NOT NULL,
name TEXT,
type TEXT,
description TEXT,
event BOOLEAN,
entry_fee TEXT,
PRIMARY KEY (event_id),
FOREIGN KEY (pub_ref) REFERENCES pub
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE closed(
closed_id BIGSERIAL NOT NULL,
event_id BIGSERIAL NOT NULL,
start_time TIMESTAMP ,
end_time TIMESTAMP,
PRIMARY KEY (closed_id),
FOREIGN KEY (event_id) REFERENCES temporal_event
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE opened(
opened_id BIGSERIAL NOT NULL,
event_id BIGSERIAL NOT NULL,
start_time TIMESTAMP,
end_time TIMESTAMP,
PRIMARY KEY (opened_id),
FOREIGN KEY (event_id) REFERENCES temporal_event
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

/*
	Function determines if a certain pub is opened at a certain time
	Param: p_id			INTEGER			OSM Pub ID of the pub
	Param: ts			TIMESTAMP		Checks if the pub is opened at this time
	Returns: true: pub is opened, false: pub is closed
*/
CREATE FUNCTION is_open(p_id integer, ts timestamp with time zone) RETURNS BOOLEAN AS $$
	DECLARE result BOOLEAN := FALSE;
	DECLARE number integer;
	BEGIN
	 SELECT COUNT(event_id) INTO number FROM pub NATURAL INNER JOIN temporal_event NATURAL INNER JOIN opened WHERE pub.pub_ref = p_id AND (ts BETWEEN opened.start_time AND opened.end_time);
	 IF number != 0
	    THEN result := TRUE;
	 END IF;
	 SELECT COUNT(event_id) INTO number FROM pub NATURAL INNER JOIN temporal_event NATURAL INNER JOIN closed WHERE pub.pub_ref = p_id AND (ts BETWEEN closed.start_time AND closed.end_time);
	 IF number != 0
		THEN result := FALSE;
	 END IF;
	 RETURN result;
	END;
$$ LANGUAGE 'plpgsql';

/*
	Function determines if a certain pub is opened at a certain time
	Param: p_id			INTEGER							OSM Pub ID of the pub
	Param: startTs		TIMESTAMP with time zone		Starttime of the interval which should be checked
	Param: stopTs		TIMESTAMP with time zone		Endtime of the interval which should be checked
	Returns: true: pub is opened, false: pub is closed
*/
CREATE FUNCTION is_open(p_id integer, startTS timestamp with time zone, stopTS timestamp with time zone) RETURNS BOOLEAN AS $$
	DECLARE result BOOLEAN := FALSE;
	DECLARE number integer;
	BEGIN
	 SELECT COUNT(event_id) INTO number FROM pub NATURAL INNER JOIN temporal_event NATURAL INNER JOIN opened WHERE pub.pub_ref = p_id AND (ts BETWEEN opened.start_time AND opened.end_time);
	 IF number != 0
	    THEN result := TRUE;
	 END IF;
	 SELECT COUNT(event_id) INTO number FROM pub NATURAL INNER JOIN temporal_event NATURAL INNER JOIN closed WHERE pub.pub_ref = p_id AND (ts BETWEEN closed.start_time AND closed.end_time);
	 IF number != 0
		THEN result := FALSE;
	 END IF;
	 RETURN result;
	END;
$$ LANGUAGE 'plpgsql';

/*
	Function creates multiple entries in the "closed" respectively the "opened"-table
	Param: event_id			INTEGER 		ID of the event for which the periodic opening/closed hours shall be created
	Param: state			TEXT 			can be 'opened' or 'closed', decides which table will be used
	Param: start_date		TIMESTAMP 		Start date of the first occurrence of opening hour/closed hour
	Param: end_date			TIMESTAMP		End date of the first occurrence of opening hour/closed hour
	Param: end_periodic		TIMESTAMP		Last occurrence of the periodic event
	Param: periodic			INTEGER			Timespan between end_periodic and next start of the event
*/
CREATE FUNCTION insert_periodic(event_id integer, state TEXT, start_date timestamp with time zone, end_date timestamp with time zone, end_periodic timestamp with time zone, periodic integer) RETURNS void AS $$
    DECLARE start1 integer := EXTRACT(EPOCH FROM start_date);
    DECLARE end1 integer := EXTRACT(EPOCH FROM end_date);
    DECLARE end_periodic INTEGER := EXTRACT(EPOCH FROM end_periodic); 
BEGIN
    WHILE (start1 <= end_periodic) 
    LOOP
    	IF state = 'opened'
    	THEN
			INSERT INTO opened (event_id, start_time, end_time) VALUES (event_id, to_timestamp(start1), to_timestamp(end1));
		END IF;
		IF state = 'closed'
    	THEN
			INSERT INTO closed (event_id, start_time, end_time) VALUES (event_id, to_timestamp(start1), to_timestamp(end1));
		END IF;
		start1 := start1 + periodic;
		end1 := end1 + periodic;
    END LOOP;
END 
$$ LANGUAGE 'plpgsql';

/*
	Function deletes multiple entries from "closed" and the "opened"-table
	Param: e_id				INTEGER 		ID of the event for which the periodic opening/closed hours shall be deleted
	Param: start_date		TIMESTAMP 		Start date of the first occurrence of opening hour/closed hour
	Param: end_date			TIMESTAMP		End date of the first occurrence of opening hour/closed hour
	Param: end_periodic		TIMESTAMP		Last occurrence of the periodic event
	Param: periodic			INTEGER			Timespan between end_periodic and next start of the event
*/
CREATE FUNCTION delete_periodic(e_id integer, start_date timestamp with time zone, end_date timestamp with time zone, end_periodic timestamp with time zone, periodic integer) RETURNS void AS $$
    DECLARE start1 integer := EXTRACT(EPOCH FROM start_date);
    DECLARE end1 integer := EXTRACT(EPOCH FROM end_date);
    DECLARE end_periodic INTEGER := EXTRACT(EPOCH FROM end_periodic); 
BEGIN
    WHILE (start1 <= end_periodic) 
    LOOP
		DELETE FROM closed WHERE event_id = e_id AND start_time = to_timestamp(start1) AND end_time = to_timestamp(end1);
		DELETE FROM opened WHERE event_id = e_id AND start_time = to_timestamp(start1) AND end_time = to_timestamp(end1);
		start1 := start1 + periodic;
		end1 := end1 + periodic;
    END LOOP;
END 
$$ LANGUAGE 'plpgsql';

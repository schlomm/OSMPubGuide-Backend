DROP TABLE closed;
DROP TABLE opened;
DROP TABLE temporal_event;
DROP TABLE pub;
DROP FUNCTION is_open(p_id integer, ts timestamp);
DROP FUNCTION insert_periodic(event_id integer, state TEXT, start_date timestamp, end_date timestamp, end_periodic timestamp, periodic integer);
DROP FUNCTION delete_periodic(event_id integer, start_date timestamp, end_date timestamp, end_periodic timestamp, periodic integer);

CREATE TABLE pub(
pub_id SERIAL NOT NULL,
beer_price FLOAT,
notice TEXT,
entry_fee FLOAT,
PRIMARY KEY (pub_id)
);

CREATE TABLE temporal_event(
event_id SERIAL NOT NULL,
pub_id SERIAL NOT NULL,
name TEXT,
type TEXT,
description TEXT,
event BOOLEAN,
kitchen_hour BOOLEAN,
happy_hour BOOLEAN,
entry_fee FLOAT,
PRIMARY KEY (event_id),
FOREIGN KEY (pub_id) REFERENCES pub
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE closed(
closed_id SERIAL NOT NULL,
event_id SERIAL NOT NULL,
start_time TIMESTAMP,
end_time TIMESTAMP,
PRIMARY KEY (closed_id),
FOREIGN KEY (event_id) REFERENCES temporal_event
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE opened(
opened_id SERIAL NOT NULL,
event_id SERIAL NOT NULL,
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
CREATE FUNCTION is_open(p_id integer, ts timestamp) RETURNS BOOLEAN AS $$
	DECLARE result BOOLEAN := FALSE;
	DECLARE number integer;
	BEGIN
	 SELECT COUNT(event_id) INTO number FROM pub NATURAL INNER JOIN temporal_event NATURAL INNER JOIN opened WHERE pub.pub_id = p_id AND (ts BETWEEN opened.start AND opened.end);
	 IF number != 0
	    THEN result := TRUE;
	 END IF;
	 SELECT COUNT(event_id) INTO number FROM pub NATURAL INNER JOIN temporal_event NATURAL INNER JOIN closed WHERE pub.pub_id = p_id AND (ts BETWEEN closed.start AND closed.end);
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
CREATE FUNCTION insert_periodic(event_id integer, state TEXT, start_date timestamp, end_date timestamp, end_periodic timestamp, periodic integer) RETURNS void AS $$
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
CREATE FUNCTION delete_periodic(e_id integer, start_date timestamp, end_date timestamp, end_periodic timestamp, periodic integer) RETURNS void AS $$
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

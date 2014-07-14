DROP TABLE closed;
DROP TABLE opened;
DROP TABLE temporal_event;
DROP TABLE pub;
DROP FUNCTION is_open(p_id bigint, ts timestamp with time zone);
DROP FUNCTION is_open(p_id bigint, startTs timestamp with time zone, stopTS timestamp with time zone);
DROP FUNCTION insert_periodic(event_id bigint, state TEXT, start_date timestamp with time zone, end_date timestamp with time zone, end_periodic timestamp with time zone, periodic integer);
DROP FUNCTION delete_periodic(event_id bigint, start_date timestamp with time zone, end_date timestamp with time zone, end_periodic timestamp with time zone, periodic integer);

/*
General information about a pub is stored in the table
times for opening and other are stored in the temporal_events table respectively in the opened or closed table
	column: pub_ref			the table pub references to the OSM reference id
	column: beer_price		it stores the cheapest beer price for 0.5L
	column: happy_hour		it stores if there exists a happy hour in this pub
*/
CREATE TABLE pub(
pub_ref BIGSERIAL NOT NULL,
beer_price FLOAT,
happy_hour BOOLEAN,
PRIMARY KEY (pub_ref)
);

/*
A temporal_event can reference several opened or closed times in the tables opened or closed
The idea is that a pub is always closed, except there is an entry in the opened table except 
there is an entry in the closed table.
	column: event_id		the primary key for events
	column: pub_ref			references the specific pub by the osm reference id
	column: name			stores the name of the temporal_event
	column: type			can be opening_hours, happy_ours, kitchen_hours etc.
	column: description		describes what thype of temporal_event is stored
	column: event			is it a special event or just an opnening_hour or s.e.
	column: entry_fee		stores the the costs of the entry
*/
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
	
/* 
General information for this closed event is stored in the referenced temporal_event
each entry represents a time were a referenced pub is closed
	column: closed_id		primary key of a closed event
	column: event_id		reference to a temporal_event
	column: start_time		start of the closed event
	column: end_time		end of the closed event
*/
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

/* 
General information for this opened event is stored in the referenced temporal_event
each entry represents a time were a referenced pub is opened
	column: opened_id		primary key of a opened event
	column: event_id		reference to a temporal_event
	column: start_time		start of the opened event
	column: end_time		end of the opened event
*/
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
	Param: p_id			bigint			OSM Pub ID of the pub
	Param: ts			TIMESTAMP		Checks if the pub is opened at this time
	Returns: true: pub is opened, false: pub is closed
*/
CREATE FUNCTION is_open(p_id bigint, ts timestamp with time zone) RETURNS BOOLEAN AS $$
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
Function determines if a certain pub is opened between two specified timestamps
	Param: p_id			bigint							OSM Pub ID of the pub
	Param: startTs		TIMESTAMP with time zone		Starttime of the interval which should be checked
	Param: stopTs		TIMESTAMP with time zone		Endtime of the interval which should be checked
	Returns: true: pub is opened, false: pub is closed
*/
CREATE FUNCTION is_open(p_id bigint, startTS timestamp with time zone, stopTS timestamp with time zone) RETURNS BOOLEAN AS $$
	DECLARE result BOOLEAN := FALSE;
	DECLARE number integer;
	BEGIN
	 SELECT COUNT(event_id) INTO number FROM pub NATURAL INNER JOIN temporal_event NATURAL INNER JOIN opened WHERE pub.pub_ref = p_id AND ((startTS BETWEEN opened.start_time AND opened.end_time) OR (stopTS BETWEEN opened.start_time AND opened.end_time) OR (startTS <= opened.start_time AND stopTS >= opened.end_time));
	 IF number != 0
	    THEN result := TRUE;
	 END IF;
	 SELECT COUNT(event_id) INTO number FROM pub NATURAL INNER JOIN temporal_event NATURAL INNER JOIN closed WHERE pub.pub_ref = p_id AND (startTS <= opened.start_time AND stopTS >= opened.end_time);
	 IF number != 0
		THEN result := FALSE;
	 END IF;
	 RETURN result;
	END;
$$ LANGUAGE 'plpgsql';

/*
Function creates multiple entries in the "closed" respectively the "opened"-table for reoccuring events like opening hours
	Param: event_id			bigint 		ID of the event for which the periodic opening/closed hours shall be created
	Param: state			TEXT 			can be 'opened' or 'closed', decides which table will be used
	Param: start_date		TIMESTAMP 		Start date of the first occurrence of opening hour/closed hour
	Param: end_date			TIMESTAMP		End date of the first occurrence of opening hour/closed hour
	Param: end_periodic		TIMESTAMP		Last occurrence of the periodic event
	Param: periodic			INTEGER			Timespan between end_periodic and next start of the event
*/
CREATE FUNCTION insert_periodic(event_id bigint, state TEXT, start_date timestamp with time zone, end_date timestamp with time zone, end_periodic timestamp with time zone, periodic integer) RETURNS void AS $$
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
	Param: e_id				bigint 		ID of the event for which the periodic opening/closed hours shall be deleted
	Param: start_date		TIMESTAMP 		Start date of the first occurrence of opening hour/closed hour
	Param: end_date			TIMESTAMP		End date of the first occurrence of opening hour/closed hour
	Param: end_periodic		TIMESTAMP		Last occurrence of the periodic event
	Param: periodic			INTEGER			Timespan between end_periodic and next start of the event
*/
CREATE FUNCTION delete_periodic(e_id bigint, start_date timestamp with time zone, end_date timestamp with time zone, end_periodic timestamp with time zone, periodic integer) RETURNS void AS $$
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

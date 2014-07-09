<?php
	$db_connection = pg_connect("") or die('Connection failed: ' . pg_last_error());
	
	$osm_api_link = 'http://api.openstreetmap.org/api/0.6/';
	
	// OSM node IDs of all bars and pubs of our system
	$osm_pub_ids = array(271428118,271428115,271428124,271428122,620079746,271428131,271428137,273815578,273815427,2949390449,1096717560,273815375,1327752993,273815498,273815545,560807730,439279333,295713917,672797694,271428120,87276323,859268756,1328494701,496151710,499657170,715332503,273815388,288390712,318497649,2385869893,1357132793,619104020,443482439,787844464,1056618250,616689799,1577263853,144016679,632705690,1402042236,1560864863,365561277,612986688,339129853,258745932,603568084,408993536,1278188406,1056653236,273815643,608541250,273815363,2128877773,2953735328,620607308);
	
	$week_days = array(
		'Mo' => 1,
		'Tu' => 2,
		'We' => 3,
		'Th' => 4,
		'Fr' => 5,
		'Sa' => 6,
		'Su' => 7,
		'Mon' => 1,
		'Tue' => 2,
		'Wed' => 3,
		'Thu' => 4,
		'Fri' => 5,
		'Sat' => 6,
		'Sun' => 7
	);
		
	$week_days_strings = array(
		1 => 'monday',
		2 => 'tuesday',
		3 => 'wednesday',
		4 => 'thursday',
		5 => 'friday',
		6 => 'saturday',
		7 => 'sunday'
	);
	
	// Iterate all OSM nodes from the array and parse opening hours
	foreach ( $osm_pub_ids as $osm_node_id ) {
		// read xml from the OSM API
		$osm_xml_node = file_get_contents($osm_api_link . 'node/' . $osm_node_id);
		
		// create SimpleXMLElement from XML code
		$xml_node = new SimpleXMLElement($osm_xml_node);
		
		$opening_hours = null;
		
		// iterate over all tags of the returned node and search for opening_hours
		foreach ( $xml_node->node[0] as $tag ) {
			$key = $tag['k'];
			$value = $tag['v'];
			
			if ( $key == 'opening_hours' ) {
				$opening_hours = (string) $value;
			}
		}
		
		if ( $opening_hours != null ) {
			$days = explode(';', $opening_hours);
			
			$open = array();
			
			foreach ( $days as $day ) {
				$times = explode(',', $day);
				$week_day = '';
				$opening_times = array();
				
				foreach($times as $time) {
					$time = trim($time);
					
					if ( $week_day == '' ) {
						if ( strpos($time, ' ') ) {
							$week_day = explode(' ', $time)[0];
							$times = explode(' ', $time)[1];
							
							if ( strpos($times, '-') ) {
								$start_open = explode('-', $times)[0];
								$end_open = explode('-', $times)[1];
								$opening_times[] = array($start_open, $end_open);
							}
							else if ( strpos($times, '+') ) {
								$times = str_replace('+', '', $times);
								$start_open = $times;
								$end_open = '03:00';
								$opening_times[] = array($start_open, $end_open);
							}
						}
					}
					else {
						if ( strpos($time, '-') ) {
							$start_open = explode('-', $time)[0];
							$end_open = explode('-', $time)[1];
							$opening_times[] = array($start_open, $end_open);
						}
						else if ( strpos($time, '+') ) {
							$times = str_replace('+', '', $time);
							$start_open = $time;
							$end_open = '03:00';
							$opening_times[] = array($start_open, $end_open);
						}
					}
				}
				
				if ( strpos($week_day, '-') ) {
					$first = $week_days[explode('-', $week_day)[0]];
					$second = $week_days[explode('-', $week_day)[1]];
					$current = $first;
					while ( $current <= $second ) {
						$open[$current] = $opening_times;
						$current++;
					}
				}
				else {
					if ( isset($week_days[$week_day]) ) {
						$number_week_day = $week_days[$week_day];
						$open[$number_week_day] = $opening_times;
					}
				}
			}
			
			$query = pg_query('SELECT event_id FROM temporal_event WHERE pub_ref = \'' . $osm_node_id . '\' AND type = \'opening_hours\'');
			while ( $row = pg_fetch_array($query) ) {
				pg_query('DELETE FROM opened WHERE event_id = '. $row['event_id'] .';');
			}
			
			$current_week_day = date('N');
			for ( $i = 1; $i <= 7; $i++ ) {
				if ( isset($open[$i]) ) {
					for ( $j = 0; $j < sizeof($open[$i]); $j++ ) {
						$row = pg_fetch_array(pg_query('SELECT event_id FROM temporal_event WHERE pub_ref = \'' . $osm_node_id . '\' AND type = \'opening_hours\''));
						$event_id = $row['event_id'];
						
						if ( $event_id == '' ) {
							pg_query("INSERT INTO temporal_event (pub_ref, name, type, description, event, entry_fee) VALUES ('" . $osm_node_id . "','opened','opening_hours','opening hours of the pub','false','0')");
							$row = pg_fetch_array(pg_query('SELECT event_id FROM temporal_event WHERE pub_ref = \'' . $osm_node_id . '\' AND type = \'opening_hours\''));
							$event_id = $row['event_id'];
						}
						
						$start_open_unix = strtotime('last ' . $week_days_strings[$i] . ' ' . $open[$i][$j][0]);
						$end_open_unix = strtotime('last ' . $week_days_strings[$i] . ' ' . $open[$i][$j][1]);
						if ( $start_open_unix > $end_open_unix ) {
							$end_open_unix += 24 * 60 * 60;
						}
						$start_open = date('Y-m-d H:i', $start_open_unix);
						$end_open = date('Y-m-d H:i', $end_open_unix);
						$end_periodic = date('Y-m-d', time() + 24 * 60 * 60 * 21);
						
						pg_query('SELECT insert_periodic('. $event_id .', \'opened\', \'' . $start_open . '+02\', \'' . $end_open . '+02\', \'' . $end_periodic . '+02\', 604800);');
					}
				}
			}
		}
	}
?>
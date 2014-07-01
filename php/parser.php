<?php
	$osm_api_link = 'http://api.openstreetmap.org/api/0.6/';
	
	// OSM node IDs of all bars and pubs of our system
	$osm_pub_ids = array(439279333, 271428122);
	
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
			print $opening_hours;
		}
	}
?>
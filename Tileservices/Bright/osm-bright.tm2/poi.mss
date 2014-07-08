// General overview about cart.css API: 
//https://www.mapbox.com/carto/api/2.3.0/
// General overview about mapbox-streets-v5
//https://www.mapbox.com/developers/vector-tiles/mapbox-streets/
// Styling lines:
//https://www.mapbox.com/tilemill/docs/guides/styling-lines/
// Styling Labels:
//https://www.mapbox.com/tilemill/docs/guides/styling-labels/
//https://www.mapbox.com/tilemill/docs/guides/labels-advanced/
// Advances map design:
//https://www.mapbox.com/tilemill/docs/guides/advanced-map-design/
// Maki Icons:
//https://www.mapbox.com/tilemill/docs/guides/using-maki-icons/


@name: '[name_de]';  

#poi_label[zoom>=16][maki='police'] {
  ::icon {
    marker-fill:#666;
    marker-file:url('icon/police-12.svg');
  }
  ::label[zoom>=18] {
    text-name: @name;
    text-face-name: @sans_md;
    text-size: 8;
    text-fill: #666;
    // Halo around label
    text-halo-fill: fadeout(#fff, 50%);
    text-halo-radius: 1;
    text-halo-rasterizer: fast;
    text-wrap-width: 70;
    text-line-spacing:	-1;
    text-transform: uppercase;
    text-character-spacing:	0.25;
    [maki!=null] { text-dy: 8; }
  } 
}


#poi_label[zoom>=16][maki='fast-food'] {
  ::icon {
    marker-fill:#666;
    marker-file:url('icon/fast-food-12.svg');
  }
  ::label[zoom>=18] {
    text-name: @name;
    text-face-name: @sans_md;
    text-size: 8;
    text-fill: #666;
    text-halo-fill: fadeout(#fff, 50%);
    text-halo-radius: 1;
    text-halo-rasterizer: fast;
    text-wrap-width: 70;
    text-line-spacing:	-1;
    text-transform: uppercase;
    text-character-spacing:	0.25;
    //POI labels with an icon need to be offset:
    [maki!=null] { text-dy: 8; }
  }
}


//#poi_label[zoom>=16][maki='bank'] {
//  ::icon {
//    marker-fill:#666;
//    marker-file:url('icon/bank-12.svg');
//  }
//  ::label {
//    text-name: @name;
//    text-face-name: @sans_md;
//    text-size: 8;
//    text-fill: #666;
//    text-halo-fill: fadeout(#fff, 50%);
//    text-halo-radius: 1;
//    text-halo-radius: 1;
//    text-halo-rasterizer: fast;
//    text-wrap-width: 70;
//    text-line-spacing:	-1;
//    text-transform: uppercase;
//    text-character-spacing:	0.25;
//    //POI labels with an icon need to be offset:
//    [maki!=null] { text-dy: 8; }
//  }
//}


#poi_label[zoom>=16][maki='hospital'] {
  ::icon {
    marker-fill:#F60808;
    marker-file:url('icon/hospital-12.svg');
  }
  ::label[zoom>=18] {
    text-name: @name;
    text-face-name: @sans_md;
    text-size: 8;
    text-fill: #666;
    text-halo-fill: fadeout(#fff, 50%);
    text-halo-radius: 1;
    text-halo-rasterizer: fast;
    text-wrap-width: 70;
    text-line-spacing:	-1;
    text-transform: uppercase;
    text-character-spacing:	0.25;
    //POI labels with an icon need to be offset:
    [maki!=null] { text-dy: 8; }
  }
}



#atms_banks[zoom>=16] {
  ::icon {
    marker-fill:#666;
    marker-file:url('icon/bank-12.svg');
    marker-spacing: 20;
    marker-allow-overlap: true;
    marker-ignore-placement: true;
  }
  ::label[zoom>=18] {
    text-name: [name];
    text-face-name: @sans_md;
    text-size: 8;
    text-fill: #666;
    text-allow-overlap: true;
    // Halo around label
    text-halo-fill: fadeout(#fff, 50%);
    text-halo-radius: 1;
    text-halo-rasterizer: fast;
    text-wrap-width: 70;
    text-line-spacing:	-1;
    text-transform: uppercase;
    text-allow-overlap:true;
    text-character-spacing:	0.25;
    text-placement-type: simple;
    text-placements: "N,S,E,W,NE,SE,NW,SW,16,14,12";
    text-dy: 9;
    text-dx: 9;
  }
}


#bus_stops[zoom>=16] {
  ::icon {
    marker-fill:#666;
    marker-file:url('icon/bus-12.svg');
    marker-spacing: 20;
    marker-allow-overlap: true;
    marker-ignore-placement: true;
  }
  ::label[zoom>=18] {
    text-name: [name];
    text-face-name: @sans_md;
    text-size: 8;
    text-fill: #666;
    text-allow-overlap: true;
    // Halo around label
    text-halo-fill: fadeout(#fff, 50%);
    text-halo-radius: 1;
    text-halo-rasterizer: fast;
    text-wrap-width: 70;
    text-line-spacing:	-1;
    text-transform: uppercase;
    text-allow-overlap:true;
    text-character-spacing:	0.25;
    text-placement-type: simple;
    text-placements: "N,S,E,W,NE,SE,NW,SW,16,14,12";
    text-dy: 9;
    text-dx: 9;
  } 
}




#housenum_label[zoom>=19] {
  text-name: [house_num];
  text-face-name: 'Open Sans Regular';
  text-fill: darken(#cde, 50%);
  text-size: 9;
  text-halo-fill: fadeout(#fff, 50%);
  text-halo-radius: 1;
  text-halo-rasterizer: fast;
  text-wrap-width: 70;
  text-line-spacing:	-1;
  text-transform: uppercase;
  text-character-spacing:	0.25;
}
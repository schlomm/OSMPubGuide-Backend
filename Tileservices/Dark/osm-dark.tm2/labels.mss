// =====================================================================
// LABELS

// General notes:
// - `text-halo-rasterizer: fast;` gives a noticeable performance
//    boost to render times and is recommended for *all* halos.

// ---------------------------------------------------------------------
// Languages

// There are 5 language options in the MapBox Streets vector tiles:
// - Local/default: '[name]'
// - English: '[name_en]'
// - French: '[name_fr]'
// - Spanish: '[name_es]'
// - German: '[name_de]'
@name: '[name_de]';  


// ---------------------------------------------------------------------
// Fonts

// All fontsets should have a good fallback that covers as many glyphs
// as possible. 'Arial Unicode MS Regular' and 'Arial Unicode MS Bold' 
//are recommended as final fallbacks if you have them available. 
//They support all the characters used in the MapBox Streets vector tiles.
@fallback: 'Open Sans Regular';
@sans: 'Open Sans Regular', @fallback;
@sans_md: 'Open Sans Semibold', @fallback;
@sans_bd: 'Open Sans Bold', @fallback;
@sans_it: 'Open Sans Italic', @fallback;


// ---------------------------------------------------------------------
// Countries

// The country labels in MapBox Streets vector tiles are placed by hand,
// optimizing the arrangement to fit as many as possible in densely-
// labeled areas.
#country_label[zoom>=3] {
  text-name: @name;
  text-face-name: @sans_bd;
  text-transform: uppercase;
  text-wrap-width: 100;
  text-wrap-before: true;
  text-fill: #D6D6D9;
  text-halo-fill: fadeout(#000000,80%);
  text-halo-radius: 2;
  text-halo-rasterizer: fast;
  text-line-spacing: -4;
  text-character-spacing: 0.5;
  text-size: 11;
  [zoom>=3][scalerank=1],
  [zoom>=4][scalerank=2],
  [zoom>=5][scalerank=3],
  [zoom>=6][scalerank>3] {
    text-size: 13;
  }
  [zoom>=4][scalerank=1],
  [zoom>=5][scalerank=2],
  [zoom>=6][scalerank=3],
  [zoom>=7][scalerank>3] {
    text-size: 15;
  }
}

#country_label_line {
  // Lines that connect offset labels to small
  // island & coastal countries at small scales.
  line-color: #334;
  line-opacity: 0.5;
}

// ---------------------------------------------------------------------
// Marine

#marine_label {
  text-name: @name;
  text-face-name: @sans_it;
  text-wrap-width: 60;
  text-wrap-before: true;
  text-fill: lighten(@water, 30);
  text-halo-fill: fadeout(#000, 75%);
  text-halo-radius: 1.5;
  text-size: 10;
  text-character-spacing: 1;
  // Some marine labels should be drawn along a line 
  // rather than on a point (the default)
  [placement='line'] {
    text-placement: line;
    text-avoid-edges: true;
  }
  // Oceans
  [labelrank=1] { 
    text-size: 18;
    text-wrap-width: 120;
    text-character-spacing:	4;
    text-line-spacing:	8;
  }
  [labelrank=2] {
    text-size: 14;
  }
  [labelrank=3] {
    text-size: 11;
  }
  [zoom>=5] {
    text-size: 12;
    [labelrank=1] {
      text-size: 22;
     }
    [labelrank=2] {
      text-size: 16;
     }
    [labelrank=3] {
      text-size: 14;
      text-character-spacing: 2;
     }
   }
}

// ---------------------------------------------------------------------
// Cities, towns, villages, etc

// City labels with dots for low zoom levels.
// The separate attachment keeps the size of the XML down.
#place_label::citydots[type='city'][zoom>=4][zoom<=7] {
  // explicitly defining all the `ldir` values wer'e going
  // to use shaves a bit off the final project.xml size
  [ldir='N'],[ldir='S'],[ldir='E'],[ldir='W'],
  [ldir='NE'],[ldir='SE'],[ldir='SW'],[ldir='NW'] {
    text-name: @name;
    text-size: 14;
    text-face-name: @sans;
    text-placement: point;
    text-fill: #6E6E6E;
    text-halo-fill: fadeout(#000000, 50%);
    text-halo-radius: 1;
    text-halo-rasterizer: fast;
    text-min-distance: 2;
    [ldir='E'] { text-dx: 5; }
    [ldir='W'] { text-dx: -5; }
    [ldir='N'] { text-dy: -5; }
    [ldir='S'] { text-dy: 5; }
    [ldir='NE'] { text-dx: 4; text-dy: -4; }
    [ldir='SE'] { text-dx: 4; text-dy: 4; }
    [ldir='SW'] { text-dx: -4; text-dy: 4; }
    [ldir='NW'] { text-dx: -4; text-dy: -4; }
    marker-width: 4;
    marker-fill: #333;
  }
}

#place_label {
  text-name: @name;
  text-face-name: @sans;
  text-wrap-width: 120;
  text-wrap-before: true;
  text-fill: #6E6E6E;
  text-halo-fill: fadeout(#000000, 50%);
  text-halo-radius: 2;
  text-halo-rasterizer: fast;
  text-size: 10;
  [type='city'][zoom>=8][zoom<=15] {
  	text-face-name: @sans_md;
    text-size: 16;
    [zoom>=10] { 
      text-size: 18;
      text-wrap-width: 140;
    }
    [zoom>=12] { 
      text-size: 24;
      text-wrap-width: 180;
    }
    // Hide at largest scales:
    [zoom>=16] { text-name: "''"; }
  }
  [type='town'] {
    text-size: 14;
    [zoom>=12] { text-size: 16; }
    [zoom>=14] { text-size: 20; }
    [zoom>=16] { text-size: 24; }
    // Hide at largest scales:
    [zoom>=18] { text-name: "''"; }
  }
  [type='village'] {
    text-size: 12;
    [zoom>=12] { text-size: 14; }
    [zoom>=14] { text-size: 18; }
    [zoom>=16] { text-size: 22; }
  }
  [type='hamlet'],
  [type='suburb'],
  [type='neighbourhood'] {
    text-fill: #A68B8B;
    text-face-name:	@sans_bd;
    text-transform: uppercase;
    text-character-spacing: 0.5;
    [zoom>=14] { text-size: 11; }
    [zoom>=15] { text-size: 12; text-character-spacing: 1; }
    [zoom>=16] { text-size: 14; text-character-spacing: 2; }
  }
}


// ---------------------------------------------------------------------
// Points of interest

//moved to poi.mss (see separate tab)


// ---------------------------------------------------------------------
// Roads

#road_label[reflen>=1][reflen<=6]::shield {
  // Motorways with a 'ref' tag that is 1-6 characters long have a
  // [ref] value for shield-style labels.
  // Custom shield png files can be created using make_shields.sh
  // in _src folder
  shield-name: [ref];
  shield-face-name: @sans_bd;
  shield-fill: #18A81F;
    // Distance between labels (those with shield as background)
  shield-min-distance: 110;
  shield-size: 8;
  shield-file: url('img/dark.png');
  [zoom>=15] {
    shield-size: 11;
    shield-file: url('img/dark.png');
  }
}

#road_label {
  // You need to use [name] for road labels if you want English street
  // prefixes and suffixes to be abbreviated. Translated labels do not
  // have abbreviations.
  text-name: [name];
  text-placement: line;  // text follows line path
  text-face-name: @sans;
  text-fill: #6E6E6E;
  text-halo-fill: fadeout(#000000, 20%);
  text-halo-radius: 2;
  text-halo-rasterizer: fast;
  text-min-distance: 150;
  text-size: 8;
  [zoom>=15] { text-size: 13; }
}


// ---------------------------------------------------------------------
// Water

#water_label {
  [zoom<=13],  // automatic area filtering @ low zooms
  [zoom>=14][area>500000],
  [zoom>=16][area>10000],
  [zoom>=17] {
    text-name: @name;
    text-face-name: @sans_it;
    text-fill: lighten(@water, 45);
    text-size: 12;
    text-wrap-width: 100;
    text-wrap-before: true;
    text-halo-fill: fadeout(#000000, 75%);
    text-halo-radius: 1.5;
  }
}
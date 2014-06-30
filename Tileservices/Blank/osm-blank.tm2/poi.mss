#poi_label[zoom>=15][maki='police'] {
  // Separate icon and label attachments are created to ensure that
  // all icon placement happens first, then labels are placed only
  // if there is still room.
  
  ::icon[maki='police'] {
    // The [maki] field values match a subset of Maki icon names, so we
    // can use that in our url expression.
    // Not all POIs have a Maki icon assigned, so we limit this section
    // to those that do. See also <https://www.mapbox.com/maki/>
    marker-fill:#666;
    marker-file:url('icon/police-12.svg');
  }
  
  ::label {
    text-name: @name;
    text-face-name: @sans_md;
    text-size: 10;
    text-fill: #f55;
    // text-transform: uppercase;
    // text-character-spacing:	0.25;
    // POI labels with an icon need to be offset:
    [maki='police'] { text-dy: 8; }
  }
}

#poi_label[zoom>=15][maki='restaurant'] {
::icon[maki='restaurant'] {
    // The [maki] field values match a subset of Maki icon names, so we
    // can use that in our url expression.
    // Not all POIs have a Maki icon assigned, so we limit this section
    // to those that do. See also <https://www.mapbox.com/maki/>
    marker-fill:#666;
    marker-file:url('icon/restaurant-12.svg');
  }

    ::label {
    text-name: @name;
    text-face-name: @sans_md;
    text-size: 10;
    text-fill: #f55;
    // text-transform: uppercase;
    // text-character-spacing:	0.25;
    // POI labels with an icon need to be offset:
    [maki='restaurant'] { text-dy: 8; }
  }
}
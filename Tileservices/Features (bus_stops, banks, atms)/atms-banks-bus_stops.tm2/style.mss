@fallback: 'Open Sans Regular';
@sans: 'Open Sans Regular', @fallback;
@sans_md: 'Open Sans Semibold', @fallback;
@sans_bd: 'Open Sans Bold', @fallback;
@sans_it: 'Open Sans Italic', @fallback;

@name: '[name_en]'; 


//Map {
//  background-color: #fff;
//}


#bus_stops[zoom>=1] {
  ::icon {
    marker-fill:#666;
    marker-file:url('icon/bus-12.svg');
  }
  ::label {
    text-name: [name];
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
  } 
}

#atms_banks[zoom>=1] {
  ::icon {
    marker-fill:#666;
    marker-file:url('icon/bank-12.svg');
  }
  ::label {
    text-name: [name];
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
  } 
}

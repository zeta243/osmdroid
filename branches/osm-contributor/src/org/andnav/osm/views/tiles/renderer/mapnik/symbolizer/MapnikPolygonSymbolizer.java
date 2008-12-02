package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import android.graphics.Color;

// Original from include/mapnik/polygon_symbolizer.hpp

public class MapnikPolygonSymbolizer extends MapnikSymbolizer {

	public MapnikPolygonSymbolizer()
	{
		super();
		mPaint.setColor(Color.argb(255, 128, 128, 128));
	}

}

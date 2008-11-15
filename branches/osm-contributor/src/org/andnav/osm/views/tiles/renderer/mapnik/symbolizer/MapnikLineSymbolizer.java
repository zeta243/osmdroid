package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikColour;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikStroke;

// Original from include/mapnik/line_symbolizer.hpp

public class MapnikLineSymbolizer extends MapnikSymbolizer {

	private MapnikStroke mStroke;

	public MapnikLineSymbolizer() {
		mStroke = new MapnikStroke();
	}

	public MapnikLineSymbolizer(MapnikStroke stroke) {
		mStroke = stroke;
	}

	public MapnikLineSymbolizer(MapnikColour pen, float width) {
		mStroke = new MapnikStroke(pen, width);
	}

	public MapnikStroke getStroke() {
		return mStroke;
	}

	public void setStroke(MapnikStroke stroke) {
		mStroke = stroke;
	}
}

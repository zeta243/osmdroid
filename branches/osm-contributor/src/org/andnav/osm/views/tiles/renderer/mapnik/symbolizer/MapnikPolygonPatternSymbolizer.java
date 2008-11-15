package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import java.io.FileNotFoundException;
import java.io.IOException;

// Original from include/mapnik/polygon_pattern_symbolizer.hpp

public class MapnikPolygonPatternSymbolizer extends MapnikSymbolizerWithImage {

	public MapnikPolygonPatternSymbolizer(String file, String type, int width, int height) throws FileNotFoundException, IOException
	{
		super(file, type, width, height);
	}
	
	public MapnikPolygonPatternSymbolizer(MapnikPolygonPatternSymbolizer s)
	{
		super(s);
	}
}

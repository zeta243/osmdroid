package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import java.io.FileNotFoundException;
import java.io.IOException;

// Original from include/mapnik/line_pattern_symbolizer.hpp

public class MapnikLinePatternSymbolizer extends MapnikSymbolizerWithImage{

	public MapnikLinePatternSymbolizer(String filename, String type,
			int width, int height) throws IOException, FileNotFoundException {
		super(filename, type, width, height);
	}

	public MapnikLinePatternSymbolizer(MapnikLinePatternSymbolizer s)
	{
		super(s);
	}
}

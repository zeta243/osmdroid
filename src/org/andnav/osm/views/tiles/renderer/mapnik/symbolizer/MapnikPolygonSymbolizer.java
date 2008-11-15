package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikColour;

// Original from include/mapnik/polygon_symbolizer.hpp

public class MapnikPolygonSymbolizer extends MapnikSymbolizer {

	private MapnikColour mFill;
	private float mOpacity;
	
	public MapnikPolygonSymbolizer()
	{
		mFill = new MapnikColour(128,128,128);
		mOpacity = 1;
	}
	
	public MapnikPolygonSymbolizer(MapnikColour c)
	{
		mFill = c;
		mOpacity = 1;
	}
	
	public MapnikColour getFill()
	{
		return mFill;
	}
	
	public void setFill(MapnikColour c)
	{
		mFill = c;
	}

	public float getOpacity() {
		return mOpacity;
	}

	public void setOpacity(float opacity) {
		mOpacity = opacity;
	}
}

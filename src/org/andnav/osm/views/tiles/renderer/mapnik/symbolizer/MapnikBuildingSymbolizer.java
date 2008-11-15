package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikColour;

// Original from include/mapnik/polygon_symbolizer.hpp

public class MapnikBuildingSymbolizer extends MapnikSymbolizer {

    MapnikColour mFill;
    double mHeight;
    float mOpacity;
	
	public MapnikBuildingSymbolizer()
	{
		mFill = new MapnikColour(128, 128, 128);
		mHeight = 0;
		mOpacity = 1;
	}
	
	public MapnikBuildingSymbolizer(MapnikColour fill, double height)
	{
		mFill = fill;
		mHeight = height;
		mOpacity = 1;
	}

	public MapnikColour getFill() {
		return mFill;
	}

	public void setFill(MapnikColour fill) {
		mFill = fill;
	}

	public double getHeight() {
		return mHeight;
	}

	public void setHeight(double height) {
		mHeight = height;
	}

	public float getOpacity() {
		return mOpacity;
	}

	public void setOpacity(float opacity) {
		mOpacity = opacity;
	}
}

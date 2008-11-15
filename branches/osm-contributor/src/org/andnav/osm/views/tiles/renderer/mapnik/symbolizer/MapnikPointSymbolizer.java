package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikImageData;

// Original from include/mapnik/point_symbolizer

public class MapnikPointSymbolizer extends MapnikSymbolizerWithImage {
	
	private boolean mOverlap;
	
	public MapnikPointSymbolizer()
	{
		super(new MapnikImageData(4,4));
		mOverlap = false;
		//default point symbol is black 4x4px square
		this.mImage.set(0xff000000);
	}
	
	public MapnikPointSymbolizer(String file, String type, int width, int height) throws FileNotFoundException, IOException
	{
		super(file, type, width, height);
		mOverlap = false;
	}
	
	public MapnikPointSymbolizer(MapnikPointSymbolizer s)
	{
		super(s);
		mOverlap = s.mOverlap;
	}

	public boolean getAllowOverlap() {
		return mOverlap;
	}

	public void setAllowOverlap(boolean overlap) {
		mOverlap = overlap;
	}
}

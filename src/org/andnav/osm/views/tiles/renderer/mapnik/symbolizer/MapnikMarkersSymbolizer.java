package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikCoordTransformer;

import android.graphics.Canvas;

// Original from include/mapnik/markers_symbolizer.hpp

public class MapnikMarkersSymbolizer extends MapnikSymbolizer {

	private boolean mAllowOverlap;
	
	MapnikMarkersSymbolizer()
	{
		mAllowOverlap = false;
	}

	@Override
	public void draw(Canvas canvas, MapnikCoordTransformer transformer,
			MapnikFeature feature) throws Exception {
		throw new Exception("MapnikMarkerSymbolizer is not implemented");
		
	}

}

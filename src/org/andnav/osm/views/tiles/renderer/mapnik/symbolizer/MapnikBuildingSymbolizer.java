package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikCoordTransformer;

import android.graphics.Canvas;
import android.graphics.Paint.Style;

// Original from include/mapnik/polygon_symbolizer.hpp

public class MapnikBuildingSymbolizer extends MapnikSymbolizer {

    double mHeight;
	
	public MapnikBuildingSymbolizer()
	{
		super();
		mPaint.setStyle(Style.FILL);
		mPaint.setARGB(255, 128, 128, 128);
		mHeight = 0;
	}
	
	public MapnikBuildingSymbolizer(int fill, double height)
	{
		super();
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(fill);		
		mHeight = height;
	}

	public double getHeight() {
		return mHeight;
	}

	public void setHeight(double height) {
		mHeight = height;
	}

	@Override
	public void draw(Canvas canvas, MapnikCoordTransformer transformer,
			MapnikFeature feature) throws Exception {
		// TODO We dont use any building symbolizer - so its not implemented yet.
		throw new Exception("Building Symbolizer is not implemented");
		
	}
}

package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import android.graphics.Paint;

// Original from include/mapnik/line_symbolizer.hpp

public class MapnikLineSymbolizer extends MapnikSymbolizer {

	// private Paint mPaint;

	public MapnikLineSymbolizer() {
		super();
	}

	public MapnikLineSymbolizer(Paint paint) {
		mPaint = paint;
	}

	public MapnikLineSymbolizer(int colour, float width) {
		super();
		mPaint.setColor(colour);
		mPaint.setStrokeWidth(width);
	}
}

package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikCoordTransformer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;

// Original from include/mapnik/polygon_pattern_symbolizer.hpp

public class MapnikPolygonPatternSymbolizer extends MapnikSymbolizerWithImage {

	public MapnikPolygonPatternSymbolizer(String file) throws FileNotFoundException, IOException
	{
		super(file);
	}
	
	public MapnikPolygonPatternSymbolizer(MapnikPolygonPatternSymbolizer s)
	{
		super(s);
	}
	
	public void draw(Canvas canvas, MapnikCoordTransformer transformer,
			MapnikFeature feature) throws Exception
	{
		Bitmap bitmap = this.getImage();
		Path path = this.getPath(feature, transformer);
		
	    Matrix matrix = new Matrix();
	    PathMeasure meas = new PathMeasure(path, true);
	    final float length = meas.getLength();
	    final float advance = bitmap.getWidth();    // might be larger to have space between each bitmap
	    float distance = 0;
	    
	    final int flags = PathMeasure.POSITION_MATRIX_FLAG | PathMeasure.TANGENT_MATRIX_FLAG;

	    while (distance < length) {
	        meas.getMatrix(distance, matrix, flags);
	        canvas.drawBitmap(bitmap, matrix, mPaint);
	        distance += advance;
	    }
	}
}

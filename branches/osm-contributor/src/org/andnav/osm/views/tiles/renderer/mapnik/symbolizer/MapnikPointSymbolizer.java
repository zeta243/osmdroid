package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikImageData;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikCoordTransformer;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikGeometry;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex.VertexCommandType;

import android.graphics.Canvas;
import android.graphics.Path;

// Original from include/mapnik/point_symbolizer

public class MapnikPointSymbolizer extends MapnikSymbolizerWithImage {
	
	private boolean mOverlap;
	
	public MapnikPointSymbolizer()
	{
		super(4,4);
		mOverlap = false;
	}
	
	public MapnikPointSymbolizer(String file) throws FileNotFoundException, IOException
	{
		super(file);
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
	
	@Override
	public void draw(Canvas canvas, MapnikCoordTransformer transformer,
			MapnikFeature feature) throws Exception {
		
		Vector<MapnikGeometry> geoms = feature.getGeometries();
		double[] coords = new double[2];
		
		for (MapnikGeometry g : geoms)
		{
			MapnikVertex v = g.getNextVertex();
			if (v != null)
			{	
				coords[0] = v.x;
				coords[1] = v.y;
				transformer.forward(coords);
				canvas.drawBitmap(mImage, (float)coords[0], (float)coords[1], mPaint);
			}
		}
	}
}

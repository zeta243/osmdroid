package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikCoordTransformer;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikGeometry;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex.VertexCommandType;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public abstract class MapnikSymbolizer {
	
	protected Paint mPaint;
	
	public MapnikSymbolizer()
	{
		mPaint = new Paint();
	}
	
	public MapnikSymbolizer(Paint paint)
	{
		mPaint = paint;
	}
	
	public Paint getPaint()
	{
		return mPaint;
	}
	
	public void setPaint(Paint p)
	{
		mPaint = p;
	}
	
	public abstract void draw(Canvas canvas, MapnikCoordTransformer transformer,
			MapnikFeature feature) throws Exception;
		
	
	protected Path getPath(MapnikFeature feature, MapnikCoordTransformer transformer)
	{
		Vector<MapnikGeometry> geoms = feature.getGeometries();
		Path path = new Path();
		double[] coords = new double[2];
		
		for (MapnikGeometry g : geoms)
		{
			if (g.numPoints() > 2)
			{
				MapnikVertex v = null;
				
				while ((v = g.getNextVertex()) != null)
				{
					coords[0] = v.x;
					coords[1] = v.y;
					transformer.forward(coords);
					
					if (v.mCmd == VertexCommandType.SEG_LINETO)
						path.lineTo((float)coords[0], (float)coords[1]);
					
					else if (v.mCmd == VertexCommandType.SEG_MOVETO)
						path.moveTo((float)coords[0], (float)coords[1]);
					
					else if (v.mCmd == VertexCommandType.SEG_CLOSE)
						path.setLastPoint((float)coords[0], (float)coords[1]);
				}
			}
		}
		return path;
	}
}

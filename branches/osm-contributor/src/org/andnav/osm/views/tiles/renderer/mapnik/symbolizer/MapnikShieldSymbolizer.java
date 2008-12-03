package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikCoordTransformer;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikGeometry;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex.VertexCommandType;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;

// Original from include/mapnik/shield_symbolizer.hpp

public class MapnikShieldSymbolizer extends MapnikTextSymbolizer implements MapnikSymbolizerWithImageInterface {

	MapnikSymbolizerWithImageInterface mSymbolizerImage;
	
	public MapnikShieldSymbolizer(String name, String faceName, int size,
								int fill,
			                      String file,
			                      String type,
			                      int width, int height) throws FileNotFoundException, IOException
	{
		super(name, faceName, size, fill);
		mSymbolizerImage = new MapnikSymbolizerWithImage(file);
	}

	@Override
	public String getFilename() {
		return mSymbolizerImage.getFilename();
	}

	@Override
	public Bitmap getImage() {
		return mSymbolizerImage.getImage();
	}

	@Override
	public void setImage(Bitmap image) {
		mSymbolizerImage.setImage(image);
	}
	
	public void draw(Canvas canvas, MapnikCoordTransformer transformer,
			MapnikFeature feature) throws Exception {
		Bitmap b = getImage().copy(getImage().getConfig(), true);
		Canvas c = new Canvas(b);
		
		// Write the name of this symbol into the canvas 'c' - then write
		// its bitmap into the main canvas 'canvas'
		
		String propertyValue = feature.getProperties().get(this.mName).toString();
		
		c.drawText(propertyValue, b.getWidth() / 2, b.getHeight() / 2, mPaint);
		
		Vector<MapnikGeometry> geoms = feature.getGeometries();
		
		float characterWidths[] = new float[propertyValue.length()];
		mPaint.getTextWidths(propertyValue, characterWidths);
		
		float totalWidth = 0;
        for (float n : characterWidths)
        	totalWidth += n;

		for (MapnikGeometry g : geoms)
		{
			if (g.numPoints() > 2)
			{
				double coords[] = new double[2];
				double dx = 0, dy = 0, old_x = 0, old_y = 0, distanceSinceLastPoint = 0, distanceSinceLastShield = 0;
				boolean first = true;
				MapnikVertex v = null;
				while ((v = g.getNextVertex()) != null)
				{
					coords[0] = v.x;
					coords[1] = v.y;
					transformer.forward(coords);

					if (v.mCmd == VertexCommandType.SEG_LINETO && distanceSinceLastShield > this.getMinumumDistance())
					{
						canvas.drawBitmap(b, (float)coords[0] - (b.getWidth() / 2), (float)coords[1] - (b.getHeight() / 2), mPaint);
						distanceSinceLastShield = 0;
					}
					
					if (!first && v.mCmd == VertexCommandType.SEG_LINETO)
					{
						dx = old_x - coords[0];
						dy = old_y - coords[1];
						distanceSinceLastPoint = (Math.sqrt(dx*dx + dy*dy));
						distanceSinceLastShield += distanceSinceLastPoint;
					}

					first = false;
					old_x = coords[0];
					old_y = coords[1];
				}
			}
		}
	}
}

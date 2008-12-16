package org.andnav.osm.views.tiles.renderer.mapnik.renderer;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikLayer;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikMap;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikCoordTransformer;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikSymbolizer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class MapnikFeatureRenderer extends MapnikFeatureStyleProcessor {

	private static final String TAG = "MapnikFeatureStyleProcessor";
	
	private int mWidth;
	private int mHeight;
	private MapnikCoordTransformer mTransformer;
	
	private Canvas mCanvas;
	
	public MapnikFeatureRenderer(MapnikMap map, Bitmap bitmap, int offsetX, int offsetY) {
		super(map);
		mWidth = bitmap.getWidth();
		mHeight = bitmap.getHeight();
		
		mTransformer = new MapnikCoordTransformer(map.getMapWidth(), map.getMapHeight(), map.getCurrentExtent(), offsetX, offsetY);
		
		mCanvas = new Canvas(bitmap);
		mCanvas.drawColor(map.getBackgroundColour());
	}

	@Override
	public void process(MapnikSymbolizer symbol, MapnikFeature feature) throws Exception {
		// Draw the feature according to the symbolizer
		symbol.draw(mCanvas, mTransformer, feature);
	}

	@Override
	public void startMapProcessing(MapnikMap map) {
		mCanvas.clipRect(0, 0, mWidth, mHeight);
	}
	
	@Override
	public void stopMapProcessing(MapnikMap map) {
		// Do Nothing
	}

	@Override
	public void startLayerProcessing(MapnikLayer layer) {
		Log.d(TAG, "Processing Layer: " + layer.getName());
		if (layer.isClearLabelCache())
		{
			// TODO: Once the collision detector is implemented, clear it here.
		}
	}
	
	@Override
	public void stopLayerProcessing(MapnikLayer map) {
		// Do Nothing
	}

}

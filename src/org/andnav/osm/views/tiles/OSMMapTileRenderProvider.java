// Created by plusminus on 21:31:36 - 25.09.2008
package org.andnav.osm.views.tiles;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.andnav.osm.util.StreamUtils;
import org.andnav.osm.util.constants.OSMConstants;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikEnvelope;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikMap;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikMapParser;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikMapParser.MapnikInvalidXMLException;
import org.andnav.osm.views.tiles.renderer.mapnik.renderer.MapnikFeatureRenderer;
import org.andnav.osm.views.util.Util;
import org.andnav.osm.views.util.constants.OSMMapViewConstants;
import org.xmlpull.v1.XmlPullParserException;

import org.andnav.osm.R;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OSMMapTileRenderProvider extends OSMAbstractMapTileProvider implements OSMConstants, OSMMapViewConstants {

	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final String TAG = "OSMMapTileRenderProvider";

	// ===========================================================
	// Fields
	// ===========================================================
	private MapnikMap mMap;


	// ===========================================================
	// Constructors
	// ===========================================================
	
	public OSMMapTileRenderProvider(Context ctx, OSMMapTileProviderInfo rendererInfo, OSMMapTileFilesystemCache mapTileFSProvider) {
		super(ctx, rendererInfo, mapTileFSProvider);
		
		mMap = new MapnikMap();
		MapnikMapParser parser = new MapnikMapParser();

		try {
			parser.parseMap(mMap, ctx.getResources().getXml(R.xml.default_map));
		} catch (NotFoundException e) {
			Log.e(TAG, e.toString());
		} catch (XmlPullParserException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} catch (MapnikInvalidXMLException e) {
			Log.e(TAG, e.toString());
		}
		Log.d(TAG, "Map Object Initialised");
		
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public boolean requestMapTileAsync(int[] coords, int zoomLevel, final Handler callback) {
		final String aURLString = mProviderInfo.getTileURLString(coords, zoomLevel);
		if(this.mPending.contains(aURLString))
			return false;
	
		this.mPending.add(aURLString);
		generateImageAsync(coords, zoomLevel, aURLString, callback);
		
		return true;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/** Sets the Child-ImageView of this to the URL passed. */
	public void generateImageAsync(final int[] coords, final int zoomLevel, final String aURLString, final Handler callback) {
		this.mThreadPool.execute(new Runnable(){
			@Override
			public void run() {
				InputStream in = null;
				OutputStream out = null;

				try {

					Log.i(DEBUGTAG, "Downloading Maptile from url: " + aURLString);

					Bitmap tile = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
					
					MapnikEnvelope box = Util.getMapnikEnvelopeFromMapTile(coords, zoomLevel);
					mMap.zoomToBox(box);
					
					MapnikFeatureRenderer renderer = new MapnikFeatureRenderer(mMap, tile, 0, 0);
					
					renderer.apply();
					
					final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
					out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);
					
					tile.compress(Bitmap.CompressFormat.PNG, 0, out);
					out.flush();

					final byte[] data = dataStream.toByteArray();

					OSMMapTileRenderProvider.this.mMapTileFSCache.saveFile(aURLString, data);

					Log.i(DEBUGTAG, "Maptile saved to: " + aURLString);

					final Message successMessage = Message.obtain(callback, MAPTILEPROVIDER_SUCCESS_ID);
					successMessage.sendToTarget();
					OSMMapTileRenderProvider.this.mPending.remove(aURLString);
				} catch (Exception e) {
					final Message failMessage = Message.obtain(callback, MAPTILEPROVIDER_FAIL_ID);
					failMessage.sendToTarget();

				    Log.e(DEBUGTAG, "Error Downloading MapTile. Exception: " + e.getClass().getSimpleName(), e);
					/* TODO What to do when downloading tile caused an error?
					 * Also remove it from the mPending?
					 * Doing not blocks it for the whole existence of this TileDownloder.
					 */
				} finally {
					StreamUtils.closeStream(in);
					StreamUtils.closeStream(out);
				}
			}
		});
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

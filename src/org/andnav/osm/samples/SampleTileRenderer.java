package org.andnav.osm.samples;

import java.io.IOException;

import org.andnav.osm.R;
import org.andnav.osm.util.constants.OSMConstants;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikEnvelope;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikMap;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikMapParser;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikMapParser.MapnikInvalidXMLException;
import org.andnav.osm.views.tiles.renderer.mapnik.renderer.MapnikFeatureRenderer;
import org.andnav.osm.views.util.Util;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class SampleTileRenderer extends Activity implements OSMConstants {
	
	private static final String TAG = "SampleTileFetcher";
	
	private EditText mZoomLevelText;
	private EditText mXText;
	private EditText mYText;
	private Button mSubmitButton;
	private ImageView mImageView;
	
	private MapnikMap mMap;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.tile_fetcher);
		
		mZoomLevelText = (EditText)findViewById(R.id.zoomBox);
		mXText = (EditText)findViewById(R.id.X);
		mYText = (EditText)findViewById(R.id.Y);
		
		// Tile for part of Arborfield
		mZoomLevelText.setText("17");
		mXText.setText("43654");
		mYText.setText("65207");

		mSubmitButton = (Button)findViewById(R.id.submitButton);
		mImageView = (ImageView)findViewById(R.id.ImageView01);
		
		mMap = new MapnikMap();
		MapnikMapParser parser = new MapnikMapParser();

		try {
			parser.parseMap(mMap, getResources().getXml(R.xml.default_map));
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
		
		mSubmitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int coords[] = new int[2];
				coords[0] = Integer.parseInt(mXText.getText().toString());
				coords[1] = Integer.parseInt(mYText.getText().toString());
				SampleTileRenderer.this.getTile(coords, Integer.parseInt(mZoomLevelText.getText().toString()));
			}
        });
	}
	
	public void getTile(final int[] coords, final int zoomLevel)
	{
		// BufferedOutputStream out;
		try {

			Bitmap tile = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

			MapnikEnvelope box = Util.getMapnikEnvelopeFromMapTile(coords, zoomLevel);
			mMap.zoomToBox(box);

			MapnikFeatureRenderer renderer = new MapnikFeatureRenderer(mMap, tile, 0, 0);

			renderer.apply();
			
			mImageView.setImageBitmap(tile);

			// final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			// out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);

			// tile.compress(Bitmap.CompressFormat.PNG, 0, out);
			// out.flush();

			// final byte[] data = dataStream.toByteArray();

		} catch (Exception e) {
			// final Message failMessage = Message.obtain(callback, MAPTILEPROVIDER_FAIL_ID);
			// failMessage.sendToTarget();

			Log.e(DEBUGTAG, "Error Downloading MapTile. Exception: " + e.getClass().getSimpleName(), e);
			/* TODO What to do when downloading tile caused an error?
			 * Also remove it from the mPending?
			 * Doing not blocks it for the whole existence of this TileDownloder.
			 */
		} finally {
			// StreamUtils.closeStream(out);
		}
	
	}
}

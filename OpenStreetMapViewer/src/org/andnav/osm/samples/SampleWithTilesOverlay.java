package org.andnav.osm.samples;

import org.andnav.osm.tileprovider.OpenStreetMapTileProviderDirect;
import org.andnav.osm.tileprovider.renderer.OpenStreetMapRendererFactory;
import org.andnav.osm.tileprovider.util.CloudmadeUtil;
import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.overlay.OpenStreetMapTilesOverlay;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 *
 * @author Alex van der Linden
 *
 */
public class SampleWithTilesOverlay extends Activity {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private OpenStreetMapView mOsmv;
	private OpenStreetMapTilesOverlay mTilesOverlay;
	private OpenStreetMapTileProviderDirect mProvider;

	// ===========================================================
	// Constructors
	// ===========================================================
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup base map
		final RelativeLayout rl = new RelativeLayout(this);

		final String cloudmadeKey = CloudmadeUtil
				.getCloudmadeKey(getApplicationContext());
		OpenStreetMapRendererFactory.setCloudmadeKey(cloudmadeKey);

		this.mOsmv = new OpenStreetMapView(this, 256);
		rl.addView(this.mOsmv, new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.mOsmv.setBuiltInZoomControls(true);

		// zoom to the netherlands
		this.mOsmv.getController().setZoom(7);
		this.mOsmv.getController().setCenter(new GeoPoint(51500000, 5400000));

		// Add tiles layer
		mProvider = new OpenStreetMapTileProviderDirect(getApplicationContext());
		mProvider
				.setPreferredRenderer(OpenStreetMapRendererFactory.FIETS_OVERLAY_NL);
		this.mTilesOverlay = new OpenStreetMapTilesOverlay(this.mOsmv,
				mProvider, this.getBaseContext());
		this.mOsmv.getOverlays().add(this.mTilesOverlay);

		this.setContentView(rl);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

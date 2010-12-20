package org.andnav.osm.services;

import org.andnav.osm.services.constants.OpenStreetMapServiceConstants;
import org.andnav.osm.tileprovider.IOpenStreetMapTileProviderCallback;
import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.tileprovider.OpenStreetMapTileProviderDirect;
import org.andnav.osm.tileprovider.OpenStreetMapTileRequestState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * The OpenStreetMapTileProviderService can download map tiles from a server and
 * stores them in a file system cache.
 *
 * @author Manuel Stahl
 */
public class OpenStreetMapTileProviderService extends Service implements
		OpenStreetMapServiceConstants, IOpenStreetMapTileProviderCallback {

        private static final Logger logger = LoggerFactory.getLogger(OpenStreetMapTileProviderService.class);

	private IOpenStreetMapTileProviderServiceCallback mCallback;

	private OpenStreetMapTileProviderDirect mTileProvider;

	@Override
	public void onCreate() {
		super.onCreate();
		mTileProvider = new OpenStreetMapTileProviderDirect(getApplicationContext());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onConfigurationChanged(Configuration pNewConfig) {
		if (DEBUGMODE)
			logger.debug("onConfigurationChanged");
		super.onConfigurationChanged(pNewConfig);
	}

	@Override
	public void onDestroy() {
		if (DEBUGMODE)
			logger.debug( "onDestroy");
		mTileProvider.detach();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		if (DEBUGMODE)
			logger.debug( "onLowMemory");
		super.onLowMemory();
	}

	@Override
	public void onRebind(Intent pIntent) {
		if (DEBUGMODE)
			logger.debug( "onRebind");
		super.onRebind(pIntent);
	}

	@Override
	public void onStart(Intent pIntent, int pStartId) {
		if (DEBUGMODE)
			logger.debug( "onStart");
		super.onStart(pIntent, pStartId);
	}

	@Override
	public boolean onUnbind(Intent pIntent) {
		if (DEBUGMODE)
			logger.debug( "onUnbind");
		return super.onUnbind(pIntent);
	}

	@Override
	public void mapTileRequestCompleted(
			final OpenStreetMapTileRequestState pState,
			final Drawable pDrawable) {
		// TODO implementation
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void mapTileRequestCandidate(
			final OpenStreetMapTileRequestState pState,
			final Drawable pDrawable) {
		// TODO implementation
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void mapTileRequestFailed(final OpenStreetMapTileRequestState pState) {
		// TODO implementation
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public boolean useDataConnection() {
		// TODO implementation
		return true;
	}

	/**
	 * The IRemoteInterface is defined through IDL
	 */
	private final IOpenStreetMapTileProviderService.Stub mBinder = new IOpenStreetMapTileProviderService.Stub() {
		@Override
		public void setCallback(
				final IOpenStreetMapTileProviderServiceCallback pCallback)
				throws RemoteException {
			mCallback = pCallback;
		}

		@Override
		public void requestMapTile(String rendererName, int zoomLevel,
				int tileX, int tileY) throws RemoteException {
			OpenStreetMapTile tile = new OpenStreetMapTile(zoomLevel,
					tileX, tileY);
			mTileProvider.getMapTile(tile);
		}
	};

}

package org.andnav.osm.views.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.andnav.osm.tileprovider.IOpenStreetMapTileProviderCallback;
import org.andnav.osm.tileprovider.IRegisterReceiver;
import org.andnav.osm.tileprovider.OpenStreetMapAsyncTileProvider;
import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.tileprovider.OpenStreetMapTileRequestState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.drawable.Drawable;
import android.os.Handler;

/**
 * Objects of this class provide access to tiles which are amenable to
 * synchronous access. They are expected to return quickly enough so that a
 * person will perceive it as instantaneous.
 * 
 * At present the only source which meets this criteria is the file system.
 */
public class OpenStreetMapTileProviderArray extends OpenStreetMapTileProvider
		implements IOpenStreetMapTileProviderCallback {

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileProviderArray.class);

	protected final List<OpenStreetMapAsyncTileProvider> mTileProviderList;

	public OpenStreetMapTileProviderArray(
			final Handler pDownloadFinishedListener,
			final IRegisterReceiver aRegisterReceiver,
			final OpenStreetMapAsyncTileProvider[] tileProviderArray) {
		super(pDownloadFinishedListener);
		mTileProviderList = new ArrayList<OpenStreetMapAsyncTileProvider>();
		Collections.addAll(mTileProviderList, tileProviderArray);
	}

	@Override
	public void detach() {
		synchronized (mTileProviderList) {
			for (OpenStreetMapAsyncTileProvider tileProvider : mTileProviderList) {
				tileProvider.stopWorkers();
				// TODO: Call detach?
			}
		}
	}

	@Override
	public Drawable getMapTile(final OpenStreetMapTile pTile) {
		if (mTileCache.containsTile(pTile)) {
			if (DEBUGMODE)
				logger.debug("MapTileCache succeeded for: " + pTile);
			return mTileCache.getMapTile(pTile);
		} else {
			if (DEBUGMODE)
				logger.debug("Cache failed, trying from FS: " + pTile);

			OpenStreetMapTileRequestState state;
			synchronized (mTileProviderList) {
				OpenStreetMapAsyncTileProvider[] providerArray = new OpenStreetMapAsyncTileProvider[mTileProviderList
						.size()];
				state = new OpenStreetMapTileRequestState(pTile,
						mTileProviderList.toArray(providerArray), this);
			}

			OpenStreetMapAsyncTileProvider provider = state.getNextProvider();
			if (provider != null) {
				provider.loadMapTileAsync(state);
			} else
				mapTileRequestFailed(state);
			return null;
		}
	}
}

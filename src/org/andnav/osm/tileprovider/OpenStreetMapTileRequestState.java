package org.andnav.osm.tileprovider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class OpenStreetMapTileRequestState {

	final Queue<OpenStreetMapAsyncTileProvider> mProviderQueue;
	final OpenStreetMapTile mMapTile;
	final IOpenStreetMapTileProviderCallback mCallback;

	public OpenStreetMapTileRequestState(OpenStreetMapTile mapTile,
			OpenStreetMapAsyncTileProvider[] providers,
			IOpenStreetMapTileProviderCallback callback) {
		mProviderQueue = new LinkedList<OpenStreetMapAsyncTileProvider>();
		Collections.addAll(mProviderQueue, providers);
		mMapTile = mapTile;
		mCallback = callback;
	}

	public OpenStreetMapTile getMapTile() {
		return mMapTile;
	}

	public IOpenStreetMapTileProviderCallback getCallback() {
		return mCallback;
	}

	public boolean isEmpty() {
		return mProviderQueue.isEmpty();
	}

	public OpenStreetMapAsyncTileProvider getNextProvider() {
		return mProviderQueue.poll();
	}
}

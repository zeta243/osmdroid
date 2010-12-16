package org.andnav.osm.tileprovider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import org.andnav.osm.tileprovider.modules.OpenStreetMapTileModuleProviderBase;

public class OpenStreetMapTileRequestState {

	final Queue<OpenStreetMapTileModuleProviderBase> mProviderQueue;
	final OpenStreetMapTile mMapTile;
	final IOpenStreetMapTileProviderCallback mCallback;
	OpenStreetMapTileModuleProviderBase mCurrentProvider;

	public OpenStreetMapTileRequestState(OpenStreetMapTile mapTile,
			OpenStreetMapTileModuleProviderBase[] providers,
			IOpenStreetMapTileProviderCallback callback) {
		mProviderQueue = new LinkedList<OpenStreetMapTileModuleProviderBase>();
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

	public OpenStreetMapTileModuleProviderBase getNextProvider() {
		mCurrentProvider = mProviderQueue.poll();
		return mCurrentProvider;
	}

	public OpenStreetMapTileModuleProviderBase getCurrentProvider() {
		return mCurrentProvider;
	}
}

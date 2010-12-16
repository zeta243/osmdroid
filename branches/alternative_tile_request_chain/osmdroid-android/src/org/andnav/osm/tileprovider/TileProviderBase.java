package org.andnav.osm.tileprovider;

import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.drawable.Drawable;

public abstract class TileProviderBase implements IOpenStreetMapTileProviderCallback {

	private static final Logger logger = LoggerFactory.getLogger(TileProviderBase.class);

	private final IOpenStreetMapTileProviderCallback mCallback;
	private TileProviderBase mNextProvider;
	private IOpenStreetMapRendererInfo mRenderer;

	public TileProviderBase(final IOpenStreetMapTileProviderCallback pCallback) {
		mCallback = pCallback;
		mCallback.setNextProvider(this);
	}

	/**
	 * Gets the name assigned to the thread for this provider.
	 *
	 * @return the thread name
	 */
	protected abstract String threadGroupName();

	/**
	 * It is expected that the implementation will construct an internal member
	 * which internally implements a TileLoader. This method is expected
	 * to return a that internal member to methods of the parent methods.
	 *
	 * @return the internal member of this tile provider.
	 */
	protected abstract Runnable getTileLoader();

	@Override
	public void mapTileRequestCompleted(final OpenStreetMapTileRequestState aState,
			Drawable aDrawable) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mapTileRequestFailed(final OpenStreetMapTileRequestState aState) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean useDataConnection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setNextProvider(TileProviderBase aTileProvider) {
		mNextProvider = aTileProvider;
	}

	public IOpenStreetMapRendererInfo getRenderer() {
		return mRenderer;
	}
}

package org.andnav.osm.tileprovider;

import org.andnav.osm.tileprovider.modules.OpenStreetMapTileDownloader;
import org.andnav.osm.tileprovider.modules.OpenStreetMapTileFilesystemProvider;
import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;
import org.andnav.osm.tileprovider.renderer.OpenStreetMapRendererFactory;
import org.andnav.osm.tileprovider.util.SimpleRegisterReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

/**
 * Objects of this class provide access to tiles which are amenable to
 * synchronous access. They are expected to return quickly enough so that a
 * person will perceive it as instantaneous.
 *
 * At present the only source which meets this criteria is the file system.
 */
public class OpenStreetMapTileProviderDirect extends
		OpenStreetMapTileProviderArray implements
		IOpenStreetMapTileProviderCallback {

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileProviderDirect.class);

	private final OpenStreetMapTileFilesystemProvider mFileSystemProvider;
	private OpenStreetMapTileDownloader mTileDownloaderProvider;

	public OpenStreetMapTileProviderDirect(final Context pContext) {
		this(new SimpleRegisterReceiver(pContext));
	}

	public OpenStreetMapTileProviderDirect(
			final IRegisterReceiver aRegisterReceiver) {
		super(aRegisterReceiver);
		mFileSystemProvider = new OpenStreetMapTileFilesystemProvider(
				aRegisterReceiver);
		mTileDownloaderProvider = new OpenStreetMapTileDownloader(
				OpenStreetMapRendererFactory.DEFAULT_RENDERER, mFileSystemProvider);
		super.mTileProviderList.add(mFileSystemProvider);
		super.mTileProviderList.add(mTileDownloaderProvider);
	}

	public IOpenStreetMapRendererInfo getRenderer() {
		return mTileDownloaderProvider.getRenderer();
	}
}

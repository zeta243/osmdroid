package org.andnav.osm.tileprovider;

import org.andnav.osm.tileprovider.modules.INetworkAvailablityCheck;
import org.andnav.osm.tileprovider.modules.NetworkAvailabliltyCheck;
import org.andnav.osm.tileprovider.modules.OpenStreetMapTileDownloader;
import org.andnav.osm.tileprovider.modules.OpenStreetMapTileFileArchiveProvider;
import org.andnav.osm.tileprovider.modules.OpenStreetMapTileFilesystemProvider;
import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;
import org.andnav.osm.tileprovider.renderer.OpenStreetMapOnlineTileRendererBase;
import org.andnav.osm.tileprovider.renderer.OpenStreetMapRendererFactory;
import org.andnav.osm.tileprovider.util.SimpleRegisterReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

/**
 * This top-level tile provider implements a default tile request chain which
 * includes a FileSystemProvider (a file-system cache), and a
 * TileDownloaderProvider (downloads map tiles via Render).
 *
 * @author Marc Kurtz
 *
 */
public class OpenStreetMapTileProviderDirect extends
		OpenStreetMapTileProviderArray implements
		IOpenStreetMapTileProviderCallback {

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileProviderDirect.class);

	/**
	 * Creates an OpenStreetMapTileProviderDirect.
	 */
	public OpenStreetMapTileProviderDirect(final Context aContext) {
		this(aContext, OpenStreetMapRendererFactory.DEFAULT_RENDERER);
	}

	/**
	 * Creates an OpenStreetMapTileProviderDirect.
	 */
	public OpenStreetMapTileProviderDirect(
			final Context aContext,
			final IOpenStreetMapRendererInfo aRenderer) {
		this(new SimpleRegisterReceiver(aContext),
			 new NetworkAvailabliltyCheck(aContext),
			 aRenderer);
	}

	/**
	 * Creates an OpenStreetMapTileProviderDirect.
	 */
	public OpenStreetMapTileProviderDirect(
			final IRegisterReceiver aRegisterReceiver,
			final INetworkAvailablityCheck aNetworkAvailablityCheck,
			final IOpenStreetMapRendererInfo aRenderer) {
		super(aRegisterReceiver);

		final OpenStreetMapTileFilesystemProvider fileSystemProvider = new OpenStreetMapTileFilesystemProvider(aRegisterReceiver);
		mTileProviderList.add(fileSystemProvider);

		final OpenStreetMapTileFileArchiveProvider archiveProvider = new OpenStreetMapTileFileArchiveProvider(aRenderer, aRegisterReceiver);
		mTileProviderList.add(archiveProvider);

		if (aRenderer instanceof OpenStreetMapOnlineTileRendererBase) {
			final OpenStreetMapTileDownloader downloaderProvider =
				new OpenStreetMapTileDownloader(
						(OpenStreetMapOnlineTileRendererBase)aRenderer,
						fileSystemProvider,
						aNetworkAvailablityCheck);
			mTileProviderList.add(downloaderProvider);
		}
	}
}

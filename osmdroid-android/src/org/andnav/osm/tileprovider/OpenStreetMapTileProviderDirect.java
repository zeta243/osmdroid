package org.andnav.osm.tileprovider;

import org.andnav.osm.tileprovider.modules.OpenStreetMapTileDownloader;
import org.andnav.osm.tileprovider.modules.OpenStreetMapTileFileArchiveProvider;
import org.andnav.osm.tileprovider.modules.OpenStreetMapTileFilesystemProvider;
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
	 *
	 * @param pContext
	 *            a context
	 */
	public OpenStreetMapTileProviderDirect(final Context pContext) {
		this(new SimpleRegisterReceiver(pContext));
	}

	/**
	 * Creates an OpenStreetMapTileProviderDirect.
	 *
	 * @param aRegisterReceiver
	 *            a RegisterReceiver
	 */
	public OpenStreetMapTileProviderDirect(
			final IRegisterReceiver aRegisterReceiver) {
		super(aRegisterReceiver);

		final OpenStreetMapTileFilesystemProvider fileSystemProvider = new OpenStreetMapTileFilesystemProvider(aRegisterReceiver);
		final OpenStreetMapTileFileArchiveProvider archiveProvider = new OpenStreetMapTileFileArchiveProvider(aRegisterReceiver);
		final OpenStreetMapTileDownloader downloaderProvider = new OpenStreetMapTileDownloader(OpenStreetMapRendererFactory.DEFAULT_RENDERER, fileSystemProvider);

		mTileProviderList.add(fileSystemProvider);
		mTileProviderList.add(archiveProvider);
		mTileProviderList.add(downloaderProvider);
	}
}

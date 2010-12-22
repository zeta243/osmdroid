// Created by plusminus on 21:31:36 - 25.09.2008
package org.andnav.osm.tileprovider.modules;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.UnknownHostException;

import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.tileprovider.OpenStreetMapTileRequestState;
import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;
import org.andnav.osm.tileprovider.renderer.OpenStreetMapOnlineTileRendererBase;
import org.andnav.osm.tileprovider.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.drawable.Drawable;

/**
 * The OpenStreetMapTileDownloader loads tiles from an HTTP server. It
 * subscribes to a FilesystemCacheProvider if available and saves data in it.
 *
 * @author Marc Kurtz
 * @author Nicolas Gramlich
 * @author Manuel Stahl
 *
 */
public class OpenStreetMapTileDownloader extends
		OpenStreetMapTileModuleProviderBase implements
		IPreferredRenderChangedReceiver {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileDownloader.class);

	// ===========================================================
	// Fields
	// ===========================================================

	private final IFilesystemCacheProvider mFilesystemCacheProvider;

	private IFilesystemCache mFilesystemCache;

	private OpenStreetMapOnlineTileRendererBase mRendererInfo;

	private final INetworkAvailablityCheck mNetworkAvailablityCheck;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OpenStreetMapTileDownloader(
			final OpenStreetMapOnlineTileRendererBase pRendererInfo) {
		this(pRendererInfo, null, null);
	}

	public OpenStreetMapTileDownloader(
			final OpenStreetMapOnlineTileRendererBase pRendererInfo,
			final IFilesystemCacheProvider pFilesystemCacheProvider) {
		this(pRendererInfo, pFilesystemCacheProvider, null);
	}

	public OpenStreetMapTileDownloader(
			final OpenStreetMapOnlineTileRendererBase pRendererInfo,
			final IFilesystemCacheProvider pFilesystemCacheProvider,
			final INetworkAvailablityCheck pNetworkAvailablityCheck) {
		super(NUMBER_OF_TILE_DOWNLOAD_THREADS,
				TILE_DOWNLOAD_MAXIMUM_QUEUE_SIZE);

		mFilesystemCacheProvider = pFilesystemCacheProvider;
		mNetworkAvailablityCheck = pNetworkAvailablityCheck;
		onPreferredRendererChanged(pRendererInfo);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public IOpenStreetMapRendererInfo getRenderer() {
		return mRendererInfo;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean getUsesDataConnection() {
		return true;
	}

	@Override
	protected String getName() {
		return "Online Tile Download Provider";
	}

	@Override
	protected String getThreadGroupName() {
		return "downloader";
	}

	@Override
	protected Runnable getTileLoader() {
		return new TileLoader();
	};

	@Override
	public void detach() {
		if (mFilesystemCacheProvider != null)
			mFilesystemCacheProvider
					.unregisterRendererForFilesystemAccess(mRendererInfo);
		super.detach();
	}

	@Override
	public int getMinimumZoomLevel() {
		return mRendererInfo.getMinimumZoomLevel();
	}

	@Override
	public int getMaximumZoomLevel() {
		return mRendererInfo.getMaximumZoomLevel();
	}

	@Override
	public void onPreferredRendererChanged(final IOpenStreetMapRendererInfo renderer) {

		// We are only interested in OpenStreetMapOnlineTileRendererBase
		// renderers
		if (renderer instanceof OpenStreetMapOnlineTileRendererBase) {

			if (mFilesystemCacheProvider != null) {
				if ((mFilesystemCache != null) && (mRendererInfo != null))
					mFilesystemCacheProvider
							.unregisterRendererForFilesystemAccess(mRendererInfo);
			}

			mRendererInfo = (OpenStreetMapOnlineTileRendererBase) renderer;

			if (mFilesystemCacheProvider != null) {
				mFilesystemCache = mFilesystemCacheProvider
						.registerRendererForFilesystemAccess(mRendererInfo,
								mRendererInfo.getMinimumZoomLevel(),
								mRendererInfo.getMaximumZoomLevel());
			}
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private String buildURL(final OpenStreetMapTile tile) {
		return mRendererInfo.getTileURLString(tile);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class TileLoader extends
			OpenStreetMapTileModuleProviderBase.TileLoader {

		@Override
		public Drawable loadTile(final OpenStreetMapTileRequestState aState)
				throws CantContinueException {

			InputStream in = null;
			OutputStream out = null;
			OpenStreetMapTile tile = aState.getMapTile();

			try {

				if (mNetworkAvailablityCheck != null
						&& !mNetworkAvailablityCheck.getNetworkAvailable()) {
					if (DEBUGMODE)
						logger.debug("Skipping " + getName()
								+ " due to NetworkAvailabliltyCheck.");
					return null;
				}

				final String tileURLString = buildURL(tile);

				if (DEBUGMODE)
					logger.debug("Downloading Maptile from url: "
							+ tileURLString);

				in = new BufferedInputStream(
						new URL(tileURLString).openStream(),
						StreamUtils.IO_BUFFER_SIZE);

				final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);
				StreamUtils.copy(in, out);
				out.flush();
				final byte[] data = dataStream.toByteArray();
				ByteArrayInputStream byteStream = new ByteArrayInputStream(data);

				// Save the data to the filesystem cache
				if (mFilesystemCache != null) {
					mFilesystemCache.saveFile(mRendererInfo, tile, byteStream);
					byteStream.reset();
				}
				Drawable result = mRendererInfo.getDrawable(byteStream);

				return result;
			} catch (final UnknownHostException e) {
				// no network connection so empty the queue
				logger.warn("UnknownHostException downloading MapTile: " + tile
						+ " : " + e);
				throw new CantContinueException(e);
			} catch (final FileNotFoundException e) {
				logger.warn("Tile not found: " + tile + " : " + e);
			} catch (final IOException e) {
				logger.warn("IOException downloading MapTile: " + tile + " : "
						+ e);
			} catch (final Throwable e) {
				logger.error("Error downloading MapTile: " + tile, e);
			} finally {
				StreamUtils.closeStream(in);
				StreamUtils.closeStream(out);
			}

			return null;
		}
	}
}

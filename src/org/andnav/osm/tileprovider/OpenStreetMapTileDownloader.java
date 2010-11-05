// Created by plusminus on 21:31:36 - 25.09.2008
package org.andnav.osm.tileprovider;

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

import org.andnav.osm.tileprovider.util.CloudmadeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The OpenStreetMapTileDownloader loads tiles from a server and passes them to
 * a OpenStreetMapTileFilesystemProvider.
 * 
 * @author Nicolas Gramlich
 * @author Manuel Stahl
 * 
 */
public class OpenStreetMapTileDownloader extends OpenStreetMapAsyncTileProvider
		implements IOpenStreetMapTileProviderCloudmadeTokenCallback {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileDownloader.class);

	// ===========================================================
	// Fields
	// ===========================================================

	private final OpenStreetMapTileFilesystemProvider mMapTileFSProvider;
	private String mCloudmadeToken;

	private IOpenStreetMapTileProviderCloudmadeTokenCallback mCallback;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OpenStreetMapTileDownloader(
			final IOpenStreetMapTileProviderCloudmadeTokenCallback pCallback,
			final OpenStreetMapTileFilesystemProvider aMapTileFSProvider) {
		super(NUMBER_OF_TILE_DOWNLOAD_THREADS, TILE_DOWNLOAD_MAXIMUM_QUEUE_SIZE);
		this.mMapTileFSProvider = aMapTileFSProvider;
		mCallback = pCallback;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected String threadGroupName() {
		return "downloader";
	}

	@Override
	protected Runnable getTileLoader() {
		return new TileLoader();
	};

	// ===========================================================
	// Methods
	// ===========================================================

	private String buildURL(final OpenStreetMapTile tile)
			throws CloudmadeException {
		return tile.getRenderer().getTileURLString(tile, this);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class TileLoader extends OpenStreetMapAsyncTileProvider.TileLoader {

		@Override
		public void loadTile(final OpenStreetMapTile aTile,
				TileLoadResult aResult) throws CantContinueException {

			InputStream in = null;
			OutputStream out = null;

			try {
				final String tileURLString = buildURL(aTile);

				if (DEBUGMODE)
					logger.debug("Downloading Maptile from url: "
							+ tileURLString);

				in = new BufferedInputStream(new URL(tileURLString)
						.openStream(), StreamUtils.IO_BUFFER_SIZE);

				final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				out = new BufferedOutputStream(dataStream,
						StreamUtils.IO_BUFFER_SIZE);
				StreamUtils.copy(in, out);
				out.flush();

				final byte[] data = dataStream.toByteArray();
				InputStream stream = new ByteArrayInputStream(data);
				aResult.setSuccessResult(stream);
				return;
			} catch (final UnknownHostException e) {
				// no network connection so empty the queue
				logger.warn("UnknownHostException downloading MapTile: "
						+ aTile + " : " + e);
				throw new CantContinueException(e);
			} catch (final FileNotFoundException e) {
				logger.warn("Tile not found: " + aTile + " : " + e);
			} catch (final IOException e) {
				logger.warn("IOException downloading MapTile: " + aTile + " : "
						+ e);
			} catch (final CloudmadeException e) {
				logger.warn("CloudmadeException downloading MapTile: " + aTile
						+ " : " + e);
			} catch (final Throwable e) {
				logger.error("Error downloading MapTile: " + aTile, e);
			} finally {
				StreamUtils.closeStream(in);
				StreamUtils.closeStream(out);
			}

			/*
			 * Don't immediately send the tile back. If we're moving, and the
			 * internet is a bit patchy, then by the time the download has
			 * finished we don't need this tile any more. If we still do need it
			 * then the file system provider will get it again next time it's
			 * needed. That should be immediately because the view is redrawn
			 * when it receives this completion event.
			 */
			// tileLoaded(aTile, true);

			aResult.setFailureResult();
		}
	}

	@Override
	public String getCloudmadeToken(final String aKey)
			throws CloudmadeException {

		if (mCloudmadeToken == null) {
			synchronized (this) {
				// check again inside the synchronised block
				if (mCloudmadeToken == null) {
					mCloudmadeToken = CloudmadeUtil.getCloudmadeToken(aKey);
				}
			}
		}

		return mCloudmadeToken;
	}

	@Override
	public String getCloudmadeKey() throws CloudmadeException {
		// TODO Auto-generated method stub
		return null;
	};

}

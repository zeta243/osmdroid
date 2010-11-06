package org.andnav.osm.tileprovider;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.andnav.osm.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.andnav.osm.views.util.IMapTileFilenameProvider;
import org.andnav.osm.views.util.OpenStreetMapTileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract child class of {@link OpenStreetMapTileProvider} which acquires
 * tile images asynchronously from some network source. The key unimplemented
 * methods are 'threadGroupname' and 'getTileLoader'.
 */
public abstract class OpenStreetMapAsyncTileProvider implements
		OpenStreetMapTileProviderConstants {

	/**
	 * 
	 * @return
	 */
	protected abstract String threadGroupName();

	/**
	 * It is expected that the implementation will construct an internal member
	 * which internally implements a {@link TileLoader}. This method is expected
	 * to return a that internal member to methods of the parent methods.
	 * 
	 * @return the internal member of this tile provider.
	 */
	protected abstract Runnable getTileLoader();

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapAsyncTileProvider.class);

	private final int mThreadPoolSize;
	private final ThreadGroup mThreadPool = new ThreadGroup(threadGroupName());
	private final ConcurrentHashMap<OpenStreetMapTile, OpenStreetMapTileRequestState> mWorking;
	final LinkedHashMap<OpenStreetMapTile, OpenStreetMapTileRequestState> mPending;
	// private static final Object PRESENT = new Object();
	private IMapTileFilenameProvider mMapTileFilenameProvider;

	public OpenStreetMapAsyncTileProvider(final int aThreadPoolSize,
			final int aPendingQueueSize,
			IMapTileFilenameProvider pMapTileFilenameProvider) {
		mThreadPoolSize = aThreadPoolSize;
		mWorking = new ConcurrentHashMap<OpenStreetMapTile, OpenStreetMapTileRequestState>();
		mPending = new LinkedHashMap<OpenStreetMapTile, OpenStreetMapTileRequestState>(
				aPendingQueueSize + 2, 0.1f, true) {
			private static final long serialVersionUID = 6455337315681858866L;

			@Override
			protected boolean removeEldestEntry(
					Entry<OpenStreetMapTile, OpenStreetMapTileRequestState> pEldest) {
				return size() > aPendingQueueSize;
			}
		};
		mMapTileFilenameProvider = pMapTileFilenameProvider;
	}

	/**
	 * Returns an IMapTileFilenameProvider to be used to obtain a filename where
	 * the downloaded tile will be saved to. This typically points to a
	 * file-system cache that is being served via a FilesystemProvider. If it is
	 * null, then the tile will never be cached.
	 * 
	 * @return
	 */
	protected IMapTileFilenameProvider getMapTileFilenameProvider() {
		return mMapTileFilenameProvider;
	}

	public void loadMapTileAsync(final OpenStreetMapTileRequestState aState) {

		final int activeCount = mThreadPool.activeCount();

		synchronized (mPending) {
			// sanity check
			if (activeCount == 0 && !mPending.isEmpty()) {
				logger
						.warn("Unexpected - no active threads but pending queue not empty");
				clearQueue();
			}

			// this will put the tile in the queue, or move it to the front of
			// the queue if it's already present
			mPending.put(aState.getMapTile(), aState);
		}

		if (DEBUGMODE)
			logger.debug(activeCount + " active threads");
		if (activeCount < mThreadPoolSize) {
			final Thread t = new Thread(mThreadPool, getTileLoader());
			t.start();
		}
	}

	private void clearQueue() {
		synchronized (mPending) {
			mPending.clear();
		}
		mWorking.clear();
	}

	/**
	 * Stops all workers - we're shutting down.
	 */
	public void stopWorkers() {
		this.clearQueue();
		this.mThreadPool.interrupt();
	}

	/**
	 * Load the requested tile. An abstract internal class whose objects are
	 * used by worker threads to acquire tiles from servers. It processes tiles
	 * from the 'pending' set to the 'working' set as they become available. The
	 * key unimplemented method is 'loadTile'.
	 * 
	 * @param aTile
	 *            the tile to load
	 * @throws CantContinueException
	 *             if it is not possible to continue with processing the queue
	 * @throws CloudmadeException
	 *             if there's an error authorizing for Cloudmade tiles
	 */
	protected abstract class TileLoader implements Runnable {

		/**
		 * The key unimplemented method.
		 * 
		 * @return true if the tile was loaded successfully and other tile
		 *         providers need not be called, false otherwise
		 * @param aTile
		 * @throws CantContinueException
		 */
		protected abstract void loadTile(OpenStreetMapTileRequestState aState,
				TileLoadResult aResult) throws CantContinueException;

		private OpenStreetMapTileRequestState nextTile() {

			synchronized (mPending) {
				OpenStreetMapTile result = null;

				// get the most recently accessed tile
				// - the last item in the iterator that's not already being
				// processed
				Iterator<OpenStreetMapTile> iterator = mPending.keySet()
						.iterator();

				// TODO this iterates the whole list, make this faster...
				while (iterator.hasNext()) {
					try {
						final OpenStreetMapTile tile = iterator.next();
						if (!mWorking.containsKey(tile)) {
							result = tile;
						}
					} catch (final ConcurrentModificationException e) {
						if (DEBUGMODE)
							logger
									.warn("ConcurrentModificationException break: "
											+ (result != null));

						// if we've got a result return it, otherwise try again
						if (result != null) {
							break;
						} else {
							iterator = mPending.keySet().iterator();
						}
					}
				}

				if (result != null) {
					mWorking.put(result, mPending.get(result));
				}

				return (result != null ? mPending.get(result) : null);
			}
		}

		/**
		 * A tile has loaded.
		 * 
		 * @param aTile
		 *            the tile that has loaded
		 * @param aTileInputStream
		 *            the input stream of the file.
		 */
		protected void tileLoaded(final OpenStreetMapTileRequestState aState,
				final InputStream aTileInputStream) {
			OpenStreetMapTile mapTile = aState.getMapTile();
			synchronized (mPending) {
				mPending.remove(mapTile);
			}
			mWorking.remove(mapTile);

			IMapTileFilenameProvider mapTileFilenameProvider = getMapTileFilenameProvider();
			if (mapTileFilenameProvider != null) {
				try {
					saveFile(mapTile, mapTileFilenameProvider
							.getOutputFile(mapTile), aTileInputStream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}

				aState.getCallback().mapTileRequestCompleted(
						aState,
						mapTileFilenameProvider.getOutputFile(mapTile)
								.getPath());
			} else {
				aState.getCallback().mapTileRequestCompleted(aState,
						aTileInputStream);
			}
		}

		/**
		 * A tile has loaded.
		 * 
		 * @param aTile
		 *            the tile that has loaded
		 * @param aTileInputStream
		 *            the input stream of the file.
		 */
		protected void tileLoaded(final OpenStreetMapTileRequestState aState,
				final byte[] aByteArray) {
			OpenStreetMapTile mapTile = aState.getMapTile();
			synchronized (mPending) {
				mPending.remove(mapTile);
			}
			mWorking.remove(mapTile);

			IMapTileFilenameProvider mapTileFilenameProvider = getMapTileFilenameProvider();
			if (mapTileFilenameProvider != null) {
				try {
					saveFile(mapTile, mapTileFilenameProvider
							.getOutputFile(mapTile), aByteArray);
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
			}

			aState.getCallback().mapTileRequestCompleted(aState,
					new ByteArrayInputStream(aByteArray));
		}

		/**
		 * A tile has loaded.
		 * 
		 * @param aTile
		 *            the tile that has loaded
		 * @param aTileInputStream
		 *            the input stream of the file.
		 */
		protected void tileLoaded(final OpenStreetMapTileRequestState aState,
				final String aFilename) {
			synchronized (mPending) {
				mPending.remove(aState.getMapTile());
			}
			mWorking.remove(aState.getMapTile());

			IMapTileFilenameProvider mapTileFilenameProvider = getMapTileFilenameProvider();
			if (mapTileFilenameProvider != null) {
				if (!aFilename.equals(mapTileFilenameProvider
						.getOutputFile(aState.getMapTile()))) {
					FileOutputStream fos = null;
					FileInputStream fis = null;
					try {
						fos = new FileOutputStream(mapTileFilenameProvider
								.getOutputFile(aState.getMapTile()));
						fis = new FileInputStream(aFilename);
						StreamUtils.copy(fis, fos);
					} catch (IOException e) {
						// TODO Auto-generated catch block
					} finally {
						StreamUtils.closeStream(fos);
						StreamUtils.closeStream(fis);
					}
				}
			}
			// TODO: Is this the best way to do this?
			aState.getCallback().mapTileRequestCompleted(aState, aFilename);
		}

		/**
		 * A tile has loaded.
		 * 
		 * @param aTile
		 *            the tile that has loaded
		 * @param aTileInputStream
		 *            the input stream of the file.
		 */
		protected void tileLoaded(final OpenStreetMapTileRequestState aState) {
			synchronized (mPending) {
				mPending.remove(aState.getMapTile());
			}
			mWorking.remove(aState.getMapTile());

			// TODO: Is this the best way to do this?
			aState.getCallback().mapTileRequestCompleted(aState);
		}

		private void tileLoadedFailed(final OpenStreetMapTileRequestState aState) {
			synchronized (mPending) {
				mPending.remove(aState.getMapTile());
			}
			mWorking.remove(aState.getMapTile());

			// TODO: Is this the best way to do this?
			aState.getCallback().mapTileRequestFailed(aState);
		}

		void saveFile(final OpenStreetMapTile tile, final File outputFile,
				final InputStream stream) throws IOException {
			final OutputStream bos = new BufferedOutputStream(
					new FileOutputStream(outputFile, false),
					StreamUtils.IO_BUFFER_SIZE);
			StreamUtils.copy(stream, bos);
			bos.flush();
			bos.close();
		}

		void saveFile(final OpenStreetMapTile tile, final File outputFile,
				final byte[] someData) throws IOException {
			final OutputStream bos = new BufferedOutputStream(
					new FileOutputStream(outputFile, false),
					StreamUtils.IO_BUFFER_SIZE);
			bos.write(someData);
			bos.flush();
			bos.close();
		}

		/**
		 * This is a functor class of type Runnable. The run method is the
		 * encapsulated function.
		 */
		@Override
		final public void run() {

			OpenStreetMapTileRequestState state;
			while ((state = nextTile()) != null) {
				TileLoadResult result = new TileLoadResult(
						OpenStreetMapAsyncTileProvider.this);

				if (DEBUGMODE)
					logger.debug("Next tile: " + state);
				try {
					loadTile(state, result);
				} catch (final CantContinueException e) {
					logger.info("Tile loader can't continue", e);
					clearQueue();
				} catch (final Throwable e) {
					logger.error("Error downloading tile: " + state, e);
				}

				// TODO: process result
				if (result.isSuccess()) {
					// tileLoaded(state, result.getResult());
				} else {
					OpenStreetMapAsyncTileProvider nextProvider = state
							.getNextProvider();
					if (nextProvider != null)
						nextProvider.loadMapTileAsync(state);
					// tileLoadedFailed(state);
				}
			}
			if (DEBUGMODE)
				logger.debug("No more tiles");
		}
	}

	class CantContinueException extends Exception {
		private static final long serialVersionUID = 146526524087765133L;

		public CantContinueException(final String aDetailMessage) {
			super(aDetailMessage);
		}

		public CantContinueException(final Throwable aThrowable) {
			super(aThrowable);
		}
	}
}

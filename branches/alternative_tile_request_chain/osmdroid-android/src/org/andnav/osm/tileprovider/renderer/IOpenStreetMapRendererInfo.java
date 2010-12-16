package org.andnav.osm.tileprovider.renderer;

import java.io.InputStream;

import org.andnav.osm.ResourceProxy;
import org.andnav.osm.tileprovider.OpenStreetMapTile;

import android.graphics.drawable.Drawable;

public interface IOpenStreetMapRendererInfo {

	/**
	 * An ordinal identifier for this renderer
	 * 
	 * @return the ordinal value
	 */
	int ordinal();

	/**
	 * A human-friendly name for this renderer
	 * 
	 * @return the renderer name
	 */
	String name();

	/**
	 * A localized human-friendly name for this renderer
	 * 
	 * @param proxy
	 *            a resource proxy
	 * @return the localized renderer name
	 */
	String localizedName(ResourceProxy proxy);

	/**
	 * Get a unique file path for the tile. This file path may be used to store
	 * the tile on a file system and performance considerations should be taken
	 * into consideration. It can include multiple paths. It should not begin
	 * with a leading path separator.
	 * 
	 * @param aTile
	 *            the tile
	 * @return the unique file path
	 */
	String getTileRelativeFilenameString(OpenStreetMapTile aTile);

	/**
	 * Get a rendered Drawable from the specified file path.
	 * 
	 * @param aFilePath
	 *            a file path
	 * @return the rendered Drawable
	 */
	Drawable getDrawable(String aFilePath);

	/**
	 * Get a rendered Drawable from the specified InputStream.
	 * 
	 * @param aTileInputStream
	 *            an InputStream
	 * @return the rendered Drawable
	 */
	Drawable getDrawable(InputStream aTileInputStream);

	/**
	 * Get the minimum zoom level this renderer represents (should be in
	 * HTTPRenderBase)
	 * 
	 * @return the minimum zoom level
	 */
	public int getMinimumZoomLevel();

	/**
	 * Get the maximum zoom level this renderer represents (should be in
	 * HTTPRenderBase)
	 * 
	 * @return the maximum zoom level
	 */
	public int getMaximumZoomLevel();

	/**
	 * Get the tile size in pixels this renderer represents
	 * 
	 * @return the tile size in pixels
	 */
	public int getTileSizePixels();

	/**
	 * Gets a URL that can be used to download this tile (should be in
	 * HTTPRenderBase)
	 * 
	 * @param aTile
	 *            a tile
	 * @return the URL
	 */
	public String getTileURLString(OpenStreetMapTile aTile);
}

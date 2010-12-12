package org.andnav.osm.tileprovider.renderer;

import java.io.InputStream;

import org.andnav.osm.ResourceProxy;
import org.andnav.osm.tileprovider.OpenStreetMapTile;

import android.graphics.drawable.Drawable;

public interface IOpenStreetMapRendererInfo {

	int ordinal();

	String name();

	String localizedName(ResourceProxy proxy);

	String getTileRelativeFilenameString(OpenStreetMapTile aTile);

	Drawable getDrawable(String aFilePath);

	Drawable getDrawable(InputStream aTileInputStream);

	public int getMinimumZoomLevel();

	public int getMaximumZoomLevel();

	public abstract String getTileURLString(OpenStreetMapTile aTile);
}

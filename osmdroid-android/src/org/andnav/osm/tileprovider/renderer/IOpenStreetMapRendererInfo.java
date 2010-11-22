package org.andnav.osm.tileprovider.renderer;

import java.io.InputStream;

import org.andnav.osm.ResourceProxy;
import org.andnav.osm.tileprovider.CloudmadeException;
import org.andnav.osm.tileprovider.IOpenStreetMapTileProviderCloudmadeTokenCallback;
import org.andnav.osm.tileprovider.OpenStreetMapTile;

import android.graphics.drawable.Drawable;

public interface IOpenStreetMapRendererInfo {

	int ordinal();
	String name();
	String localizedName(ResourceProxy proxy);
	int maptileSizePx();
	int maptileZoom();
	String getTileRelativeFilenameString(OpenStreetMapTile aTile);
	Drawable getDrawable(String aFilePath);
	Drawable getDrawable(InputStream aTileInputStream);
}

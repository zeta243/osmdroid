// Created by plusminus on 18:23:16 - 25.09.2008
package org.andnav.osm.views.util;

import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public enum OSMMapTileProviderInfo {
	OSMARENDER("http://tah.openstreetmap.org/Tiles/tile/", "OsmaRender", ".png", 17, 256, OSMMapTileProviderType.DOWNLOAD_PROVIDER),
	MAPNIK("http://tile.openstreetmap.org/", "Mapnik", ".png", 18, 256, OSMMapTileProviderType.DOWNLOAD_PROVIDER),
	CYCLEMAP("http://b.andy.sandbox.cloudmade.com/tiles/cycle/", "Cycle Map", ".png", 17, 256, OSMMapTileProviderType.DOWNLOAD_PROVIDER),
	OPENARIELMAP("http://tile.openaerialmap.org/tiles/1.0.0/openaerialmap-900913/", "OpenArialMap (Satellite)", ".jpg", 13, 256, OSMMapTileProviderType.DOWNLOAD_PROVIDER),
	CLOUDMADESMALLTILES("http://tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/2/64/", "Cloudmade (Small tiles)", ".jpg", 13, 64, OSMMapTileProviderType.DOWNLOAD_PROVIDER),
	CLOUDMADESTANDARDTILES("http://tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/2/256/", "Cloudmade (Standard tiles)", ".jpg", 18, 256, OSMMapTileProviderType.DOWNLOAD_PROVIDER),
	INTERNAL("http://internal/", "Internal", "", 18, 256, OSMMapTileProviderType.LOCAL_PROVIDER); // URL and name are still needed - they are used as keys by the FS/memory tile caches
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	public final String BASEURL, NAME, IMAGE_FILENAMEENDING;
	public final int ZOOM_MAXLEVEL, MAPTILE_SIZEPX;
	public final OSMMapTileProviderType PROVIDER_TYPE;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	private OSMMapTileProviderInfo(final String aBaseUrl, final String aName, final String aImageFilenameEnding, final int aZoomMax, final int aTileSizePX, final OSMMapTileProviderType aProviderType){
		this.BASEURL = aBaseUrl;
		this.NAME = aName;
		this.ZOOM_MAXLEVEL = aZoomMax;
		this.IMAGE_FILENAMEENDING = aImageFilenameEnding;
		this.MAPTILE_SIZEPX = aTileSizePX;
		this.PROVIDER_TYPE = aProviderType;
	}
	
	public static OSMMapTileProviderInfo getDefault() {
		return MAPNIK;
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	public String getTileURLString(final int[] tileID, final int zoomLevel){
		return new StringBuilder().append(this.BASEURL)
		.append(zoomLevel)
		.append("/")
		.append(tileID[OpenStreetMapViewConstants.MAPTILE_LONGITUDE_INDEX])
		.append("/")
		.append(tileID[OpenStreetMapViewConstants.MAPTILE_LATITUDE_INDEX])
		.append(this.IMAGE_FILENAMEENDING)
		.toString();
	}
}

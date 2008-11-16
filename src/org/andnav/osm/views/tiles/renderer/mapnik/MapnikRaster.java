package org.andnav.osm.views.tiles.renderer.mapnik;

public class MapnikRaster {
	
	private MapnikEnvelope mExt;
	private MapnikImageData mData;
	
	public MapnikRaster(MapnikEnvelope ext, MapnikImageData data)
	{
		mExt = ext;
		mData = data;
	}
}

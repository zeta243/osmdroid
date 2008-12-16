package org.andnav.osm.views.tiles.renderer.mapnik;

public class MapnikProjection {
	
	public enum MapnikProjectionDataType
	{
		LatLong,
		Gudermann
	}
	
	public MapnikProjectionDataType mType;
	
	public MapnikProjection(MapnikProjectionDataType t)
	{
		mType = t;
	}
	
//	public MapnikProjection(String params)
//	{
//		mParams = params;
//		init();
//	}

//	private void init()
//	{
//		// TODO: Initialise the projection object.
//	}
}

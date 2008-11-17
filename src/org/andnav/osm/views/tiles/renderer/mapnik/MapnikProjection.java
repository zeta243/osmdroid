package org.andnav.osm.views.tiles.renderer.mapnik;

import org.andnav.osm.views.tiles.renderer.mapnik.projector.Projector;

public class MapnikProjection {
	
	private String mParams;
	private Projector mProjection;
	
	public MapnikProjection()
	{
		mParams = "+proj=latlong +ellps=WGS84";
		init();
	}
	
	public MapnikProjection(String params)
	{
		mParams = params;
		init();
		
	}

	private void init()
	{
		// TODO: Initialise the projection object.
	}
}

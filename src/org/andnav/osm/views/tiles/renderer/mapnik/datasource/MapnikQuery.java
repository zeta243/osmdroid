package org.andnav.osm.views.tiles.renderer.mapnik.datasource;

import java.util.Vector;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikEnvelope;

// Original from include/makpik/query.hpp

public class MapnikQuery {
	
	private MapnikEnvelope mBoundingBox;
	private double mResolution;
	private Vector<String> mNames;
	
	public MapnikQuery(MapnikEnvelope boundingBox, double resolution)
	{
		mBoundingBox = boundingBox;
		mResolution = resolution;
		mNames = new Vector<String>();
	}
	
	public MapnikQuery(MapnikQuery q)
	{
		mBoundingBox = q.mBoundingBox;
		mResolution = q.mResolution;
		mNames      = q.mNames;
	}
	
	public double getResolution()
	{
		return mResolution;
	}
	
	public MapnikEnvelope getBoundingBox()
	{
		return mBoundingBox;
	}

	public void addPropertyName(String name)
	{
		mNames.add(name);
	}
	
	public Vector<String> getPropertyNames()
	{
		return mNames;
	}
}

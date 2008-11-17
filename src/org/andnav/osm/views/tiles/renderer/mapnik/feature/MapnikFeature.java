package org.andnav.osm.views.tiles.renderer.mapnik.feature;

import java.util.HashMap;
import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikRaster;

public class MapnikFeature {
	
	private int mId;
	Vector<Double> mGeomCont;
	MapnikRaster mRaster;
	HashMap<String, Object> properties;
	
	public MapnikFeature(int id)
	{
		mId = id;
		properties = new HashMap<String, Object>();
		mGeomCont = new Vector<Double>();
		mRaster  = null;
	}
	
	public int getId()
	{
		return mId;
	}
	
	public void addGeometry(Double g)
	{
		mGeomCont.add(g);
	}
	
	public int getNumGeometries()
	{
		return mGeomCont.size();
	}
	
	public Double getGeometry(int id)
	{
		return mGeomCont.get(id);
	}
	
	public MapnikRaster getRaster()
	{
		return mRaster;
	}
	
	public void setRaster(MapnikRaster r)
	{
		mRaster = r;
	}
	
	public HashMap<String, Object> getProperties()
	{
		return properties;
	}
	
	
	
}

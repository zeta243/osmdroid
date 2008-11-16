package org.andnav.osm.views.tiles.renderer.mapnik.feature;

import java.util.HashMap;
import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikRaster;

// geometry2d
public class MapnikFeature<GEOMETRY_TYPE> {
	
	private int mId;
	Vector<GEOMETRY_TYPE> mGeomCont;
	MapnikRaster mRaster;
	HashMap<String, Object> properties;
	
	public MapnikFeature(int id)
	{
		mId = id;
		properties = new HashMap<String, Object>();
		mGeomCont = new Vector<GEOMETRY_TYPE>();
		mRaster  = null;
	}
	
	public int getId()
	{
		return mId;
	}
	
	public void addGeometry(GEOMETRY_TYPE g)
	{
		mGeomCont.add(g);
	}
	
	public int getNumGeometries()
	{
		return mGeomCont.size();
	}
	
	public GEOMETRY_TYPE getGeometry(int id)
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

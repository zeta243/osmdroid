package org.andnav.osm.views.tiles.renderer.mapnik.feature;

import java.util.HashMap;
import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikRaster;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikGeometry;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikPoint;

public class MapnikFeature {
	
	private int mId;
	Vector<MapnikGeometry> mGeomCont;
	MapnikRaster mRaster;
	HashMap<String, MapnikParameterValue> properties;
	
	public MapnikFeature(int id)
	{
		mId = id;
		properties = new HashMap<String, MapnikParameterValue>();
		mGeomCont = new Vector<MapnikGeometry>();
		mRaster  = null;
	}
	
	public int getId()
	{
		return mId;
	}
	
	public void addGeometry(MapnikGeometry p)
	{
		mGeomCont.add(p);
	}
	
	public int getNumGeometries()
	{
		return mGeomCont.size();
	}
	
	public MapnikGeometry getGeometry(int id)
	{
		return mGeomCont.get(id);
	}
	
	public Vector<MapnikGeometry> getGeometries()
	{
		return mGeomCont;
	}
	
	public MapnikRaster getRaster()
	{
		return mRaster;
	}
	
	public void setRaster(MapnikRaster r)
	{
		mRaster = r;
	}
	
	public HashMap<String, MapnikParameterValue> getProperties()
	{
		return properties;
	}
	
	
	
}

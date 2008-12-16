package org.andnav.osm.views.tiles.renderer.mapnik.feature;

import java.util.HashMap;
import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikGeometry;

public class MapnikFeature {
	
	private int mId;
	Vector<MapnikGeometry> mGeomCont;
	HashMap<String, MapnikParameterValue> properties;
	
	public MapnikFeature(int id)
	{
		mId = id;
		properties = new HashMap<String, MapnikParameterValue>();
		mGeomCont = new Vector<MapnikGeometry>();
	}
	
	public int getId()
	{
		return mId;
	}
	
	public void addGeometry(MapnikGeometry g)
	{
		mGeomCont.add(g);
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
	
	public void setProperty(String key, MapnikParameterValue value)
	{
		properties.put(key, value);
	}
	
	public HashMap<String, MapnikParameterValue> getProperties()
	{
		return properties;
	}
	
	
	
}

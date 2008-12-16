package org.andnav.osm.views.tiles.renderer.mapnik;

import java.util.HashMap;

public class MapnikParameters {
	private HashMap<String, MapnikParameterValue> mParams;
	
	public MapnikParameters()
	{
		mParams = new HashMap<String, MapnikParameterValue>();
	}
	
	public void set(String key, MapnikParameterValue val)
	{
		mParams.put(key, val);
	}
	
	public Integer getInt(String key, Integer def)
	{
		MapnikParameterValue v = mParams.get(key);
		if (v == null)
			return def;
		return (Integer)v.getValue();
	}
	
	public String getString(String key, String def)
	{
		MapnikParameterValue v = mParams.get(key);
		if (v == null)
			return def;
		return (String) v.getValue();
	}
	
	public Double getDouble(String key, Double def)
	{
		MapnikParameterValue v = mParams.get(key);
		if (v == null)
			return def;
		return (Double)v.getValue(); 
	}
}

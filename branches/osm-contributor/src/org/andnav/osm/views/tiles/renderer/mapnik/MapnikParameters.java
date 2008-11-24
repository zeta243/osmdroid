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
		Integer r = (Integer)mParams.get(key).getValue();
		return (r == null ? def : r);
	}
	
	public String getString(String key, String def)
	{
		String r = (String)mParams.get(key).getValue();
		return (r == null ? def : r);
	}
	
	public Double getDouble(String key, Double def)
	{
		Double r = (Double)mParams.get(key).getValue(); 
		return (r == null ? def : r);
	}
}

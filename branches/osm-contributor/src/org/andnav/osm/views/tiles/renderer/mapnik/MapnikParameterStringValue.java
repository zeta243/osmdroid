package org.andnav.osm.views.tiles.renderer.mapnik;

public class MapnikParameterStringValue extends MapnikParameterValue<String> {
	
	private String p;
	
	public MapnikParameterStringValue(String v)
	{
		p = v;
	}
	
	public String getValue()
	{
		return p;
	}
	
	public void setValue(String v)
	{
		p = v;
	}
	
	public String toString()
	{
		return p;
	}
}

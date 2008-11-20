package org.andnav.osm.views.tiles.renderer.mapnik;

public class MapnikParameterIntValue extends MapnikParameterValue<Integer> {
	
	private Integer p;
	
	public MapnikParameterIntValue(Integer v)
	{
		p = v;
	}
	
	public Integer getValue()
	{
		return p;
	}
	
	public void setValue(Integer v)
	{
		p = v;
	}
	
	public String toString()
	{
		return p.toString();
	}
}

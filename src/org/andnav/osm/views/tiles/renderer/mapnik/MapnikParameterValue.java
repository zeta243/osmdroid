package org.andnav.osm.views.tiles.renderer.mapnik;

public class MapnikParameterValue<T> {

	private T p;
	
	public MapnikParameterValue(T v)
	{
		p = v;
	}
	
	public T getValue()
	{
		return p;
	}
	
	public void setValue(T v)
	{
		p = v;
	}
}

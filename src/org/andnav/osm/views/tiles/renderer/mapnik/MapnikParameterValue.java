package org.andnav.osm.views.tiles.renderer.mapnik;

public abstract class MapnikParameterValue<T> {

	public MapnikParameterValue()
	{

	}
	
	public abstract T getValue();
	
	public abstract void setValue(T v);

	public abstract String toString();
}

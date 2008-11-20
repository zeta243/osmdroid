package org.andnav.osm.views.tiles.renderer.mapnik;

public class MapnikParameterDoubleValue extends MapnikParameterValue<Double> {

	private Double p;
	
	public MapnikParameterDoubleValue(Double v)
	{
		p = v;
	}
	
	public Double getValue()
	{
		return p;
	}
	
	public void setValue(Double v)
	{
		p = v;
	}
	
	public String toString()
	{
		return p.toString();
	}
}

package org.andnav.osm.views.tiles.renderer.mapnik;

public class MapnikParameterStringValue extends MapnikParameterValue {
	
	public MapnikParameterStringValue(String v)
	{
		super(MapnikParameterValueType.STRING, v);
	}
	
	public String getValue()
	{
		return (String) mValue;
	}
	
	public void setValue(String v)
	{
		mValue = v;
	}
	
	public String toString()
	{
		return mValue.toString();
	}

	@Override
	public void setValue(Object v) {
		setValue((String)v);
	}
}

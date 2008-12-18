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
	
	public String toString()
	{
		if (mValue == null)
			return "";
		return getValue();
	}

	@Override
	public void setValue(Object v) {
		mValue = v;
	}
}

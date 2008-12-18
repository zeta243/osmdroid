package org.andnav.osm.views.tiles.renderer.mapnik;

public class MapnikParameterIntValue extends MapnikParameterValue {
	
	public MapnikParameterIntValue(Integer v)
	{
		super(MapnikParameterValueType.INTEGER, v);
	}
	
	public Integer getValue()
	{
		return (Integer) mValue;
	}
	
	public String toString()
	{
		if (mValue == null)
			return "";
		return mValue.toString();
	}

	@Override
	public void setValue(Object v) {
		mValue = v;
	}
}

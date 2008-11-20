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
	
	public void setValue(Integer v)
	{
		mValue = v;
	}
	
	public String toString()
	{
		return mValue.toString();
	}

	@Override
	public void setValue(Object v) {
		setValue((Integer)v);
	}
}

package org.andnav.osm.views.tiles.renderer.mapnik;

public class MapnikParameterDoubleValue extends MapnikParameterValue {
	
	public MapnikParameterDoubleValue(Double v)
	{
		super(MapnikParameterValueType.DOUBLE, v);
	}
	
	public Double getValue()
	{
		return (Double) mValue;
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

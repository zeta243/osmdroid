package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterDoubleValue;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterIntValue;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterStringValue;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;

public class MapnikFilterLiteral extends MapnikFilterExpression {

	private MapnikParameterValue mValue;
	
	public MapnikFilterLiteral(MapnikParameterIntValue val)
	{
		mValue = val;
	}
	
	public MapnikFilterLiteral(MapnikParameterDoubleValue val)
	{
		mValue = val;
	}
	
	public MapnikFilterLiteral(MapnikParameterStringValue val)
	{
		mValue = val;
	}
	
	public MapnikFilterLiteral(MapnikFilterLiteral l)
	{
		mValue = l.mValue;
	}
	
	@Override
	public void accept(MapnikFilterVisitor filterVisitor) {
		filterVisitor.visit(this);
	}

	@Override
	public MapnikFilterLiteral clone() {
		return new MapnikFilterLiteral(this);
	}

	@Override
	public MapnikParameterValue getValue(MapnikFeature feature) {
		return mValue;
	}

	@Override
	public String toString() {
		return mValue.toString();
	}
}

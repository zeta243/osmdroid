package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterStringValue;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;

public class MapnikFilterStringLiteral extends MapnikFilterExpression {

	private MapnikParameterStringValue mValue;
	
	public MapnikFilterStringLiteral(String val)
	{
		mValue = new MapnikParameterStringValue(val);
	}
	
	public MapnikFilterStringLiteral(MapnikFilterStringLiteral l)
	{
		mValue = l.mValue;
	}
	
	@Override
	public void accept(MapnikFilterVisitor filterVisitor) {
		filterVisitor.visit(this);
	}

	@Override
	public MapnikFilterStringLiteral clone() {
		return new MapnikFilterStringLiteral(this);
	}

	@Override
	public MapnikParameterStringValue getValue(MapnikFeature feature) {
		return mValue;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}

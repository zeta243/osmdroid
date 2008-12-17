package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterStringValue;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;

public class MapnikFilterProperty extends MapnikFilterExpression {

	private String mName;

	public MapnikFilterProperty(String name)
	{
		mName = name;
	}

	public MapnikFilterProperty(MapnikFilterProperty p)
	{
		mName = p.mName;
	}

	public String getName()
	{
		return mName;
	}

	@Override
	public void accept(MapnikFilterVisitor filterVisitor) {
		filterVisitor.visit(this);
	}

	@Override
	public MapnikFilterProperty clone() {
		return new MapnikFilterProperty(this);
	}

	@Override
	public MapnikParameterValue getValue(MapnikFeature feature) {
		return feature.getProperties().get(mName);
	}

	@Override
	public String toString() {
		return "[" + mName + "]";
	}

}

package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;

public class MapnikFilterLogicalNot extends MapnikFilter {

	private MapnikFilter mFilter;
	
	public MapnikFilterLogicalNot(MapnikFilter filter)
	{
		mFilter = filter;
	}
	
	public MapnikFilterLogicalNot(MapnikFilterLogicalNot f)
	{
		mFilter = f.mFilter;
	}
	
	public boolean pass(MapnikFeature feature)
	{
		return !(mFilter.pass(feature));
	}
	
	public MapnikFilterLogicalNot clone()
	{
		return new MapnikFilterLogicalNot(this);
	}
	
	public void accept(MapnikFilterVisitor visitor)
	{
		mFilter.accept(visitor);
		visitor.visit(this);
	}

	public String toString()
	{
		return "not (" + mFilter.toString() + ")";
	}
}

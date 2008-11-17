package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;

// Original from include/mapnik/filter.hpp

public abstract class MapnikFilter {

	public abstract boolean pass(MapnikFeature feature);
	
	public abstract MapnikFilter clone();
	
	public abstract void accept(MapnikFilterVisitor visitor);
	
	public abstract String toString();

}

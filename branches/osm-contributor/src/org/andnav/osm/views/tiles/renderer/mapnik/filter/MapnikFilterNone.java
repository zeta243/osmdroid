package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;

public class MapnikFilterNone extends MapnikFilter {

	@Override
	public void accept(MapnikFilterVisitor visitor) {
	}

	@Override
	public MapnikFilter clone() {
		return new MapnikFilterNone();
	}

	@Override
	public boolean pass(MapnikFeature feature) {
		return false;
	}

	@Override
	public String toString() {
		return "false";
	}

}

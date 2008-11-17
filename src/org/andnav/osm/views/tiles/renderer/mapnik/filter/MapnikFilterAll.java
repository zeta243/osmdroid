package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;

public class MapnikFilterAll extends MapnikFilter {

	@Override
	public void accept(MapnikFilterVisitor visitor) {
	}

	@Override
	public MapnikFilter clone() {
		return new MapnikFilterAll();
	}

	@Override
	public boolean pass(MapnikFeature feature) {
		return true;
	}

	@Override
	public String toString() {
		return "true";
	}

}

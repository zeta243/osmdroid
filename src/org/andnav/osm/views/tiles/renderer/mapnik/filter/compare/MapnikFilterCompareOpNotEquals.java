package org.andnav.osm.views.tiles.renderer.mapnik.filter.compare;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;

public class MapnikFilterCompareOpNotEquals extends MapnikFilterCompareOp
{
	@Override
	public boolean compare(MapnikParameterValue left, MapnikParameterValue right) {
		return (left.toString() != right.toString());
	}

	@Override
	public String toString() {
		return "<>";
	}
}

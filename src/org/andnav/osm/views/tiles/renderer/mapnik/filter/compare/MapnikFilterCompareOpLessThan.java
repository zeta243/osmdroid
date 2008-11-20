package org.andnav.osm.views.tiles.renderer.mapnik.filter.compare;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;

public class MapnikFilterCompareOpLessThan extends MapnikFilterCompareOp
{
	@Override
	public boolean compare(MapnikParameterValue left, MapnikParameterValue right) {
		return ((Integer)(left.getValue()) < (Integer)(right.getValue()));
	}

	@Override
	public String toString() {
		return "<";
	}
}
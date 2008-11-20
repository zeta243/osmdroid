package org.andnav.osm.views.tiles.renderer.mapnik.filter.compare;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;

public abstract class MapnikFilterCompareOp
{
	public abstract boolean compare(MapnikParameterValue left, MapnikParameterValue right);
	public abstract String toString();
}
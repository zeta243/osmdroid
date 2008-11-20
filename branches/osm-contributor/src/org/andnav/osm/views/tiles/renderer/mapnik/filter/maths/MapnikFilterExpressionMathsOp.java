package org.andnav.osm.views.tiles.renderer.mapnik.filter.maths;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterExpression;

public abstract class MapnikFilterExpressionMathsOp
{
	public abstract MapnikParameterValue calculate(MapnikFeature feature, MapnikFilterExpression leftE, MapnikFilterExpression rightE);
	public abstract String toString();
}
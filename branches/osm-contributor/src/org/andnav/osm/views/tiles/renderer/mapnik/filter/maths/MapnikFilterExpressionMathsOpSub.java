package org.andnav.osm.views.tiles.renderer.mapnik.filter.maths;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterExpression;

public class MapnikFilterExpressionMathsOpSub extends MapnikFilterExpressionMathsOp
{
	@Override
	public MapnikParameterValue calculate(MapnikFeature feature,
			                              MapnikFilterExpression leftE,
			                              MapnikFilterExpression rightE) {
		return leftE.getValue(feature).subtract(rightE.getValue(feature));
	}

	@Override
	public String toString() {
		return "-";
	}
}
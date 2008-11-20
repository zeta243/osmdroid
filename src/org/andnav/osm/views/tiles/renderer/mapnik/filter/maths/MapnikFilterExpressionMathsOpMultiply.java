package org.andnav.osm.views.tiles.renderer.mapnik.filter.maths;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterExpression;

public class MapnikFilterExpressionMathsOpMultiply extends MapnikFilterExpressionMathsOp
{
	@Override
	public MapnikParameterValue calculate(MapnikFeature feature,
			                              MapnikFilterExpression leftE,
			                              MapnikFilterExpression rightE) {
		return leftE.getValue(feature).multiply(rightE.getValue(feature));
	}
	
	@Override
	public String toString() {
		return "*";
	}
}
package org.andnav.osm.views.tiles.renderer.mapnik.filter.maths;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterExpression;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterVisitor;

public class MapnikFilterExpressionMaths extends MapnikFilterExpression {

	private MapnikFilterExpression mLeft;
	private MapnikFilterExpression mRight;
	
	private MapnikFilterExpressionMathsOp mOp;
	
	public MapnikFilterExpressionMaths(MapnikFilterExpressionMathsOp op, MapnikFilterExpression left, MapnikFilterExpression right)
	{
		mLeft = left;
		mRight = right;
		mOp = op;
	}
	
	@Override
	public void accept(MapnikFilterVisitor filterVisitor) {
		mLeft.accept(filterVisitor);
        mRight.accept(filterVisitor);
        filterVisitor.visit(this);
	}
	
	@Override
	public MapnikFilterExpression clone() {
		return new MapnikFilterExpressionMaths(mOp, mLeft, mRight);
	}

	public MapnikParameterValue getValue(MapnikFeature feature) {
		return mOp.calculate(feature, mLeft, mRight);
	}
	@Override
	public String toString() {
		return mLeft.toString() + " " + mOp.toString() + " " + mRight.toString();
	}

}

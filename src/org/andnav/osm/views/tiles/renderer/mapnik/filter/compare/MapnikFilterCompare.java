package org.andnav.osm.views.tiles.renderer.mapnik.filter.compare;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilter;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterExpression;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterVisitor;

public class MapnikFilterCompare extends MapnikFilter {

	private MapnikFilterExpression mLeft;
	private MapnikFilterExpression mRight;
	private MapnikFilterCompareOp  mOp;

	public MapnikFilterCompare(MapnikFilterExpression left, MapnikFilterExpression right, MapnikFilterCompareOp op)
	{
		mLeft  = left;
		mRight = right;
		mOp    = op;
	}
	
	public MapnikFilterCompare(MapnikFilterCompare c)
	{
		mLeft = c.mLeft;
		mRight = c.mRight;
		mOp    = c.mOp;
	}
	
	@Override
	public boolean pass(MapnikFeature feature)
	{
		return (mOp.compare(mLeft.getValue(feature), mRight.getValue(feature)));
	}
	
	@Override
	public void accept(MapnikFilterVisitor visitor) {
		mLeft.accept(visitor);
		mRight.accept(visitor);
		visitor.visit(this);
	}

	@Override
	public MapnikFilterCompare clone() {
		return new MapnikFilterCompare(this);
	}

	@Override
	public String toString() {
		return mLeft.toString() + " " + mOp.toString() + " " + mRight.toString();
	}
}

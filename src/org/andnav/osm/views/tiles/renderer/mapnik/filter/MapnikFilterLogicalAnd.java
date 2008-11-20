package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;

public class MapnikFilterLogicalAnd extends MapnikFilter {

	private MapnikFilter mLeft;
	private MapnikFilter mRight;
	
	public MapnikFilterLogicalAnd(MapnikFilter left, MapnikFilter right)
	{
		mLeft = left;
		mRight = right;
	}
	
	public MapnikFilterLogicalAnd(MapnikFilterLogicalAnd f)
	{
		mLeft = f.mLeft;
		mRight = f.mRight;
	}
	
	public boolean pass(MapnikFeature feature)
	{
		if (mLeft.pass(feature) && mRight.pass(feature))
			return true;
		return false;
	}
	
	public MapnikFilterLogicalAnd clone()
	{
		return new MapnikFilterLogicalAnd(this);
	}
	
	public void accept(MapnikFilterVisitor visitor)
	{
		mLeft.accept(visitor);
		mRight.accept(visitor);
		visitor.visit(this);
	}

	public String toString()
	{
		return "(" + mLeft.toString() + " and " + mRight.toString() + ")";
	}

}

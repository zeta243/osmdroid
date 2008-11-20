package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;

public class MapnikFilterLogicalOr extends MapnikFilter {
	
	private MapnikFilter mLeft;
	private MapnikFilter mRight;
	
	public MapnikFilterLogicalOr(MapnikFilter left, MapnikFilter right)
	{
		mLeft = left;
		mRight = right;
	}
	
	public MapnikFilterLogicalOr(MapnikFilterLogicalOr f)
	{
		mLeft = f.mLeft;
		mRight = f.mRight;
	}
	
	public boolean pass(MapnikFeature feature)
	{
		if (mLeft.pass(feature))
			return true;
		else
			return mRight.pass(feature);
	}
	
	public MapnikFilterLogicalOr clone()
	{
		return new MapnikFilterLogicalOr(this);
	}
	
	public void accept(MapnikFilterVisitor visitor)
	{
		mLeft.accept(visitor);
		mRight.accept(visitor);
		visitor.visit(this);
	}

	public String toString()
	{
		return "(" + mLeft.toString() + " or " + mRight.toString() + ")";
	}
}

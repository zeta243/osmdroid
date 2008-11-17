package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikGeometry;

public class MapnikFilterHitTest extends MapnikFilter {
	
	private double mX;
	private double mY;
	private double mTolerance;
	
	public MapnikFilterHitTest(double x, double y, double tol)
	{
		mX = x;
		mY = y;
		mTolerance = tol;
	}

	public boolean pass(MapnikFeature f)
	{
		for (MapnikGeometry g : f.getGeometries())
		{
			if (g.HitTest(mX, mY, mTolerance))
			    return true;
		}
		return false;
	}

	@Override
	public void accept(MapnikFilterVisitor visitor) {
	}

	@Override
	public MapnikFilter clone() {
		return new MapnikFilterHitTest(mX, mY, mTolerance);
	}

	@Override
	public String toString() {
		return "hittest";
	}
}

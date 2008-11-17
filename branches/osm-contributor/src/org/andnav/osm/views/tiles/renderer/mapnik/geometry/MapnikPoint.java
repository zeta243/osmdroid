package org.andnav.osm.views.tiles.renderer.mapnik.geometry;

import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex.VertexCommandType;

public class MapnikPoint extends MapnikGeometry {

	private MapnikVertex mPoint;
	
	public MapnikPoint()
	{
		super();
	}
	
	@Override
	public boolean HitTest(double x, double y, double tol) {
		return MapnikGeometryUtil.pointInsideCircle(mPoint.x, mPoint.y, x, y, tol);
	}

	@Override
	public void labelPosition(double[] coords) {
		coords[0] = mPoint.x;
		coords[1] = mPoint.y;
	}

	// Not needed for a point
	@Override
	public void lineTo(MapnikVertex v) {
	}

	@Override
	public void moveTo(MapnikVertex v) {
		mPoint.x = v.x;
		mPoint.y = v.y;
	}

	@Override
	public int numPoints() {
		return 1;
	}

	// Not needed for a point
	@Override
	public void rewind(int pos) {
		return;
	}

	// Not needed for a point
	@Override
	public void setCapacity(int size) {
	}

	@Override
	public GeomType getType() {
		return GeomType.Point;
	}

	@Override
	public MapnikVertex getNextVertex() {
		return mPoint;
	}
}

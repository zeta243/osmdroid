package org.andnav.osm.views.tiles.renderer.mapnik.filter.maths;

public abstract class MapnikFilterExpressionMathsOp
{
	public abstract int calculate(int left, int right);
	public abstract double calculate(double left, double right);
	public abstract String toString();
}
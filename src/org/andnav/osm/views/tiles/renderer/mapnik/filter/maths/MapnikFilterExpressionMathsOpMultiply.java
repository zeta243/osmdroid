package org.andnav.osm.views.tiles.renderer.mapnik.filter.maths;

public class MapnikFilterExpressionMathsOpMultiply extends MapnikFilterExpressionMathsOp
{
	public int calculate(int left, int right)
	{
		return left * right;
	}
	
	public double calculate(double left, double right)
	{
		return left * right;
	}

	@Override
	public String toString() {
		return "*";
	}
}
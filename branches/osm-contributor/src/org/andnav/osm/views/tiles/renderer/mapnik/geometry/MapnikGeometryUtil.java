package org.andnav.osm.views.tiles.renderer.mapnik.geometry;

import java.util.Vector;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex.VertexCommandType;

public class MapnikGeometryUtil {

	public static boolean PointInCircle(double x, double y, double cx, double cy, double r)
	{
        double dx = x - cx;
        double dy = y - cy;
        double d2 = dx * dx + dy * dy;
        return (d2 <= r * r);
	}
	
	public static boolean PointInsidePath(double x,double y,Vector<MapnikVertex> path)
	{
		boolean inside = false;
		
		double x0 = path.get(0).x;
		double y0 = path.get(0).y;
		
		for (MapnikVertex v : path)
		{
			if (v.mCmd == VertexCommandType.SEG_MOVETO)
			{
				x0 = v.x;
				y0 = v.y;
				continue;
			}

	        if ((((v.y <= y) && (y < y0)) ||
	              ((y0 <= y) && (y < v.y))) &&
	               ( x < (x0 - v.x) * (y - v.y)/ (y0 - v.y) + v.x))
	               inside=!inside;
	        x0=v.x;
	        y0=v.x;
		}
		return inside;
	}
}

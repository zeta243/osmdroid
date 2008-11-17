package org.andnav.osm.views.tiles.renderer.mapnik.geometry;

import java.util.Vector;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex.VertexCommandType;

public class MapnikGeometryUtil {

	public static boolean pointInsideCircle(double x, double y, double cx, double cy, double r)
	{
        double dx = x - cx;
        double dy = y - cy;
        double d2 = dx * dx + dy * dy;
        return (d2 <= r * r);
	}
	
	public static boolean pointInsidePath(double x,double y,Vector<MapnikVertex> path)
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
	
	public static boolean pointOnPath(double x, double y, Vector<MapnikVertex> path, double tol)
	{
		double x0 = path.get(0).x;
		double y0 = path.get(0).y;
		
        double x1,y1;
        for (MapnikVertex v : path)
        {
            if ( v.mCmd == VertexCommandType.SEG_MOVETO)
            {
                x0 = v.x;
                y0 = v.y;
                continue;
            }

            double distance = pointToSegmentDistance(x,y,x0,y0,v.x,v.y);
            if (distance < tol)
                return true;
            x0=v.x;
            y0=v.y;
        }
        return false;
	}
	
	public static double pointToSegmentDistance(double x, double y,
                                                double ax, double ay,
                                                double bx, double by)
	{
        double len2 = distance2(ax,ay,bx,by);

        if (len2 < 1e-14)
        {
            return distance(x,y,ax,ay);
        }

        double r = ((x - ax)*(bx - ax) + (y - ay)*(by -ay))/len2;
        if ( r < 0 )
        {
            return distance(x,y,ax,ay);
        }
        else if (r > 1)
        {
            return distance(x,y,bx,by);
        }
        double s = ((ay - y)*(bx - ax) - (ax - x)*(by - ay))/len2;
        
        return Math.abs(s) * Math.sqrt(len2);

	}
	
    public static double distance(double x0,double y0, double x1,double y1)
    {
        return Math.sqrt(distance2(x0,y0,x1,y1));
    }


    public static double distance2(double x0,double y0,double x1,double y1)
    {
        double dx = x1 - x0;
        double dy = y1 - y0;
        return square(dx) + square(dy);
    }
    
    public static double square(double d)
    {
    	return d * d;
    }

}

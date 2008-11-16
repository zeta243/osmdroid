package org.andnav.osm.views.tiles.renderer.mapnik.geometry;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikEnvelope;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex.VertexCommandType;

public abstract class MapnikGeometry {
	
    public enum GeomType {
        Point,
        LineString,
        Polygon
    };

    public MapnikGeometry()
    {
    	
    }
    
    public MapnikEnvelope getEnvelope()
    {
    	MapnikEnvelope result = null;
    	
        rewind(0);
        for (int i = 0 ; i < numPoints() ; ++i)
        {
            MapnikVertex v = getNextVertex();
            if (i==0)
            {
            	result = new MapnikEnvelope(v.x, v.y, v.x, v.y);
            }
            else
            {
                result.expandToInclude(v.x, v.y);
            }
        }
        return result;
    }

    public abstract GeomType getType();
    public abstract boolean HitTest(double x, double y, double tol);
    public abstract void labelPosition(double[] coords);
    public abstract void moveTo(MapnikVertex v);
    public abstract void lineTo(MapnikVertex v);
    public abstract int numPoints();
    public abstract MapnikVertex getNextVertex();
    public abstract void rewind(int pos);
    public abstract void setCapacity(int size);
    
}

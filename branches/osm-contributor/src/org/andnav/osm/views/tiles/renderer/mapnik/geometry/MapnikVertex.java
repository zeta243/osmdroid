package org.andnav.osm.views.tiles.renderer.mapnik.geometry;

// From include/mapnik/vertex.hpp

public class MapnikVertex {

    public enum VertexCommandType {
        SEG_END,
        SEG_MOVETO,
        SEG_LINETO,
        SEG_CLOSE
    };
    
    public double x;
    public double y;
    public VertexCommandType mCmd;
    
    public MapnikVertex()
    {
    	x = 0;
    	y = 0;
    	mCmd = VertexCommandType.SEG_END;
    }
    
    public MapnikVertex(double X, double Y, VertexCommandType cmd)
    {
    	x = X;
    	y = Y;
    	mCmd = cmd;
    }
    
    public MapnikVertex(MapnikVertex v)
    {
    	x = v.x;
    	y = v.y;
    	mCmd = v.mCmd;
    }
}

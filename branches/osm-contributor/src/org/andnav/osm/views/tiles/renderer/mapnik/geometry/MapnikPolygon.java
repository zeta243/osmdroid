package org.andnav.osm.views.tiles.renderer.mapnik.geometry;

import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex.VertexCommandType;

public class MapnikPolygon extends MapnikGeometry {

    private Vector<MapnikVertex> mVertices;
    private int mItr;

	public MapnikPolygon()
	{
		mItr = 0;
	}
	
	@Override
	public boolean HitTest(double x, double y, double unused) {
		return MapnikGeometryUtil.pointInsidePath(x, y, mVertices);
	}

	@Override
	public GeomType getType() {
		return GeomType.Polygon;
	}

	@Override
	public void labelPosition(double[] coords) {
		int size = mVertices.size();
		
        if (size < 3)
        {
        	MapnikVertex v = mVertices.get(0);
        	coords[0] = v.x;
        	coords[1] = v.y;
           return;
        }

        double ai;
        double atmp = 0;
        double xtmp = 0;
        double ytmp = 0;
        double x0 =0;
        double y0 =0;
        double x1 =0;
        double y1 =0;

        int i,j;
        for (i = size-1,j = 0; j < size; i = j, ++j)
        {
        	MapnikVertex v1 = mVertices.get(i);
        	MapnikVertex v2 = mVertices.get(j);
        	
        	x0 = v1.x;
        	y0 = v1.y;
        	x1 = v2.x;
        	y1 = v2.y;
        	
            ai = x0 * y1 - x1 * y0;
            atmp += ai;
            xtmp += (x1 + x0) * ai;
            ytmp += (y1 + y0) * ai;
        }
        if (atmp != 0)
        {
           coords[0] = xtmp/(3*atmp);
           coords[1] = ytmp /(3*atmp);
           return;
        }
        coords[0] = x0;
        coords[1] = y0;
	}

	@Override
	public void lineTo(MapnikVertex v) {
		v.mCmd = VertexCommandType.SEG_LINETO;
		mVertices.add(v);
	}

	@Override
	public void moveTo(MapnikVertex v) {
		v.mCmd = VertexCommandType.SEG_LINETO;
		mVertices.add(v);
	}

	@Override
	public int numPoints() {
		return mVertices.size();
	}

	@Override
	public void rewind(int pos) {
		mItr = 0;
	}

	@Override
	public void setCapacity(int size) {
		mVertices.ensureCapacity(size);
	}

	@Override
	public MapnikVertex getNextVertex() {
		return mVertices.get(mItr++);
	}



}

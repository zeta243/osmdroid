package org.andnav.osm.views.tiles.renderer.mapnik;

public class MapnikEnvelope {
	private double mMinX;
	private double mMinY;
	private double mMaxX;
	private double mMaxY;
	
	public static final int COORD_X = 0;
	public static final int COORD_Y = 1;
	
	public MapnikEnvelope()
	{
		mMinX = mMinY = 0;
		mMaxX = mMaxY = -1.0;
	}
	
	public MapnikEnvelope(double minx, double miny, double maxx, double maxy)
	{
		init(minx, miny, maxx, maxy);
	}
	
	public MapnikEnvelope(double[] c0,double[] c1)
	{
		 init(c0[COORD_X],c0[COORD_Y],c1[COORD_X],c1[COORD_Y]);
	}
	
	public MapnikEnvelope(MapnikEnvelope e)
	{
		init(e.mMinX, e.mMinY, e.mMaxX, e.mMaxY);
	}
		
	private void init(double minx, double miny, double maxx, double maxy)
	{
        if (minx<maxx)
        {
            mMinX=minx;
            mMaxX=maxx;
        }
        else
        {
            mMinX=maxx;
            mMaxX=minx;
        }
        if (miny<maxy)
        {
            mMinY=miny;
            mMaxY=maxy;
        }
        else
        {
            mMinY=maxy;
            mMaxY=miny;
        }
	}

	public double getMinX() {
		return mMinX;
	}

	public double getMinY() {
		return mMinY;
	}

	public double getMaxX() {
		return mMaxX;
	}

	public double getMaxY() {
		return mMaxY;
	}

	public double getWidth()
	{
		return mMaxX - mMinX;
	}
	
	public void setWidth(double w)
	{
		double cx = getCenterX();
		mMinX = (cx - w * 0.5);
		mMaxX = (cx + w * 0.5);
	}
	
	public double getHeight()
	{
		return mMaxY - mMinY;
	}
	
	public void setHeight(double h)
	{
		double cy = getCenterY();
		mMinY = (cy - h * 0.5);
		mMaxY = (cy + h * 0.5);
	}
	
	public double getCenterX()
	{
		return 0.5 * (mMinX+mMaxX);
	}
	
	public double getCenterY()
	{
		return 0.5 * (mMinY+mMaxY);
	}
	
	public double[] getCenter()
	{
		double[] d = new double[2];
		d[COORD_X] = getCenterX();
		d[COORD_Y] = getCenterY();
		return d;
	}
	
	public void expandToInclude(double[] coord)
	{
		expandToInclude(coord[COORD_X], coord[COORD_X]);
	}
	
	public void expandToInclude(double x, double y)
	{
        if (x<mMinX) mMinX=x;
        if (x>mMaxX) mMaxX=x;
        if (y<mMinY) mMinY=y;
        if (y>mMaxY) mMaxY=y;
	}
	
	public void expandToInclude(MapnikEnvelope e)
	{
        if (e.mMinX<mMinX) mMinX=e.mMinX;
        if (e.mMaxX>mMaxX) mMaxX=e.mMaxX;
        if (e.mMinY<mMinY) mMinY=e.mMinY;
        if (e.mMaxY>mMaxY) mMaxY=e.mMaxY;
	}
	
	public boolean contains(double[] coords)
	{
		return contains(coords[COORD_X], coords[COORD_Y]);
	}
	
	public boolean contains(double x, double y)
	{
		return x>=mMinX && x<=mMaxX && y>=mMinY && y<=mMaxY;		
	}
	
	public boolean contains(MapnikEnvelope other)
	{
        return other.mMinX>=mMinX &&
               other.mMaxX<=mMaxX &&
               other.mMinY>=mMinY &&
               other.mMaxY<=mMaxY;
	}
	
	public boolean intersects(double[] coords)
	{
		return intersects(coords[COORD_X], coords[COORD_Y]);
	}
	
	public boolean intersects(double x, double y)
	{
        return !(x>mMaxX || x<mMinX || y>mMaxY || y<mMinY);
	}
	
	public boolean intersects(MapnikEnvelope e)
	{
        return !(e.mMinX>mMaxX || e.mMaxX<mMinX ||
                e.mMinY>mMaxY || e.mMaxY<mMinY);
	}
	
	public MapnikEnvelope intersect(MapnikEnvelope e)
	{
		double minX = Math.max(mMinX, e.mMinX);
		double minY = Math.max(mMinY, e.mMinY);
		
		double maxX = Math.min(mMaxX, e.mMaxX);
		double maxY = Math.min(mMaxY, e.mMaxY);
		
		return new MapnikEnvelope(minX, minY, maxX, maxY);
	}
	
	public void reCenter(double cx, double cy)
	{
		double dx = cx - getCenterX();
		double dy = cy - getCenterY();
		mMinX += dx;
		mMinY += dy;
		mMaxX += dx;
		mMaxY += dy;
	}
}

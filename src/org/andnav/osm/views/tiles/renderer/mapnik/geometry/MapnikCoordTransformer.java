package org.andnav.osm.views.tiles.renderer.mapnik.geometry;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikEnvelope;

// Original from include/mapnik/ctrans.hpp

public class MapnikCoordTransformer {
	
    private int mWidth;
    private int mHeight;
    private double mScale;
    private MapnikEnvelope mExtent;
    private double mOffset_x;
    private double mOffset_y;

    public MapnikCoordTransformer(int width, int height, MapnikEnvelope extent, int offset_x, int offset_y)
    {
    	mWidth = width;
    	mHeight = height;
    	mExtent = extent;
    	mOffset_x = offset_x;
    	mOffset_y = offset_y;
    	
        double sx=((double)width)/extent.getWidth();
        double sy=((double)height)/extent.getHeight();
        mScale = Math.min(sx, sy);
    }
    
    public double getScale()
    {
    	return mScale;
    }
    
    public void forward(double[] coords)
    {
    	coords[0] = (coords[0] - mExtent.getMinX()) * mScale - mOffset_x;
    	coords[1] = (mExtent.getMaxY() - coords[1]) * mScale - mOffset_y;
    }
    
    public void backward(double[] coords)
    {
    	coords[0] = mExtent.getMinX() + (coords[0] + mOffset_x) / mScale;
    	coords[1] = mExtent.getMaxY() - (coords[1] + mOffset_y) / mScale;
    }  
}

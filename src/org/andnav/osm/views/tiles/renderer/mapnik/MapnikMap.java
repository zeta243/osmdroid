package org.andnav.osm.views.tiles.renderer.mapnik;

import java.util.HashMap;
import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.datasource.MapnikDataSource;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureSet;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureSetFilter;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureTypeStyle;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterHitTest;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikCoordTransformer;

// original from include/mapnik/map.hpp

public class MapnikMap {
	
	public static MapnikFeatureTypeStyle defaultStyle;
	
	static final int MIN_MAPSIZE = 16;
	static final int MAX_MAPSIZE = MIN_MAPSIZE << 10;

	private int mWidth;
	private int mHeight;
	private String mSrs; // Spatial Reference String

	private int mBackground;
	
	private HashMap<String, MapnikFeatureTypeStyle> mStyles;
	private Vector<MapnikLayer> mLayers;
	private MapnikEnvelope mCurrentExtent;
	
	public MapnikMap()
	{
		mWidth  = 512;
		mHeight = 512;
		mSrs    = "+proj=latlong +datum=WGS84";
		mStyles = new HashMap<String, MapnikFeatureTypeStyle>();
		mLayers = new Vector<MapnikLayer>();
	}
	
	public MapnikMap(int width, int height, String src)
	{
		if (width < MIN_MAPSIZE)
			width = MIN_MAPSIZE;
		if (height > MAX_MAPSIZE)
			height = MAX_MAPSIZE;
		mWidth  = width;
		mHeight = height;
		mSrs    = src;
		mStyles = new HashMap<String, MapnikFeatureTypeStyle>();
		mLayers = new Vector<MapnikLayer>();
	}
	
	public MapnikMap(MapnikMap m)
	{
		mWidth      = m.mWidth;
		mHeight     = m.mHeight;
		mSrs        = m.mSrs;
		mBackground = m.mBackground;
		mStyles     = m.mStyles;
		mCurrentExtent   = m.mCurrentExtent;
	}
	
	public HashMap<String, MapnikFeatureTypeStyle> getStyles()
	{
		return mStyles;
	}
	
	public void addStyle(String key, MapnikFeatureTypeStyle style)
	{
		mStyles.put(key, style);
	}
	
	public void removeStyle(String key)
	{
		mStyles.remove(key);
	}
	
	public MapnikFeatureTypeStyle getStyle(String key)
	{
		MapnikFeatureTypeStyle s = mStyles.get(key);
		return (s == null ? MapnikMap.defaultStyle : s);
	}
	
	public int getNumLayers()
	{
		return mLayers.size();
	}
	
	public void addLayer(MapnikLayer l)
	{
		mLayers.add(l);	
	}
	
	public void removeLayer(int location)
	{
		mLayers.remove(location);
	}
	
	public void removeAll()
	{
		mLayers.clear();
		mStyles.clear();
	}
	
	public MapnikLayer getLayer(int location)
	{
		return mLayers.get(location);
	}
	
	public Vector<MapnikLayer> getLayers()
	{
		return mLayers;
	}
	
	public int getMapWidth()
	{
		return mWidth;
	}
	
	public void setMapWidth(int width)
	{
        if (width >= MIN_MAPSIZE && width <= MAX_MAPSIZE)
        {
            mWidth = width;
            fixAspectRatio();
        }
	}
	
	public int getMapHeight()
	{
		return mHeight;
	}
	
	public void setMapHeight(int height)
	{
		if (height >= MIN_MAPSIZE && height <= MAX_MAPSIZE)
		{
			mHeight = height;
			fixAspectRatio();
		}
	}
	
	public void resizeMap(int width, int height)
	{
        if (width >= MIN_MAPSIZE && width <= MAX_MAPSIZE &&
                height >= MIN_MAPSIZE && height <= MAX_MAPSIZE)
        {
            mWidth=width;
            mHeight=height;
            fixAspectRatio();
        }
	}
	
	public String getSRS()
	{
		return mSrs;
	}
	
	public void setSRS(String srs)
	{
		mSrs = srs;
	}
	
	public int getBackgroundColour()
	{
		return mBackground;
	}
	
	public void setBackgroundColour(int c)
	{
		mBackground = c;
	}
	
	public void zoom(Double factor)
	{
		double[] center = mCurrentExtent.getCenter();
        
        double w = factor * mCurrentExtent.getWidth();
        double h = factor * mCurrentExtent.getHeight();
        mCurrentExtent = new MapnikEnvelope(center[0] - 0.5 * w,
                                          center[1] - 0.5 * h,
                                          center[0] + 0.5 * w,
                                          center[1] + 0.5 * h);
        fixAspectRatio();
	}
	
	public void zoomAll()
	{
		// Projection Transformations skipped - all data *must* be in Mercator projection
		MapnikEnvelope mapEnvelope = null;
		
		for (MapnikLayer l : mLayers)
		{
			MapnikEnvelope layerEnvelope = l.getEnvelope();
			
			double mapMinX = layerEnvelope.getMinX();
			double mapMinY = layerEnvelope.getMinY();
			
			double mapMaxX = layerEnvelope.getMaxX();
			double mapMaxY = layerEnvelope.getMaxY();
			
			MapnikEnvelope convertedLayerEnvelope = new MapnikEnvelope(mapMinX, mapMinY, mapMaxX, mapMaxY);
			
			if (mapEnvelope == null)
				mapEnvelope = convertedLayerEnvelope;
			else
				mapEnvelope.expandToInclude(convertedLayerEnvelope);
		}
		
		this.zoomToBox(mapEnvelope);
	}
	
	public void zoomToBox(MapnikEnvelope e)
	{
		this.mCurrentExtent = e;
		fixAspectRatio();
	}
	
	private void fixAspectRatio()
	{
        double ratio1 = (double) mWidth / (double) mHeight;
        double ratio2 = mCurrentExtent.getWidth() / mCurrentExtent.getHeight();

        if (ratio2 > ratio1)
        {
            mCurrentExtent.setHeight(mCurrentExtent.getWidth() / ratio1);
        }
        else if (ratio2 < ratio1)
        {
            mCurrentExtent.setWidth(mCurrentExtent.getHeight() * ratio1);
        }
	}
	
	public MapnikEnvelope getCurrentExtent()
	{
		return this.mCurrentExtent;
	}
	
	public void pan(int x, int y)
	{
        int dx = (int) (x - (0.5 * mWidth));
        int dy = (int)(0.5 * mHeight) - y;
        
        double s = mWidth/mCurrentExtent.getWidth();
        double minx  = mCurrentExtent.getMinX() + dx/s;
        double maxx  = mCurrentExtent.getMaxX() + dx/s;
        double miny  = mCurrentExtent.getMinY() + dy/s;
        double maxy  = mCurrentExtent.getMaxY() + dy/s;
        mCurrentExtent = new MapnikEnvelope(minx,miny,maxx,maxy);
	}
	
	public void panAndZoom(int x, int y, double factor)
	{
		pan(x, y);
		zoom(factor);
	}
	
	public double getScale()
	{
		if (mWidth > 0)
			return mCurrentExtent.getWidth() / mWidth;
		return mCurrentExtent.getWidth();
	}
	
	public MapnikCoordTransformer getCoordTransformer()
	{
		return new MapnikCoordTransformer(mWidth, mHeight, mCurrentExtent, 0, 0);
	}
	
	public MapnikFeatureSet queryPoint(int index, double[] coords)
	{
		// Projection Transformations skipped - all data *must* be in Mercator projection
		MapnikLayer l = getLayer(index);
        
        double minx = mCurrentExtent.getMinX();
        double maxx = mCurrentExtent.getMaxX();

        double tol = (maxx - minx) / mWidth * 3;
        
        MapnikDataSource ds = l.getDataSource();
        if (ds != null)
        {
        	MapnikFeatureSet fs = ds.getFeaturesAtPoint(coords);
        	if (fs != null)
        		return new MapnikFeatureSet(new MapnikFeatureSetFilter(fs, new MapnikFilterHitTest(coords[0], coords[1], tol)));
        }	    
		return null;
	}

	public MapnikFeatureSet queryMapPoint(int index, double[] coords)
	{
		// Projection Transformations skipped - all data *must* be in Mercator projection
		MapnikLayer l = getLayer(index);
		MapnikCoordTransformer tr = getCoordTransformer();
		
		tr.backward(coords);
		
		double minx = mCurrentExtent.getMinX();
		double maxx = mCurrentExtent.getMaxX();		

        double tol = (maxx - minx) / mWidth * 3;
        MapnikDataSource ds = l.getDataSource();
        if (ds != null)
        {
        	MapnikFeatureSet fs = ds.getFeaturesAtPoint(coords);
        	if (fs != null)
        		return new MapnikFeatureSet(new MapnikFeatureSetFilter(fs, new MapnikFilterHitTest(coords[0], coords[1], tol)));
        }
		return null;
	}
}

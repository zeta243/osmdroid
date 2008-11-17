package org.andnav.osm.views.tiles.renderer.mapnik;

import java.util.HashMap;
import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureTypeStyle;

// original from include/mapnik/map.hpp

public class MapnikMap {
	
	public static MapnikFeatureTypeStyle defaultStyle;
	
	static final int MIN_MAPSIZE = 16;
	static final int MAX_MAPSIZE = MIN_MAPSIZE << 10;

	private int mWidth;
	private int mHeight;
	private String mSrc;

	private MapnikColour mBackground;
	
	private HashMap<String, MapnikFeatureTypeStyle> mStyles;
	private Vector<MapnikLayer> mLayers;
	private MapnikEnvelope mCurrentExtent;
	
	public MapnikMap()
	{
		mWidth  = 400;
		mHeight = 400;
		mSrc    = "+proj=latlong +datum=WGS84";
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
		mSrc    = src;
		mStyles = new HashMap<String, MapnikFeatureTypeStyle>();
		mLayers = new Vector<MapnikLayer>();
	}
	
	public MapnikMap(MapnikMap m)
	{
		mWidth      = m.mWidth;
		mHeight     = m.mHeight;
		mSrc        = m.mSrc;
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
	
	public String getSource()
	{
		return mSrc;
	}
	
	public void setSource(String source)
	{
		mSrc = source;
	}
	
	public MapnikColour getBackgroundColour()
	{
		return mBackground;
	}
	
	public void setBackgroundColour(MapnikColour c)
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
		MapnikProjection proj0 = new MapnikProjection(mSrc);
		MapnikEnvelope ext = null;
		boolean first = true;
		
		for (MapnikLayer l : mLayers)
		{
			String layerSrc = l.getSrc();
			MapnikProjection proj1 = new MapnikProjection(layerSrc);
			
		}
/*
        projection proj0(srs_);
        Envelope<double> ext;
        bool first = true;
        std::vector<Layer>::const_iterator itr = layers_.begin();
        std::vector<Layer>::const_iterator end = layers_.end();
        while (itr != end)
        {
            std::string const& layer_srs = itr->srs();
            projection proj1(layer_srs);
            proj_transform prj_trans(proj0,proj1);

            Envelope<double> layerExt = itr->envelope();
            double x0 = layerExt.minx();
            double y0 = layerExt.miny();
            double z0 = 0.0;
            double x1 = layerExt.maxx();
            double y1 = layerExt.maxy();
            double z1 = 0.0;
            prj_trans.backward(x0,y0,z0);
            prj_trans.backward(x1,y1,z1);

            Envelope<double> layerExt2(x0,y0,x1,y1);
#ifdef MAPNIK_DEBUG
            std::clog << " layer1 - > " << layerExt << "\n";
            std::clog << " layer2 - > " << layerExt2 << "\n";
#endif
            if (first)
            {
                ext = layerExt2;
                first = false;
            }
            else
            {
                ext.expand_to_include(layerExt2);
            }
            ++itr;
        }
        zoomToBox(ext);
*/
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
}

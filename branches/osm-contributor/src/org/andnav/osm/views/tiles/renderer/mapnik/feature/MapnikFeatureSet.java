package org.andnav.osm.views.tiles.renderer.mapnik.feature;

import java.util.Vector;

public class MapnikFeatureSet {

	private Vector<MapnikFeature> mFeatureSet;
	private int mItr;
	
	public MapnikFeatureSet()
	{
		mFeatureSet = new Vector<MapnikFeature>();
	}
	
	public MapnikFeatureSet(MapnikFeatureSetFilter featureSetFilter)
	{
		mFeatureSet = new Vector<MapnikFeature>();
		MapnikFeature f;
		while ((f = featureSetFilter.getNext()) != null)
		{
			mFeatureSet.add(f);
		}
	}
	
	public void rewind()
	{
		mItr = 0;
	}
	
	public MapnikFeature getNext()
	{
		return mFeatureSet.get(mItr++);
	}
}

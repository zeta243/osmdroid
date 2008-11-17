package org.andnav.osm.views.tiles.renderer.mapnik.feature;

import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilter;


public class MapnikFeatureSetFilter extends MapnikFeatureSet {
	
	MapnikFeatureSet mFeatureSet;
	MapnikFilter mFilter;
	
	public MapnikFeatureSetFilter(MapnikFeatureSet featureSet, MapnikFilter f)
	{
		mFeatureSet = featureSet;
		mFilter     = f;
	}
	
	public MapnikFeature getNext()
	{
		MapnikFeature f = mFeatureSet.getNext();
		while (f != null && !mFilter.pass(f))
		{
			f = mFeatureSet.getNext();
		}
		return f;
	}
}

package org.andnav.osm.views.tiles.renderer.mapnik.feature;

// original from include/mapnik/feature_type_style.hpp

import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikRule;

public class MapnikFeatureTypeStyle {

	private Vector<MapnikRule> mRules;

	public MapnikFeatureTypeStyle() {
	}

	public MapnikFeatureTypeStyle(MapnikFeatureTypeStyle fts) {
		mRules = fts.mRules;
	}
	
	public void addRule(MapnikRule r)
	{
		mRules.add(r);
	}
	
	public Vector<MapnikRule> getRules()
	{
		return mRules;
	}
}

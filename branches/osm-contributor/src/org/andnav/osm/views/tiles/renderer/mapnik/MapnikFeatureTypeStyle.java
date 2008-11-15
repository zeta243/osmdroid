package org.andnav.osm.views.tiles.renderer.mapnik;

// original from include/mapnik/feature_type_style.hpp

import java.util.Vector;

public class MapnikFeatureTypeStyle {

	private Vector<MapnikRule> mRules;

	MapnikFeatureTypeStyle() {
	}

	MapnikFeatureTypeStyle(MapnikFeatureTypeStyle fts) {
		mRules = fts.mRules;
	}

	/*
	 * class feature_type_style {
	 * 
	 * feature_type_style(feature_type_style const& rhs) : rules_(rhs.rules_) {}
	 * 
	 * feature_type_style& operator=(feature_type_style const& rhs) { if (this
	 * == &rhs) returnthis; rules_=rhs.rules_; returnthis; }
	 * 
	 * void add_rule(rule_type const& rule) { rules_.push_back(rule); }
	 * 
	 * rules const& get_rules() const { return rules_; }
	 * 
	 * ~feature_type_style() {} };
	 */
}

package org.andnav.osm.views.tiles.renderer.mapnik;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterVisitor;

public interface MapnikExpression {
	
	public Object getValue(MapnikFeature feature);
	public void accept(MapnikFilterVisitor v);
	
	/*
    virtual value get_value(FeatureT const& feature) const=0;
    virtual void accept(filter_visitor<FeatureT>& v)=0;
    virtual expression<FeatureT>* clone() const=0;
    virtual std::string to_string() const=0;
    virtual ~expression() {}
*/
}

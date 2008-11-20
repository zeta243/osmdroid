package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterValue;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;

public abstract class MapnikFilterExpression {
	
	public abstract MapnikParameterValue getValue(MapnikFeature feature);
	
	public abstract void accept(MapnikFilterVisitor filterVisitor);
	
	public abstract MapnikFilterExpression clone();
	
	public abstract String toString();
}

package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikExpression;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikRule;

public interface MapnikFilterVisitor {
	
	public void visit(MapnikFilter filter);
	public void visit(MapnikExpression expression);
	public void visit(MapnikRule rule);

}

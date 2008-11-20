package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikRule;

public interface MapnikFilterVisitor {
	
	public void visit(MapnikFilter filter);
	public void visit(MapnikFilterExpression expression);
	public void visit(MapnikRule rule);

}

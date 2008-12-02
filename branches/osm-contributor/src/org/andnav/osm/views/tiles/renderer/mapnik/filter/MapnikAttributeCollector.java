package org.andnav.osm.views.tiles.renderer.mapnik.filter;

import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikRule;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikShieldSymbolizer;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikSymbolizer;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikTextSymbolizer;

public class MapnikAttributeCollector implements MapnikFilterVisitor {

	private Vector<String> mNames;
	
	public MapnikAttributeCollector(Vector<String> names)
	{
		mNames = names;
	}
	
	@Override // Not interested
	public void visit(MapnikFilter filter) {
		return;
	}

	@Override
	public void visit(MapnikFilterExpression expression) {
		if (expression instanceof MapnikFilterProperty)
		{
		    mNames.add(((MapnikFilterProperty)expression).getName());
		}
	}

	@Override
	public void visit(MapnikRule rule) {
		Vector<MapnikSymbolizer> symbols = rule.getSymbolizers();
		MapnikSymbolizerAttributes attributes = new MapnikSymbolizerAttributes(mNames);
		
		for (MapnikSymbolizer s : symbols)
		{
			if (s instanceof MapnikTextSymbolizer)
				attributes.insert((MapnikTextSymbolizer)s);
			else if (s instanceof MapnikShieldSymbolizer)
				attributes.insert((MapnikShieldSymbolizer)s);
		}
		MapnikFilter f = rule.getFilter();
		f.accept(this);
	}

	public class MapnikSymbolizerAttributes {
		
		private Vector<String> mNames;
		
		public MapnikSymbolizerAttributes(Vector<String> names)
		{
			mNames = names;
		}
		
		public void insert(MapnikTextSymbolizer s)
		{
			mNames.add(s.getName());
		}
		
		public void insert(MapnikShieldSymbolizer s)
		{
			mNames.add(s.getName());
		}
	}
}

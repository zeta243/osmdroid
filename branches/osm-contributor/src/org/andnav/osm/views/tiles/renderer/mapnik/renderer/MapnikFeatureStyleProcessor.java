package org.andnav.osm.views.tiles.renderer.mapnik.renderer;

import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikEnvelope;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikLayer;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikMap;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikRule;
import org.andnav.osm.views.tiles.renderer.mapnik.datasource.MapnikDataSource;
import org.andnav.osm.views.tiles.renderer.mapnik.datasource.MapnikQuery;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureSet;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureTypeStyle;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikAttributeCollector;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilter;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikSymbolizer;

public abstract class MapnikFeatureStyleProcessor {
	
	protected MapnikMap mMap;
	
	public MapnikFeatureStyleProcessor(MapnikMap map)
	{
		mMap = map;
	}
	
	public void apply() throws Exception
	{
		startMapProcessing(mMap);
		double scaleDenominator = (mMap.getScale() / 0.00028);
		
		Vector<MapnikLayer> layers = mMap.getLayers();
		for (MapnikLayer l : layers)
		{
			if (l.isActive())
				applyToLayer(l, scaleDenominator);
		}
	}
	
	public abstract void startMapProcessing(MapnikMap map);
	public abstract void stopMapProcessing(MapnikMap map);
	
	public abstract void startLayerProcessing(MapnikLayer layer);
	public abstract void stopLayerProcessing(MapnikLayer layer);
	
	public abstract void process(MapnikSymbolizer symbol, MapnikFeature f) throws Exception;
	
    public void applyToLayer(MapnikLayer layer, double scaleDenominator) throws Exception
    {
    	startLayerProcessing(layer);
    	MapnikDataSource ds = layer.getDataSource();
    	
    	if (ds == null)
    	{
    		stopLayerProcessing(layer);
    		return;
    	}
    	MapnikEnvelope e = mMap.getCurrentExtent();
    	MapnikQuery query = new MapnikQuery(e, mMap.getMapWidth() / e.getWidth());
    	
    	Vector<String> styles = layer.getStyles();
    	
    	boolean activeRules = false;
    	for (String s : styles)
    	{
    		MapnikFeatureTypeStyle fts = mMap.getStyle(s);
    		Vector<MapnikRule> rules = fts.getRules();
    		Vector<String> names = new Vector<String>();
    		MapnikAttributeCollector collector = new MapnikAttributeCollector(names);
    		Vector<MapnikRule> ifRules = new Vector<MapnikRule>();
    		Vector<MapnikRule> elseRules = new Vector<MapnikRule>();
    		
    		if (rules != null)
    		{
    			for (MapnikRule r : rules)
    			{
    				if (r.active(scaleDenominator))
    				{
    					activeRules = true;
    					r.accept(collector);
    					if (r.isElseFilter())
    						elseRules.add(r);
    					else
    						ifRules.add(r);
    				}
    			}
    		}
    		
    		for (String name : names)
    		{
    			query.addPropertyName(name);
    		}
    		
    		if (activeRules)
    		{
    			MapnikFeatureSet fs = ds.getFeatures(query);
    			if (fs != null)
    			{
    				MapnikFeature feature = null;
    				while ((feature = fs.getNext()) != null)
    				{
    					boolean doElse = true;
    					for (MapnikRule r : ifRules)
    					{
    						MapnikFilter filter = r.getFilter();
    						if (filter.pass(feature))
    						{
    							doElse = false;
    							Vector <MapnikSymbolizer> symbols = r.getSymbolizers();
    							
    							for (MapnikSymbolizer symbol : symbols)
    							{
    								process(symbol, feature);
    							}
    						}
    					}
    					if (doElse)
    					{
    						for (MapnikRule r : elseRules)
    						{
    							Vector <MapnikSymbolizer> symbols = r.getSymbolizers();
    							for (MapnikSymbolizer symbol : symbols)
    							{
    								process(symbol, feature);
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	stopLayerProcessing(layer);
    }
}

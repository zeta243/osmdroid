package org.andnav.osm.views.tiles.renderer.mapnik;

import java.io.IOException;
import java.util.Stack;
import java.util.Vector;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureTypeStyle;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilter;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterExpression;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterParser;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikLinePatternSymbolizer;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikLineSymbolizer;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikPointSymbolizer;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikPolygonPatternSymbolizer;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikPolygonSymbolizer;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikShieldSymbolizer;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikTextSymbolizer;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikTextSymbolizer.LabelPlacementEnum;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.util.Log;

public class MapnikMapParser {
	
	private static final String TAG = "MapnikMapParser";
	
	public MapnikMapParser()
	{
	}
	
	public boolean parseMap(MapnikMap m, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
        	if (eventType == XmlPullParser.START_TAG)
        	{
        		Log.d(TAG, "Got Tag: " + xpp.getName());
        		if (xpp.getName().equals("Map"))
        		{
        			for (int i = 0; i < xpp.getAttributeCount(); i++)
        			{
        				if (xpp.getAttributeName(i).equals("bgcolor"))
        				{
        					m.setBackgroundColour(Color.parseColor(xpp.getAttributeValue(i)));
        				}
        				else if (xpp.getAttributeName(i).equals("srs"))
        				{
        					m.setSRS(xpp.getAttributeValue(i));
        				}
        				else
        					throw new MapnikInvalidXMLException(xpp, "Unexpected attribute: " + xpp.getAttributeName(i));
        			}
        			
        			while (eventType != XmlPullParser.END_DOCUMENT)
        			{
        				eventType = xpp.next();
        				if (eventType == XmlPullParser.START_TAG)
        				{
        					if (xpp.getName().equals("Style"))
        						parseStyle(m, xpp);
        					else if (xpp.getName().equals("Layer"))
        					{
        						parseLayer(m, xpp);
        					}
        					else if (xpp.getName().equals("Datasource"))
        					{
        						// TODO: Not needed/used in OSM - not implemented.
        						throw new MapnikInvalidXMLException(xpp, "Support for Datasource templates has not been implemented");
        					}
        					else
        						throw new MapnikInvalidXMLException(xpp, "Unexpected Tag");
        				}
        				else
        					break;
        			}
        		}
        		else
        			throw new MapnikInvalidXMLException(xpp, "Unexpected Tag");
        	}
        	eventType = xpp.next();
        } 
        return true;
	}
	
	private void parseLayer(MapnikMap m, XmlPullParser xpp)  throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String layerName = "Unnamed";
		String status = null;
		String srs    = null;
		String clearLabelsCache = null;

		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName.equals("name"))
				layerName = attrValue;
			
			else if (attrName.equals("srs"))
				srs = attrValue;
			
			else if (attrName.equals("status" ))
				status = attrValue;
			
			else if (attrName.equals("clear_label_cache"))
				clearLabelsCache = attrValue;
			
			else
				throw new MapnikInvalidXMLException(xpp, "Unexpected attribute: " + attrName);
		}
		
		if (srs == null)
			srs = m.getSRS();
		
		MapnikLayer layer = new MapnikLayer(layerName, srs);
		
		if (status != null)
			if (status.equals("on"))
				layer.setActive(true);
			else if (status.equals("off"))
				layer.setActive(false);
			else
				throw new MapnikInvalidXMLException(xpp, "Unexpected attribute value: status (" + status + ")");
		
		if (clearLabelsCache != null)
			if (clearLabelsCache == "on" || clearLabelsCache == "true")
				layer.setClearLabelCache(true);
			else if (clearLabelsCache == "off" || clearLabelsCache == "false")
				layer.setClearLabelCache(false);
			else
				throw new MapnikInvalidXMLException(xpp, "Unexpected attribute: clear_label_cache");
		
		int eventType = xpp.getEventType();
		
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG)
			{
				if (xpp.getName().equals("StyleName"))
				{
					eventType = xpp.next();
					if (eventType == XmlPullParser.TEXT)
					{
						layer.addStyle(xpp.getText());
						eventType = xpp.next();
						if (eventType != XmlPullParser.END_TAG)
							throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
						continue;
					}
					else
						throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
					
					
				}
				else if (xpp.getName().equals("Datasource"))
				{
					MapnikParameters params = new MapnikParameters();
					
					while (eventType != XmlPullParser.END_DOCUMENT)
					{
						eventType = xpp.next();
						if (eventType == XmlPullParser.START_TAG)
						{
							if (xpp.getName().equals("Parameter"))
							{
								String paramName = null;
								for (int i = 0; i < xpp.getAttributeCount(); i++)
								{
									if (xpp.getAttributeName(i).equals("name"))
										paramName = xpp.getAttributeValue(i);
									else
									    throw new MapnikInvalidXMLException(xpp, "Unexpected attribute: " + xpp.getAttributeName(i));
								}
								if (paramName == null)
									throw new MapnikInvalidXMLException(xpp, "Missing required attribute (name): " + xpp.getName());
								
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									params.set(paramName, new MapnikParameterStringValue(xpp.getText()));
									
									eventType = xpp.next();
									if (eventType != XmlPullParser.END_TAG)
										throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
									continue;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
							}
						}
						else if (eventType == XmlPullParser.END_TAG && xpp.getName().equals("Datasource"))
						    break;
					}
				}
				else
					throw new MapnikInvalidXMLException(xpp, "Unexpected Tag");
			}
			else  if (eventType == XmlPullParser.END_TAG)
				break;
			else
				throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
		}
	}
	
	private void parseStyle(MapnikMap m, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String styleName = "<missing name>";
		
		MapnikFeatureTypeStyle style = new MapnikFeatureTypeStyle();
		
		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName.equals("name"))
				styleName = attrValue;
			else
				throw new MapnikInvalidXMLException(xpp, "Unexpected attribute: " + attrName);
		}
		
		int eventType = xpp.getEventType();
		
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG)
			{
				if (xpp.getName().equals("Rule"))
				    parseRule(style, xpp);
				else
					throw new MapnikInvalidXMLException(xpp, "Unexpected Tag");
			}
			else if (eventType == XmlPullParser.END_TAG && xpp.getName().equals("Rule"))
				continue;
			else if (eventType == XmlPullParser.END_TAG && xpp.getName().equals("Style"))
				break;
			else
				throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
		}
		m.addStyle(styleName, style);
	}
	
	private void parseRule(MapnikFeatureTypeStyle style, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String ruleName = "";
		String ruleTitle = "";

		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName.equals("name"))
				ruleName = attrValue;
			else if (attrName.equals("title"))
			    ruleTitle = attrValue;
			else
				throw new MapnikInvalidXMLException(xpp, "Unexpected attribute: " + attrValue);
		}
		
		MapnikRule rule = new MapnikRule(ruleName, ruleTitle);
		
		int eventType = xpp.getEventType();
		
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG)
			{
				String tagName = xpp.getName();
				if (tagName.equals("Filter"))
				{
				    eventType = xpp.next();
				    if (eventType != XmlPullParser.TEXT)
				    	new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				    
					Stack<MapnikFilter> filters = new Stack<MapnikFilter>();
					Stack<MapnikFilterExpression> expressions = new Stack<MapnikFilterExpression>();
					try { 
						MapnikFilterParser.compile(filters, expressions, xpp.getText());
					} catch (Exception e) {
						throw new MapnikInvalidXMLException(xpp, e.getMessage());
					}
				    rule.setFilter(filters.pop());
				    
				    eventType = xpp.next();
				    if (eventType != XmlPullParser.END_TAG)
				    	throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				}
				else if (tagName.equals("ElseFilter"))
				{
					rule.setElseFilter(true);
				    eventType = xpp.next();
				    if (eventType != XmlPullParser.END_TAG)
				    	throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				}
				else if (tagName.equals("MinScaleDenominator"))
				{
				    eventType = xpp.next();
				    if (eventType != XmlPullParser.TEXT)
				    	throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				    
				    rule.setMinScale(Double.parseDouble(xpp.getText()));
				    
				    eventType = xpp.next();
				    if (eventType != XmlPullParser.END_TAG)
				    	throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				}
				else if (tagName.equals("MaxScaleDenominator"))
				{
				    eventType = xpp.next();
				    if (eventType != XmlPullParser.TEXT)
				    	throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				    
				    rule.setMaxScale(Double.parseDouble(xpp.getText()));
				    
				    eventType = xpp.next();
				    if (eventType != XmlPullParser.END_TAG)
				    	throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				}
				else if (tagName.equals("PointSymbolizer"))
					parsePointSymbolizer(rule, xpp);

				else if (tagName.equals("LinePatternSymbolizer"))
					parseLinePatternSymbolizer(rule, xpp);

				else if (tagName.equals("PolygonPatternSymbolizer"))
					parsePolygonPatternSymbolizer(rule, xpp);

				else if (tagName.equals("TextSymbolizer"))
					parseTextSymbolizer(rule, xpp);

				else if (tagName.equals("ShieldSymbolizer"))
					parseShieldSymbolizer(rule, xpp);

				else if (tagName.equals("LineSymbolizer"))
					parseLineSymbolizer(rule, xpp);

				else if (tagName.equals("PolygonSymbolizer"))
					parsePolygonSymbolizer(rule, xpp);

				else if (tagName.equals("BuildingSymbolizer"))
					parseBuildingSymbolizer(rule, xpp);

				else if (tagName.equals("RasterSymbolizer"))
					parseRasterSymbolizer(rule, xpp);

				else if (tagName.equals("MarkersSymbolizer"))
					parseMarkersSymbolizer(rule, xpp);

				else
					throw new MapnikInvalidXMLException(xpp, "Unexpected Tag");
			}
			else  if (eventType == XmlPullParser.END_TAG)
				break;
			else
				throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
		}
	}
	
	private void parseMarkersSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException {
		// TODO
		throw new MapnikInvalidXMLException(xpp, "Marker Symbolizer not implemented");
	}

	private void parseRasterSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException {
		// TODO 
		throw new MapnikInvalidXMLException(xpp, "Raster Symbolizer not implemented");
	}

	private void parseBuildingSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException {
		// TODO 
		throw new MapnikInvalidXMLException(xpp, "Building Symbolizer not implemented");
	}

	
	private void parsePolygonSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		MapnikPolygonSymbolizer symbolizer = new MapnikPolygonSymbolizer();
		
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG)
			{
				if (xpp.getName().equals("CssParameter"))
				{
					for (int i = 0; i < xpp.getAttributeCount(); i++)
					{
						String attrName = xpp.getAttributeName(i);
						String attrValue = xpp.getAttributeValue(i);
						
						if (attrName.equals("name"))
						{
							if (attrValue.equals("fill"))
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									try
									{
									    symbolizer.getPaint().setColor(Color.parseColor(xpp.getText()));
									}
									catch (IllegalArgumentException e)
									{
										Log.e(TAG, e.toString());
										throw new MapnikInvalidXMLException(xpp, "Invalid Colour");
									}
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
								
							}
							else if (attrValue.equals("fill-opacity"))
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									symbolizer.getPaint().setAlpha((int)(255 * Float.parseFloat(xpp.getText())));
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
							}
						}
					}				
				}
				else
				    throw new MapnikInvalidXMLException(xpp, "Unexpected Tag");
			}
			else if (eventType == XmlPullParser.END_TAG && xpp.getName().equals("CssParameter"))
				continue;
			else
				break;
		}
		
		rule.appendSymbolizer(symbolizer);
	}

	private void parseLineSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		Paint paint = new Paint();
		// MapnikStroke stroke = new MapnikStroke();
		
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG)
			{
				if (xpp.getName().equals("CssParameter"))
				{
					for (int i = 0; i < xpp.getAttributeCount(); i++)
					{
						String attrName = xpp.getAttributeName(i);
						String attrValue = xpp.getAttributeValue(i);
						
						if (attrName.equals("name"))
						{
							if (attrValue.equals("stroke"))
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									try
									{
									    paint.setColor(android.graphics.Color.parseColor(xpp.getText()));
									}
									catch (IllegalArgumentException e)
									{
										Log.e(TAG, e.toString());
										throw new MapnikInvalidXMLException(xpp, "Invalid Colour");
									}
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
								
							}
							else if (attrValue.equals("stroke-width"))
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									paint.setStrokeWidth(Float.parseFloat(xpp.getText()));
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
							}
							else if (attrValue.equals("stroke-opacity"))
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									paint.setAlpha((int)(255 * Float.parseFloat(xpp.getText())));
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
							}
							else if (attrValue.equals("stroke-linejoin"))
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									String type = xpp.getText();
									if (type.equals("round"))
										paint.setStrokeJoin(Join.ROUND);
									else if (type.equals("miter"))
										paint.setStrokeJoin(Join.MITER);
									else if (type.equals("bevel"))
										paint.setStrokeJoin(Join.BEVEL);
									else
										throw new MapnikInvalidXMLException(xpp, "Invalid LineJoin: " + type);
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
							}
							else if (attrValue.equals("stroke-linecap"))
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									String type = xpp.getText();
									if (type.equals("round"))
										paint.setStrokeCap(Cap.ROUND);
									else if (type.equals("square"))
										paint.setStrokeCap(Cap.SQUARE);
									else if (type.equals("butt"))
										paint.setStrokeCap(Cap.BUTT);
									else
										throw new MapnikInvalidXMLException(xpp, "Invalid LineJoin: " + type);
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
							}
							else if (attrValue.equals("stroke-dasharray"))
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									String str = xpp.getText();
									String[] tokens = str.split(",");
									
									Vector<Float> dashes = new Vector<Float>();
									for (String t : tokens)
									{
										dashes.add(Float.parseFloat(t));
									}
									if (dashes.size() % 2 == 0)
									{
										for (String t : tokens)
										{
											dashes.add(Float.parseFloat(t));
										}
									}

									float[] myDashes = new float[dashes.size()];
									for (int n = 0; n < dashes.size(); n += 2)
									{
										myDashes[n] = dashes.get(n);
									}
									DashPathEffect pathEffect = new DashPathEffect(myDashes, (float)0);
									paint.setPathEffect(pathEffect);

									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
							}
						}
					}				
				}
				else
				    throw new MapnikInvalidXMLException(xpp, "Unexpected Tag");
			}
			else if (eventType == XmlPullParser.END_TAG && xpp.getName().equals("CssParameter"))
				continue;
			else
				break;
		}
		
		rule.appendSymbolizer(new MapnikLineSymbolizer(paint));
	}

	private void parseShieldSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String name = null;
		String faceName = null;
		int size = -1;
		int fill = Color.TRANSPARENT;
		
		String file = null;
		int min_distance = -1;
		
		LabelPlacementEnum placement = LabelPlacementEnum.POINT_PLACEMENT;
		
		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName.equals("name"))
			    name = attrValue;
			
			else if (attrName.equals("face_name"))
				faceName = attrValue;
			
			else if (attrName.equals("size"))
				size = Integer.parseInt(attrValue);
			
			else if (attrName.equals("fill"))
			{
				try {
					fill = Color.parseColor(attrValue);
				}
				catch (IllegalArgumentException e)
				{
					Log.e(TAG, e.toString());
					throw new MapnikInvalidXMLException(xpp, "Invalid Colour");
				}
			}
			
			else if (attrName.equals("file"))
				file = attrValue;
			
			else if (attrName.equals("min_distance"))
				min_distance = Integer.parseInt(attrValue);
			
			else if (attrName.equals("placement"))
			{
				if (attrValue.equals("line"))
					placement = LabelPlacementEnum.LINE_PLACEMENT;
				else if (attrValue.equals("point"))
					placement = LabelPlacementEnum.POINT_PLACEMENT;
				else
					throw new MapnikInvalidXMLException(xpp, "Invalid value for attribute: " + attrName + "(" + attrValue + ")");
			}
			
			else
			    throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
		}
		
		MapnikShieldSymbolizer symbolizer = null;
		if (name != null && faceName != null && size > 0 && file != null)
		    symbolizer = new MapnikShieldSymbolizer(name, faceName, size, fill, file);
		else
			throw new MapnikInvalidXMLException(xpp, "Missing required attributes (name, face_name, size, fill, file)");
		
		if (min_distance > 0)
			symbolizer.setMinumumDistance(min_distance);
		symbolizer.setLabelPlacement(placement);
		
		rule.appendSymbolizer(symbolizer);
		
		xpp.next();
	}

	private void parseTextSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String name = null;
		String faceName = null;
		int size = 0;
		int fill = Color.TRANSPARENT;
		
		LabelPlacementEnum placement = LabelPlacementEnum.POINT_PLACEMENT;
		int halo_fill = Color.TRANSPARENT;
		int halo_radius = -1;
		int text_ratio = -1;
		int wrap_width = -1;
		int spacing = -1;
		int min_distance = -1;
		boolean avoidEdges = false;
		boolean allowOverlap = false;
		Double maxCharAngleDelta = null;
		double[] displacement = {0.0,0.0};
		
		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName.equals("name"))
				name = attrValue;
			
			else if (attrName.equals("face_name"))
				faceName = attrValue;
			
			else if (attrName.equals("size"))
				size = Integer.parseInt(attrValue);
			
			else if (attrName.equals("fill"))
			{
				try {
					fill = Color.parseColor(attrValue);
				}
				catch (IllegalArgumentException e)
				{
					Log.e(TAG, e.toString());
					throw new MapnikInvalidXMLException(xpp, "Invalid Colour");
				}
			}
			else if (attrName.equals("placement"))
			{
				if (attrValue.equals("line"))
				    placement = LabelPlacementEnum.LINE_PLACEMENT;
				else if (attrValue.equals("point"))
					placement = LabelPlacementEnum.POINT_PLACEMENT;
				else
					throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
			}
			else if (attrName.equals("halo_fill"))
			{
				try {
				    halo_fill = Color.parseColor(attrValue);
				}
				catch (IllegalArgumentException e)
				{
					Log.e(TAG, e.toString());
					throw new MapnikInvalidXMLException(xpp, "Invalid Colour");
				}
			}
			else if (attrName.equals("halo_radius"))
				halo_radius = Integer.parseInt(attrValue);
			
			else if (attrName.equals("text_ratio"))
				text_ratio = Integer.parseInt(attrValue);
			
			else if (attrName.equals("wrap_width"))
				wrap_width = Integer.parseInt(attrValue);
			
			else if (attrName.equals("spacing"))
				spacing = Integer.parseInt(attrValue);
			
			else if (attrName.equals("avoid_edges"))
				if (attrValue.equals("true"))
					avoidEdges = true;
				else if (attrValue.equals("false"))
					avoidEdges = false;
				else
					throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
			
			else if (attrName.equals("allow_overlap"))
				if (attrValue.equals("true"))
					allowOverlap = true;
				else if (attrValue.equals("false"))
					allowOverlap = false;
				else
					throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
			
			else if (attrName.equals("max_char_angle_delta"))
				maxCharAngleDelta = Double.parseDouble(attrValue);
			
			else if (attrName.equals("dx"))
				displacement[0] = Double.parseDouble(attrValue);
			
			else if (attrName.equals("dy"))
				displacement[1] = Double.parseDouble(attrValue);
			
			else if (attrName.equals("min_distance"))
				min_distance = Integer.parseInt(attrValue);
			
			else
				throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
		}
		
		MapnikTextSymbolizer symbolizer = null;
		
		if (name != null && faceName != null && size > 0)
			symbolizer = new MapnikTextSymbolizer(name, faceName, size, fill);
		else
			throw new MapnikInvalidXMLException(xpp, "Missing required attributes (file, face_name, size, fill)");
		
		symbolizer.setLabelPlacement(placement);

		symbolizer.setHaloFill(halo_fill);
		
		symbolizer.setDisplacement(displacement);
		
		if (halo_radius != -1)
			symbolizer.setHaloRadius(halo_radius);
		if (text_ratio != -1)
			symbolizer.setTextRatio(text_ratio);
		if (wrap_width != -1)
			symbolizer.setWrapWidth(wrap_width);
		if (spacing != -1)
			symbolizer.setLabelSpacing(spacing);
		if (min_distance != -1)
			symbolizer.setMinumumDistance(min_distance);
		symbolizer.setAvoidEdges(avoidEdges);
		symbolizer.setOverlap(allowOverlap);
		if (maxCharAngleDelta != null)
		    symbolizer.setMaxCharAngleDelta(maxCharAngleDelta);
		
		rule.appendSymbolizer(symbolizer);
		
		xpp.next();
	}

	private void parsePolygonPatternSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String file = null;
		
		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName.equals("file"))
				file = attrValue;
			
			else
				throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
		}
		MapnikPolygonPatternSymbolizer symbolizer = null;
		if (file != null)
			symbolizer = new MapnikPolygonPatternSymbolizer(file);
		else
			throw new MapnikInvalidXMLException(xpp, "Missing required attributes (file)");

		rule.appendSymbolizer(symbolizer);
		xpp.next();
	}

	private void parseLinePatternSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String file = null;
		
		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName.equals("file"))
				file = attrValue;

			else
				throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
		}
		MapnikLinePatternSymbolizer symbolizer = null;
		if (file != null)
			symbolizer = new MapnikLinePatternSymbolizer(file);
		else
			throw new MapnikInvalidXMLException(xpp, "Missing required attributes (file, type, width, height)");

		rule.appendSymbolizer(symbolizer);
		xpp.next();
	}

	private void parsePointSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String file = null;
		boolean allow_overlap = true;
		
		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName.equals("file"))
				file = attrValue;
			
			else if (attrName.equals("allow_overlap"))
			{
			    if (attrValue.equals("false"))
			    	allow_overlap = false;
			    else if (attrValue.equals("true"))
			    	allow_overlap = true;
			    else
			    	throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
			}
			else
				throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
		}
		MapnikPointSymbolizer symbolizer = null;
		if (file != null)
		{
			symbolizer = new MapnikPointSymbolizer(file);
		}
		else
			symbolizer = new MapnikPointSymbolizer();

		symbolizer.setAllowOverlap(allow_overlap);
		rule.appendSymbolizer(symbolizer);
		xpp.next();
	}

	public class MapnikInvalidXMLException extends Exception
	{
		private static final long serialVersionUID = 5010677168353539997L;

		private int mLineNumber;
		private String mTag;
		private String mMessage;
		
		public MapnikInvalidXMLException(XmlPullParser xpp, String message)
		{
			mLineNumber = xpp.getLineNumber();
			mTag = xpp.getName();
			mMessage = message;
			Log.d(TAG, this.toString());
		}

		public int getLineNumber() {
			return mLineNumber;
		}

		public String getTag() {
			return mTag;
		}

		public String getMessage() {
			return mMessage;
		}
		
		public String toString()
		{
			return mMessage + " while processing " + mTag + " at line " + mLineNumber;
		}
	}
}

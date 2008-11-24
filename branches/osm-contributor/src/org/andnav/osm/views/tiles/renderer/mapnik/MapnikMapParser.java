package org.andnav.osm.views.tiles.renderer.mapnik;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikStroke.LineCapEnum;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikStroke.LineJoinEnum;
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

public class MapnikMapParser {

	private boolean mStrict;
	// TODO: Make use of Androids font system
	private HashMap<String, MapnikParameters> mDatasourceTemplates;
	
	public MapnikMapParser(boolean strict)
	{
		mStrict = strict;
		mDatasourceTemplates = new HashMap<String, MapnikParameters>();
	}
	
	public boolean parseMap(MapnikMap m, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
        	if (eventType == XmlPullParser.START_TAG)
        	{
        		if (xpp.getName() == "Map")
        		{
        			for (int i = 0; 0 < xpp.getAttributeCount(); i++)
        			{
        				if (xpp.getAttributeName(i) == "bgcolor")
        				{
        					m.setBackgroundColour(new MapnikColour(Integer.valueOf(xpp.getAttributeValue(i), 16)));
        				}
        				else if (xpp.getAttributeName(i) == "srs")
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
        					if (xpp.getName() == "Style")
        						parseStyle(m, xpp);
        					else if (xpp.getName() == "Layer")
        					{
        						parseLayer(m, xpp);
        					}
        					else if (xpp.getName() == "Datasource")
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
        	else
        		break;
        } 
        return true;
	}
	
	private void parseLayer(MapnikMap m, XmlPullParser xpp)  throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String layerName = "Unnamed";
		String status = null;
		String srs    = null;
		String clearLabelsCache = null;

		for (int i = 0; 0 < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName == "name")
				layerName = attrValue;
			
			else if (attrName == "srs")
				srs = attrValue;
			
			else if (attrName == "status" )
				status = attrValue;
			
			else if (attrName == "clear_label_cache")
				clearLabelsCache = attrValue;
			
			else
				throw new MapnikInvalidXMLException(xpp, "Unexpected attribute: " + attrName);
		}
		
		// XXX if no projection is given inherit from map? [DS]
		
		if (srs == null)
			srs = m.getSRS();
		
		MapnikLayer layer = new MapnikLayer(layerName, srs);
		
		if (status != null)
			if (status == "on")
				layer.setActive(true);
			else if (status == "off")
				layer.setActive(false);
			else
				throw new MapnikInvalidXMLException(xpp, "Unexpected attribute: status");
		
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
				if (xpp.getName() == "StyleName")
				{
					eventType = xpp.next();
					if (eventType == XmlPullParser.TEXT)
					{
						layer.addStyle(xpp.getText());
						continue;
					}
					else
						throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
					
				}
				else if (xpp.getName() == "Datasource")
				{
					MapnikParameters params = new MapnikParameters();
					
					while (eventType != XmlPullParser.END_DOCUMENT)
					{
						eventType = xpp.next();
						if (eventType == XmlPullParser.START_TAG)
						{
							if (xpp.getName() == "Parameter")
							{
								String paramName = null;
								for (int i = 0; i < xpp.getAttributeCount(); i++)
								{
									if (xpp.getAttributeName(i) == "name")
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
									continue;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
							}
						}
						else if (eventType == XmlPullParser.END_TAG && xpp.getName() == "Parameter")
						    break;
					}
				}
				else
					throw new MapnikInvalidXMLException(xpp, "Unexpected Tag");
			}
			else if (eventType == XmlPullParser.END_TAG && xpp.getName() == "StyleName")
				continue;
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
		
		for (int i = 0; 0 < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName == "name")
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
				if (xpp.getName() == "Rule")
				    parseRule(style, xpp);
				else
					throw new MapnikInvalidXMLException(xpp, "Unexpected Tag");
			}
			else  if (eventType == XmlPullParser.END_TAG)
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

		for (int i = 0; 0 < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName == "name")
				ruleName = attrValue;
			else if (attrName == "title")
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
				if (tagName == "Filter")
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
				    	new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				}
				else if (tagName == "ElseFilter")
				{
					rule.setElseFilter(true);
				}
				else if (tagName == "MinScaleDenominator")
				{
				    eventType = xpp.next();
				    if (eventType != XmlPullParser.TEXT)
				    	new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				    
				    rule.setMinScale(Double.parseDouble(xpp.getText()));
				    
				    if (eventType != XmlPullParser.END_TAG)
				    	new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				}
				else if (tagName == "MaxScaleDenominator")
				{
				    eventType = xpp.next();
				    if (eventType != XmlPullParser.TEXT)
				    	new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				    
				    rule.setMaxScale(Double.parseDouble(xpp.getText()));
				    
				    if (eventType != XmlPullParser.END_TAG)
				    	new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				}
				else if (tagName == "PointSymbolizer")
				{
					parsePointSymbolizer(rule, xpp);
				}
				else if (tagName == "LinePatternSymbolizer")
				{
					parseLinePatternSymbolizer(rule, xpp);
				}
				else if (tagName == "PolygonPatternSymbolizer")
				{
					parsePolygonPatternSymbolizer(rule, xpp);
				}
				else if (tagName == "TextSymbolizer")
				{
					parseTextSymbolizer(rule, xpp);
				}
				else if (tagName == "ShieldSymbolizer")
				{
					parseShieldSymbolizer(rule, xpp);
				}
				else if (tagName == "LineSymbolizer")
				{
					parseLineSymbolizer(rule, xpp);
				}
				else if (tagName == "PolygonSymbolizer")
				{
					parsePolygonSymbolizer(rule, xpp);
				}
				
				// Not needed/used for OSM - dont need to be implemented.
//				else if (xpp.getName() == "BuildingSymbolizer")
//				{
//					parseBuildingSymbolizer(rule, xpp); /* Not used */
//				}
//				else if (xpp.getName() == "RasterSymbolizer")
//				{
//					parseRasterSymbolizer(rule, xpp); /* Not used */
//				}
//				else if (xpp.getName() == "MarkersSymbolizer")
//				{
//					parseMarkersSymbolizer(rule, xpp); /* Not used */
//				}
				else
					throw new MapnikInvalidXMLException(xpp, "Unexpected Tag");
			}
			else  if (eventType == XmlPullParser.END_TAG)
				break;
			else
				throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
		}
	}
	
	// Not needed/used for OSM - dont need to be implemented.
//	private void parseMarkersSymbolizer(MapnikRule rule, XmlPullParser xpp) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void parseRasterSymbolizer(MapnikRule rule, XmlPullParser xpp) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void parseBuildingSymbolizer(MapnikRule rule, XmlPullParser xpp) {
//		// TODO Auto-generated method stub
//		
//	}

	
	private void parsePolygonSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		MapnikPolygonSymbolizer symbolizer = new MapnikPolygonSymbolizer();
		
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG)
			{
				if (xpp.getName() == "CssParameter")
				{
					for (int i = 0; i < xpp.getAttributeCount(); i++)
					{
						String attrName = xpp.getAttributeName(i);
						String attrValue = xpp.getAttributeValue(i);
						
						if (attrName == "name")
						{
							if (attrValue == "fill")
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									symbolizer.setFill(new MapnikColour(android.graphics.Color.parseColor(xpp.getText())));
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
								
							}
							else if (attrValue == "fill-opacity")
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									symbolizer.setOpacity(Float.parseFloat(xpp.getText()));
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
			else if (eventType == XmlPullParser.END_TAG && xpp.getName() == "CssParameter")
				continue;
			else
				break;
		}
		
		rule.appendSymbolizer(symbolizer);
	}

	private void parseLineSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		MapnikStroke stroke = new MapnikStroke();
		
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG)
			{
				if (xpp.getName() == "CssParameter")
				{
					for (int i = 0; i < xpp.getAttributeCount(); i++)
					{
						String attrName = xpp.getAttributeName(i);
						String attrValue = xpp.getAttributeValue(i);
						
						if (attrName == "name")
						{
							if (attrValue == "stroke")
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									stroke.setColour(new MapnikColour(android.graphics.Color.parseColor(xpp.getText())));
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
								
							}
							else if (attrValue == "stroke-width")
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									stroke.setWidth(Float.parseFloat(xpp.getText()));
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
							}
							else if (attrValue == "stroke-opacity")
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									stroke.setOpacity(Float.parseFloat(xpp.getText()));
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
							}
							else if (attrValue == "stroke-linejoin")
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									String type = xpp.getText();
									if (type == "round")
										stroke.setLineJoin(LineJoinEnum.ROUND_JOIN);
									else if (type == "miter")
										stroke.setLineJoin(LineJoinEnum.MITER_JOIN);
									else if (type == "miter_revert")
										stroke.setLineJoin(LineJoinEnum.MITER_REVERT_JOIN);
									else if (type == "bevel")
										stroke.setLineJoin(LineJoinEnum.BEVEL_JOIN);
									else
										throw new MapnikInvalidXMLException(xpp, "Invalid LineJoin: " + type);
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
							}
							else if (attrValue == "stroke-linecap")
							{
								eventType = xpp.next();
								if (eventType == XmlPullParser.TEXT)
								{
									String type = xpp.getText();
									if (type == "round")
										stroke.setLineCap(LineCapEnum.ROUND_CAP);
									else if (type == "square")
										stroke.setLineCap(LineCapEnum.SQUARE_CAP);
									else if (type == "butt")
										stroke.setLineCap(LineCapEnum.BUTT_CAP);
									else
										throw new MapnikInvalidXMLException(xpp, "Invalid LineJoin: " + type);
									break;
								}
								else
									throw new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
							}
							else if (attrValue == "stroke-dasharray")
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
									for (int n = 0; n < dashes.size(); n += 2)
									{
										stroke.addDash(dashes.get(n), dashes.get(n + 1));
									}

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
			else if (eventType == XmlPullParser.END_TAG && xpp.getName() == "CssParameter")
				continue;
			else
				break;
		}
		
		rule.appendSymbolizer(new MapnikLineSymbolizer(stroke));
	}

	private void parseShieldSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String name = null;
		String faceName = null;
		int size = -1;
		MapnikColour fill = null;
		
		String file = null;
		String type = null;
		int width = -1;
		int height = -1;
		int min_distance = -1;
		
		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName == "name")
			    name = attrValue;
			
			else if (attrName == "face_name")
				faceName = attrValue;
			
			else if (attrName == "size")
				size = Integer.parseInt(attrValue);
			
			else if (attrName == "fill")
				fill = new MapnikColour(android.graphics.Color.parseColor(attrValue));
			
			else if (attrName == "file")
				file = attrValue;
			
			else if (attrName == "type")
				type = attrValue;
			
			else if (attrName == "width")
				width = Integer.parseInt(attrValue);
			
			else if (attrName == "height")
				height = Integer.parseInt(attrValue);
			
			else if (attrName == "min_distance")
				min_distance = Integer.parseInt(attrValue);
			
			else
			    throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
		}
		
		MapnikShieldSymbolizer symbolizer = null;
		if (name != null && faceName != null && size > 0 && fill != null &&
			file != null && type != null && width > 0 && height > 0)
		    symbolizer = new MapnikShieldSymbolizer(name, faceName, size, fill, file, type, width, height);
		else
			throw new MapnikInvalidXMLException(xpp, "Missing required attributes (name, face_name, size, fill, file, type, width, height)");
		
		if (min_distance > 0)
			symbolizer.setMinumumDistance(min_distance);
		
		rule.appendSymbolizer(symbolizer);
	}

	private void parseTextSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String name = null;
		String faceName = null;
		int size = 0;
		MapnikColour fill = null;
		
		LabelPlacementEnum placement = LabelPlacementEnum.POINT_PLACEMENT;
		MapnikColour halo_fill = null;
		int halo_radius = -1;
		int text_ratio = -1;
		int wrap_width = -1;
		int spacing = -1;
		int min_distance = -1;
		boolean avoidEdges = false;
		boolean allowOverlap = false;
		Double maxCharAngleDelta = null;
		
		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName == "name")
				name = attrValue;
			
			else if (attrName == "face_name")
				faceName = attrValue;
			
			else if (attrName == "size")
				size = Integer.parseInt(attrValue);
			
			else if (attrName == "fill")
			    fill = new MapnikColour(android.graphics.Color.parseColor(attrValue));
			
			else if (attrName == "placement")
			{
				if (attrValue == "line")
				    placement = LabelPlacementEnum.LINE_PLACEMENT;
				else if (attrValue == "point")
					placement = LabelPlacementEnum.POINT_PLACEMENT;
				else
					throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
			}
			else if (attrName == "halo_fill")
				halo_fill = new MapnikColour(android.graphics.Color.parseColor(attrValue));
			
			else if (attrName == "halo_radius")
				halo_radius = Integer.parseInt(attrValue);
			
			else if (attrName == "text_ratio")
				text_ratio = Integer.parseInt(attrValue);
			
			else if (attrName == "wrap_width")
				wrap_width = Integer.parseInt(attrValue);
			
			else if (attrName == "spacing")
				spacing = Integer.parseInt(attrValue);
			
			else if (attrName == "avoid_edges")
				if (attrValue == "true")
					avoidEdges = true;
				else if (attrValue == "false")
					avoidEdges = false;
				else
					throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
			
			else if (attrName == "allow_overlap")
				if (attrValue == "true")
					allowOverlap = true;
				else if (attrValue == "false")
					allowOverlap = false;
				else
					throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
			
			else if (attrName == "max_char_angle_delta")
				maxCharAngleDelta = Double.parseDouble(attrValue);
			
			else
				throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
		}
		
		MapnikTextSymbolizer symbolizer = null;
		
		if (name != null && faceName != null && size > 0 && fill != null)
			symbolizer = new MapnikTextSymbolizer(name, faceName, size, fill);
		else
			throw new MapnikInvalidXMLException(xpp, "Missing required attributes (file, face_name, size, fill)");
		
		symbolizer.setLabelPlacement(placement);
		if (halo_fill != null)
			symbolizer.setHaloFill(halo_fill);
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
	}

	private void parsePolygonPatternSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String file = null;
		String type = null;
		int width   = -1;
		int height  = -1;
		
		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName == "file")
				file = attrValue;
			
			else if (attrName == "type")
				type = attrValue;
			
			else if (attrName == "width")
				width = Integer.parseInt(attrValue);
			
			else if (attrName == "height")
			    height = Integer.parseInt(attrValue);
			
			else
				throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
		}
		MapnikPolygonPatternSymbolizer symbolizer = null;
		if (file != null && type != null && width > 0 && height > 0)
			symbolizer = new MapnikPolygonPatternSymbolizer(file, type, width, height);
		else
			throw new MapnikInvalidXMLException(xpp, "Missing required attributes (file, type, width, height)");

		rule.appendSymbolizer(symbolizer);
	}

	private void parseLinePatternSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String file = null;
		String type = null;
		int width   = -1;
		int height  = -1;
		
		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName == "file")
				file = attrValue;
			
			else if (attrName == "type")
				type = attrValue;
			
			else if (attrName == "width")
				width = Integer.parseInt(attrValue);
			
			else if (attrName == "height")
			    height = Integer.parseInt(attrValue);
			
			else
				throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
		}
		MapnikLinePatternSymbolizer symbolizer = null;
		if (file != null && type != null && width > 0 && height > 0)
			symbolizer = new MapnikLinePatternSymbolizer(file, type, width, height);
		else
			throw new MapnikInvalidXMLException(xpp, "Missing required attributes (file, type, width, height)");

		rule.appendSymbolizer(symbolizer);
	}

	private void parsePointSymbolizer(MapnikRule rule, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String file = null;
		String type = null;
		int width   = -1;
		int height  = -1;
		boolean allow_overlap = true;
		
		for (int i = 0; i < xpp.getAttributeCount(); i++)
		{
			String attrName = xpp.getAttributeName(i);
			String attrValue = xpp.getAttributeValue(i);
			
			if (attrName == "file")
				file = attrValue;
			
			else if (attrName == "type")
				type = attrValue;
			
			else if (attrName == "width")
				width = Integer.parseInt(attrValue);
			
			else if (attrName == "height")
			    height = Integer.parseInt(attrValue);
			
			else if (attrName == "allow_overlap")
			{
			    if (attrValue == "false")
			    	allow_overlap = false;
			    else if (attrValue == "true")
			    	allow_overlap = true;
			    else
			    	throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
			}
			else
				throw new MapnikInvalidXMLException(xpp, "Invalid Attribute: " + attrName);
		}
		MapnikPointSymbolizer symbolizer = null;
		if (file != null && type != null && width > 0 && height > 0)
		{
			symbolizer = new MapnikPointSymbolizer(file, type, width, height);
		}
		else if (file == null && type == null && width == -1 && height == -1)
			symbolizer = new MapnikPointSymbolizer();
		else
			throw new MapnikInvalidXMLException(xpp, "Missing required attributes (file, type, width, height)");

		
		symbolizer.setAllowOverlap(allow_overlap);
		rule.appendSymbolizer(symbolizer);
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

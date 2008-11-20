package org.andnav.osm.views.tiles.renderer.mapnik;

import java.io.IOException;
import java.util.HashMap;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureTypeStyle;
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
        					m.setSource(xpp.getAttributeValue(i));
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
        						// TODO: Parse Layer Definition
        					}
        					else if (xpp.getName() == "Datasource")
        					{
        						// TODO: Parse Datasource Definition
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
	
	private void parseStyle(MapnikMap m, XmlPullParser xpp) throws XmlPullParserException, IOException, MapnikInvalidXMLException
	{
		String styleName = "<missing name>";
		
		MapnikFeatureTypeStyle style = new MapnikFeatureTypeStyle();
		
		for (int i = 0; 0 < xpp.getAttributeCount(); i++)
		{
			if (xpp.getAttributeName(i) == "name")
				styleName = xpp.getAttributeValue(i);
			else
				throw new MapnikInvalidXMLException(xpp, "Unexpected attribute: " + xpp.getAttributeName(i));
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
			if (xpp.getAttributeName(i) == "name")
				ruleName = xpp.getAttributeValue(i);
			else if (xpp.getAttributeName(i) == "title")
			    ruleTitle = xpp.getAttributeValue(i);
			else
				throw new MapnikInvalidXMLException(xpp, "Unexpected attribute: " + xpp.getAttributeName(i));
		}
		
		MapnikRule rule = new MapnikRule(ruleName, ruleTitle);
		
		int eventType = xpp.getEventType();
		
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG)
			{
				if (xpp.getName() == "Filter")
				{
				    eventType = xpp.next();
				    if (eventType != XmlPullParser.TEXT)
				    	new MapnikInvalidXMLException(xpp, "Malformed XML (Unexpected Event)");
				    
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

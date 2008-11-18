package org.andnav.osm.views.tiles.renderer.mapnik;

import java.io.IOException;
import java.util.HashMap;

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
	
	public boolean parseMap(MapnikMap m, XmlPullParser xpp) throws XmlPullParserException, IOException
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
        			}
        			while (eventType != XmlPullParser.END_DOCUMENT)
        			{
        				eventType = xpp.next();
        				if (eventType == XmlPullParser.START_TAG)
        				{
        					if (xpp.getName() == "Style")
        					{
        						// TODO: Parse Style Definition
        					}
        					else if (xpp.getName() == "Layer")
        					{
        						// TODO: Parse Layer Definition
        					}
        					else if (xpp.getName() == "Datasource")
        					{
        						// TODO: Parse Datasource Definition
        					}
        				}
        				else
        					break;
        			}
        		}
        	}
        	else
        		break;
        } 
        return true;
	}
}

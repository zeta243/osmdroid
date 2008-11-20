package org.andnav.osm.views.tiles.renderer.mapnik;

import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureAttributeDescriptor;

public class MapnikLayerDescriptor {

	private String mName;
	private String mEncoding;
	private Vector<MapnikFeatureAttributeDescriptor> mAttrDesc;

	public MapnikLayerDescriptor(String name, String encoding)
	{
		mName = name;
		mEncoding = encoding;
		mAttrDesc = new Vector<MapnikFeatureAttributeDescriptor>();
	}
	
	public MapnikLayerDescriptor(MapnikLayerDescriptor l)
	{
		mName = l.mName;
		mEncoding = l.mEncoding;
		mAttrDesc = l.mAttrDesc;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getEncoding() {
		return mEncoding;
	}

	public void setEncoding(String encoding) {
		mEncoding = encoding;
	}

	public void addAttributeDescriptor(MapnikFeatureAttributeDescriptor ad)
	{
		mAttrDesc.add(ad);
	}
	
	public Vector<MapnikFeatureAttributeDescriptor> getAttributeDescriptors() {
		return mAttrDesc;
	}
}

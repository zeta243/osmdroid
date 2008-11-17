package org.andnav.osm.views.tiles.renderer.mapnik.feature;

public class MapnikFeatureAttributeDescriptor {
	public enum AttributeType {
		Integer,
		Float,
		Double,
		String,
		Geometry,
		Object
	};
		
	private String mName;
	private AttributeType mType;
	private boolean mPrimaryKey;
	private int mSize;
	private int mPrecision;

	public MapnikFeatureAttributeDescriptor(String name, AttributeType type, boolean primaryKey, int size, int precision)
	{
		mName       = name;
		mType       = type;
		mPrimaryKey = primaryKey;
		mSize       = size;
		mPrecision  = precision;
	}
	
	public MapnikFeatureAttributeDescriptor(MapnikFeatureAttributeDescriptor fad)
	{
		mName       = fad.mName;
		mType       = fad.mType;
		mPrimaryKey = fad.mPrimaryKey;
		mSize       = fad.mSize;
		mPrecision  = fad.mPrecision;
		
	}
	
	
	public String getName() {
		return mName;
	}

	public AttributeType getType() {
		return mType;
	}

	public boolean isPrimaryKey() {
		return mPrimaryKey;
	}

	public int getSize() {
		return mSize;
	}

	public int getPrecision() {
		return mPrecision;
	}
}

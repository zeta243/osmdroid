package org.andnav.osm.views.tiles.renderer.mapnik;

import java.util.Vector;

import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;

// Original from include/mapnik/stroke.hpp
//               src/stroke.cpp

public class MapnikStroke {

	static final int DASH = 0;
	static final int GAP  = 1;

	private MapnikColour mColour;
	private float mWidth;
	private float mOpacity; // 0.0 - 1.0
	private Cap mLineCap;
	private Join mLineJoin;
	private Vector<float[]> mDash;

	public MapnikStroke() {
		mColour = new MapnikColour(0,0,0);
	    mWidth = 1;
	    mOpacity = 1;
	    mLineCap = Cap.BUTT;
	    mLineJoin = Join.MITER;
	    mDash = new Vector<float[]>();
	}

	public MapnikStroke(MapnikColour c, float width) {
		mColour = c;
		mWidth = width;
		mOpacity = 1;
	    mLineCap = Cap.BUTT;
	    mLineJoin = Join.MITER;
	    mDash = new Vector<float[]>();
	}

	public MapnikStroke(MapnikStroke s) {
		mColour = s.mColour;
		mWidth  = s.mWidth;
		mOpacity = s.mOpacity;
		mLineCap = s.mLineCap;
		mLineJoin = s.mLineJoin;
		mDash     = s.mDash;
	}
	
	public MapnikColour getColour() {
		return mColour;
	}
	
	public void setColour(MapnikColour c) {
		mColour = c;
	}

	public float getWidth() {
		return mWidth;
	}

	public void setWidth(float w) {
		mWidth = w;
	}

	public float getOpacity() {
		return mOpacity;
	}

	public void setOpacity(float o) {
		if (o > 1.0)
			mOpacity = (float)1.0;
		else if (o < 0.0)
		    mOpacity = (float)0.0;
		else
			mOpacity = o;
	}

	public Cap getLineCap() {
		return mLineCap;
	}

	public void setLineCap(Cap c) {
		mLineCap = c;
	}

	public Join getLineJoin() {
		return mLineJoin;
	}
	
	public void setLineJoin(Join j)
	{
		mLineJoin = j;
	}

	public void addDash(float dash, float gap) {
		float[] d = new float[2];
		d[DASH] = dash;
		d[GAP]  = gap;
		mDash.add(d);
	}

	public boolean hasDash() {
		if (mDash.isEmpty())
			return false;
		return true;
	}

	public Vector<float[]> getDashArray() {
		return mDash;
	}
}

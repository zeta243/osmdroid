package org.andnav.osm.views.tiles.renderer.mapnik;

// Original from include/mapnik/color.hpp
// Status: Complete

public class MapnikColour {

	private int m_argb;

	public MapnikColour() {
		m_argb = 0xffffffff;
	}

	public MapnikColour(int red, int green, int blue, int alpha) {
		m_argb = ((alpha & 0xff) << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff));
	}
	
	public MapnikColour(int red, int green, int blue) {
		int alpha = 0xff;
		m_argb = ((alpha & 0xff) << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff));
	}

	public MapnikColour(int argb) {
		m_argb = argb;
	}

	public MapnikColour(MapnikColour c) {
		m_argb = c.m_argb;
	}
	
	public int getAlpha() {
		return (m_argb >> 24) & 0xff;
	}

	public int getRed() {
		return (m_argb) >> 16 & 0xff;
	}

	public int getGreen() {
		return (m_argb >> 8) & 0xff;
	}

	public int getBlue() {
		return m_argb & 0xff;
	}

	public void setAlpha(int a) {
		m_argb = (m_argb & 0x00ffffff | (a & 0xff) << 24);
	}
	
	public void setRed(int b) {
		m_argb = (m_argb & 0xff00ffff) | ((b & 0xff) << 16);
	}

	public void setGreen(int g) {
		m_argb = (m_argb & 0xffff00ff) | ((g & 0xff) << 8);
	}
	
	public void setBlue(int r) {
		m_argb = (m_argb & 0xffffff00) | (r & 0xff);
	}

	public int getARGB() {
		return m_argb;
	}

	public void setBGR(int rgb) {
		m_argb = (m_argb & 0xff000000) | (rgb & 0xffffff);
	}

	public boolean equals(final MapnikColour c) {
		if (m_argb == c.m_argb)
			return true;
		return false;
	}

	public String toString() {
		return "rgb (" + getRed() + "," + getGreen() + "," + getBlue() + "," + getAlpha() + ")";
	}

	public String toHexString() {
		return String.format("#%2x%2x%2x", getRed(), getGreen(), getBlue());
	}

}

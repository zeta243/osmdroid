package org.andnav.osm.renderer.mapnik;

// Original from include/mapnik/color.hpp
// Status: Complete

public class MapnikColour {

    private int m_abgr;
  
    MapnikColour()
    {
    	m_abgr = 0xffffffff;
    }
    
    MapnikColour(int red, int green, int blue, int alpha)
    {    	
    	m_abgr = ((alpha & 0xff) << 24 |
    			  (blue  & 0xff) << 16 |
    			  (green & 0xff) << 8  |
    			  (red   & 0xff));
    }
    
    MapnikColour(int abgr)
    {
    	m_abgr = abgr;
    }

    MapnikColour(MapnikColour c)
    {
    	m_abgr = c.m_abgr;
    }
    
    public int getRed()
    {
    	return m_abgr & 0xff;
    }
    
    public int getGreen()
    {
    	return (m_abgr >> 8) & 0xff;
    }
    
    public int getBlue()
    {
    	return (m_abgr) >> 16 & 0xff;
    }
    
    public int getAlpha()
    {
    	return (m_abgr >> 24) & 0xff;
    }
    
    public void setRed(int r)
    {
    	m_abgr = (m_abgr & 0xffffff00) | (r & 0xff);
    }
    
    public void setGreen(int g)
    {
    	m_abgr = (m_abgr & 0xffff00ff) | ((g & 0xff) << 8);
    }
    
    public void setBlue(int b)
    {
    	m_abgr = (m_abgr & 0xff00ffff) | ((b & 0xff) << 16);
    }
    
    public void setAlpha(int a)
    {
    	m_abgr = (m_abgr & 0x00ffffff | (a & 0xff) << 24);
    }
    
    public int getRGBA()
    {
    	return m_abgr;
    }
    
    public void setBGR(int bgr)
    {
    	m_abgr = (m_abgr & 0xff000000) | (bgr & 0xffffff);
    }
    
    public boolean equals(final MapnikColour c)
    {
    	if (m_abgr == c.m_abgr)
    		return true;
    	return false;
    }
    
    public String toString()
    {
    	return "rgb (" + getRed() + "," + getGreen() + "," + getBlue() + "," + getAlpha() + ")";
    }
    
    public String toHexString()
    {
    	return String.format("#%2x%2x%2x", getRed(), getGreen(), getBlue());
    }


}

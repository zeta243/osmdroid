package org.andnav.osm.views.tiles.renderer.mapnik;

import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilter;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterAll;
import org.andnav.osm.views.tiles.renderer.mapnik.filter.MapnikFilterVisitor;
import org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikSymbolizer;

// Original from include/mapnik/rule.hpp

public class MapnikRule {

	private String mName;
	private String mTitle;
	private String mAbstract;
	private double mMinScale;
	private double mMaxScale;
	private Vector<MapnikSymbolizer> mSymbolizerVector;
	private MapnikFilter mFilter;
	private boolean mElseFilter;
	
	public MapnikRule()
	{
		mName = new String();
		mTitle = new String();
		mAbstract = new String();
		mMinScale = 0;
		mMaxScale = Double.POSITIVE_INFINITY;
		mSymbolizerVector = new Vector<MapnikSymbolizer>();
		mFilter = new MapnikFilterAll();
		mElseFilter = false;
	}
	
	public MapnikRule(String name,
			          String title,
			          double minScaleDenominator,
			          double maxScaleDenominator)
	{
		mName = name;
		mTitle = title;
		mAbstract = new String();
		mMinScale = minScaleDenominator;
		mMaxScale = maxScaleDenominator;
		mSymbolizerVector = new Vector<MapnikSymbolizer>();
		mFilter = new MapnikFilterAll();
		mElseFilter = false;
	}
	
	public MapnikRule(MapnikRule r)
	{
		mName = r.mName;
		mTitle = r.mTitle;
		mAbstract = r.mAbstract;
		mMinScale = r.mMinScale;
		mMaxScale = r.mMaxScale;
		mSymbolizerVector = r.mSymbolizerVector;
		mFilter = r.mFilter;
		mElseFilter = r.mElseFilter;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getAbstract() {
		return mAbstract;
	}

	public void setAbstract(String abstract1) {
		mAbstract = abstract1;
	}

	public double getMinScale() {
		return mMinScale;
	}

	public void setMinScale(double minScale) {
		mMinScale = minScale;
	}

	public double getMaxScale() {
		return mMaxScale;
	}

	public void setMaxScale(double maxScale) {
		mMaxScale = maxScale;
	}

	public MapnikFilter getFilter() {
		return mFilter;
	}

	public void setFilter(MapnikFilter filter) {
		mFilter = filter;
	}

	public boolean isElseFilter() {
		return mElseFilter;
	}

	public void setElseFilter(boolean elseFilter) {
		mElseFilter = elseFilter;
	}

	public void appendSymbolizer(MapnikSymbolizer s)
	{
		mSymbolizerVector.add(s);
	}
	
	public void removeSymbolizer(int location)
	{
		mSymbolizerVector.remove(location);
	}
	
	public Vector<MapnikSymbolizer> getSymbolizers()
	{
		return mSymbolizerVector;
	}
	
	public boolean active(double scale)
	{
		return ( scale >= mMinScale - 1e-6 && scale < mMaxScale + 1e-6);
	}
	
    public void accept(MapnikFilterVisitor v)
    {
       v.visit(this);
    }
}

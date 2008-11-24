package org.andnav.osm.views.tiles.renderer.mapnik;

import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.datasource.MapnikDataSource;

public class MapnikLayer {

	private String mName;
	private String mTitle;
	private String mAbstract;
	private String mSrs;
	
	private double mMinZoom;
	private double mMaxZoom;
	
	private boolean mActive;
	private boolean mQueryable;
	private boolean mClearLabelCache;
	
	private Vector<String> mStyles;
	
	private MapnikDataSource mDataSource;

	public MapnikLayer(String name, String srs)
	{
		mName = name;
		mTitle = "";
		mAbstract = "";
		mSrs      = srs;
		mMinZoom  = 0;
		mMaxZoom = Double.POSITIVE_INFINITY;
		mActive = true;
		mQueryable = false;
		mClearLabelCache = false;
		mStyles = new Vector<String>();
		mDataSource = null;
	}
	
	public MapnikLayer(MapnikLayer l)
	{
		mName = l.mName;
		mTitle = l.mTitle;
		mAbstract = l.mAbstract;
		mSrs      = l.mSrs;
		mMinZoom  = l.mMinZoom;
		mMaxZoom  = l.mMaxZoom;
		mActive   = l.mActive;
		mQueryable = l.mQueryable;
		mClearLabelCache = l.mClearLabelCache;
		mStyles     = l.mStyles;
		mDataSource = l.mDataSource;
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

	public String getSRS() {
		return mSrs;
	}

	public void setSRS(String srs) {
		mSrs = srs;
	}

	public double getMinZoom() {
		return mMinZoom;
	}

	public void setMinZoom(double minZoom) {
		mMinZoom = minZoom;
	}

	public double getMaxZoom() {
		return mMaxZoom;
	}

	public void setMaxZoom(double maxZoom) {
		mMaxZoom = maxZoom;
	}

	public boolean isActive() {
		return mActive;
	}

	public void setActive(boolean active) {
		mActive = active;
	}

	public boolean isQueryable() {
		return mQueryable;
	}

	public void setQueryable(boolean queryable) {
		mQueryable = queryable;
	}

	public boolean isClearLabelCache() {
		return mClearLabelCache;
	}

	public void setClearLabelCache(boolean clearLabelCache) {
		mClearLabelCache = clearLabelCache;
	}

	public MapnikDataSource getDataSource() {
		return mDataSource;
	}

	public void setDataSource(MapnikDataSource dataSource) {
		mDataSource = dataSource;
	}

	public Vector<String> getStyles() {
		return mStyles;
	}
	
	public void addStyle(String style)
	{
		mStyles.add(style);
	}
	
	public boolean isVisible(double scale)
	{
		return isActive() && scale >= mMinZoom - 1e-6 && scale < mMaxZoom + 1e-6;
	}
	
	public MapnikEnvelope getEnvelope()
	{
		if (this.mDataSource != null)
		{
			return mDataSource.getEnvelope();
		}
		return new MapnikEnvelope();
	}
}

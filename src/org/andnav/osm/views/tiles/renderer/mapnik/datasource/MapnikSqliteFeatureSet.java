package org.andnav.osm.views.tiles.renderer.mapnik.datasource;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureSet;

import android.database.Cursor;

public class MapnikSqliteFeatureSet extends MapnikFeatureSet {
	
	private Cursor mCursor;
	MapnikSqliteDatasource mDatasource;
	
	public MapnikSqliteFeatureSet(MapnikSqliteDatasource d, Cursor c)
	{
		super();
		mDatasource = d;
		mCursor = c;
	}

	public void rewind()
	{
		mCursor.requery();
	}

	public MapnikFeature getNext()
	{
		if (mCursor.moveToNext())
		    return mDatasource.getFeature(mCursor.getInt(0));
		return null;
	}
	
	public void finalize() throws Throwable
	{
		mCursor.close();
	}
}

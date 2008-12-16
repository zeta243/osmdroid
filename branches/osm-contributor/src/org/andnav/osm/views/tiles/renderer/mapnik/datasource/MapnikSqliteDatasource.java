package org.andnav.osm.views.tiles.renderer.mapnik.datasource;

import java.util.HashMap;
import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikEnvelope;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikLayerDescriptor;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterDoubleValue;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterIntValue;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameterStringValue;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikParameters;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureAttributeDescriptor;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureSet;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeatureAttributeDescriptor.AttributeType;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikGeometry;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikLineString;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikPoint;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikPolygon;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex.VertexCommandType;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class MapnikSqliteDatasource  extends MapnikDataSource {

	private static final String TAG = "MapnikSqliteDatasource";
	
	private enum GeoType {
		Point,
		Line,
		Polygon
	}
	
	private static final String mDatabaseName = "map";
	private String mTableName;
	private String mTableFields;
	private String mTableWhere;
	private String mTableOrder;
	private GeoType mGeoType;
	private MapnikDataSourceType mType;
	private MapnikEnvelope mEnvelope;
	private MapnikLayerDescriptor mLayerDescriptor;
	private String mName;
	private SQLiteDatabase mDatabase;
	
	private static HashMap<String, SqliteQueryCache> queryCache = new HashMap<String, SqliteQueryCache>(); 
	
	public MapnikSqliteDatasource(MapnikParameters p) throws IllegalArgumentException, SQLiteException {
		super(p);

		mTableName = p.getString("table_name", null);
		mTableFields = p.getString("table_fields", null);
		mTableWhere  = p.getString("table_where", null);
		mTableOrder  = p.getString("table_order_by", null);

		if (mTableName == null || mTableFields == null)
		    throw new IllegalArgumentException("Invalid Database Parameters");
		
		mType = MapnikDataSourceType.Vector;
		mLayerDescriptor = new MapnikLayerDescriptor(p.getString("type", null), "utf-8");
		
		mDatabase = SQLiteDatabase.openDatabase("/sdcard/OSM/" + mDatabaseName + ".sqlite", null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		
		String ext = p.getString("extent", null);
		if (ext != null)
		{
			String[] parts = ext.split(",", 4);
			mEnvelope = new MapnikEnvelope(Double.parseDouble(parts[0]),
					                       Double.parseDouble(parts[1]),
					                       Double.parseDouble(parts[2]),
					                       Double.parseDouble(parts[3]));
		}
		
		if (mDatabase != null)
		{
			Cursor c = mDatabase.rawQuery("SELECT * FROM " + mTableName + " LIMIT 0", null);
			// There's no propper way to find out the data type of each column in Android :-(
			// therefore we have to infer type based on column name
			String[] names = c.getColumnNames();
			for (int i = 0; i < names.length; i++)
			{
				if (names[i].equals("osm_id"))
					mLayerDescriptor.addAttributeDescriptor(new MapnikFeatureAttributeDescriptor(names[i], AttributeType.Integer, true, 0, 0, i));
				else if (names[i].equals("z_order"))
					mLayerDescriptor.addAttributeDescriptor(new MapnikFeatureAttributeDescriptor(names[i], AttributeType.Integer, false, 0, 0, i));
				else if (names[i].equals("way_area"))
					mLayerDescriptor.addAttributeDescriptor(new MapnikFeatureAttributeDescriptor(names[i], AttributeType.Double, false, 0, 0, i));
				else
					mLayerDescriptor.addAttributeDescriptor(new MapnikFeatureAttributeDescriptor(names[i], AttributeType.String, false, 0, 0, i));
			}
			c.close();
		}
		
		if (mTableName.endsWith("_line"))
			this.mGeoType = GeoType.Line;
		else if (mTableName.endsWith("_polygon"))
			this.mGeoType = GeoType.Polygon;
		else this.mGeoType = GeoType.Point;
	}
	
	public String getName()
	{
		return mName;
	}

	@Override
	public MapnikEnvelope getEnvelope() {
		return mEnvelope;
	}

	@Override
	public MapnikFeatureSet getFeatures(MapnikQuery query) {

		Cursor c = null;
		SqliteQueryCache cache = queryCache.get(mTableName);

		if (cache != null && (c = cache.getCursor(query)) != null)
			return new MapnikSqliteFeatureSet(this, c);
		else if (cache == null)
		{
			cache = new SqliteQueryCache(mTableName);
			queryCache.put(mTableName, cache);
		}
			
		MapnikEnvelope box = query.getBoundingBox();
		
		String sql = "SELECT DISTINCT " + mTableName + "_id " +
		               " FROM " + mTableName + "_coords " +
		               " WHERE x > " + box.getMinX() + " AND " +
		               "       x < " + box.getMaxX() + " AND " +
		               "       y > " + box.getMinY() + " AND " +
		               "       y < " + box.getMaxY();
		Log.d(TAG, "Executing SQL Query: " + sql);
		c = mDatabase.rawQuery(sql, null);
		
		cache.setCursor(c, query);
		Log.d(TAG, "Query returned " + c.getCount() + " rows");
		return new MapnikSqliteFeatureSet(this, c);
	}

	@Override
	public MapnikFeatureSet getFeaturesAtPoint(double[] coords) {

		Cursor c = null;
		SqliteQueryCache cache = queryCache.get(mTableName);

		if (cache != null && (c = cache.getCursor(coords)) != null)
			return new MapnikSqliteFeatureSet(this, c);
		else if (cache == null)
		{
			cache = new SqliteQueryCache(mTableName);
			queryCache.put(mTableName, cache);
		}
		
		String sql = "SELECT DISTINCT " + mTableName + "_id " +
		" FROM " + mTableName + "_coords " +
		" WHERE x > " + coords[0] + " AND " +
		"       x < " + coords[0] + " AND " +
		"       y > " + coords[1] + " AND " +
		"       y < " + coords[1];
		c = mDatabase.rawQuery(sql, null);
		
		cache.setCursor(c, coords);

		return new MapnikSqliteFeatureSet(this, c);
		
	}

	@Override
	public MapnikLayerDescriptor getLayerDescriptor() {
		return this.mLayerDescriptor;
	}

	@Override
	public MapnikDataSourceType getType() {
		return mType;
	}

	public String getTableName() {
		return mTableName;
	}

	public String getTableFields() {
		return mTableFields;
	}

	public String getTableWhere() {
		return mTableWhere;
	}

	public String getTableOrder() {
		return mTableOrder;
	}

	public MapnikFeature getFeature(int id) {
		
		Cursor c = mDatabase.rawQuery("SELECT * FROM " + mTableName + " WHERE osm_id = " + id, null);
		
		MapnikFeature f = new MapnikFeature(id);
		
		Vector<MapnikFeatureAttributeDescriptor> fields = mLayerDescriptor.getAttributeDescriptors();
		
		for (MapnikFeatureAttributeDescriptor field : fields)
		{
			if (field.getType() == AttributeType.String)
				f.setProperty(field.getName(), new MapnikParameterStringValue(c.getString(field.getColumn())));
		    else if (field.getType() == AttributeType.Integer)
			    f.setProperty(field.getName(), new MapnikParameterIntValue(c.getInt(field.getColumn())));
		    else if (field.getType() == AttributeType.Double)
		    	f.setProperty(field.getName(), new MapnikParameterDoubleValue(c.getDouble(field.getColumn())));
		}
		
		c.close();
		
		MapnikGeometry g = null;
		if (mGeoType == GeoType.Line)
		{
			g = new MapnikLineString();
		}
		else if (mGeoType == GeoType.Polygon)
			g = new MapnikPolygon();
		else
			g = new MapnikPoint();
		
		c = mDatabase.rawQuery("SELECT x, y FROM " + mTableName + "_coords WHERE " + mTableName + "_id = " + id, null);
		
		g.setCapacity(c.getCount());
		
		c.moveToNext();
		g.lineTo(new MapnikVertex(c.getDouble(0), c.getDouble(1), VertexCommandType.SEG_MOVETO));
		while (c.moveToNext())
		{	
			g.lineTo(new MapnikVertex(c.getDouble(0), c.getDouble(1), VertexCommandType.SEG_LINETO));	
		}

		f.addGeometry(g);
		
		return f;
	}
	
	public class SqliteQueryCache
	{
		public String mTableName;
		public Cursor mCursor;
		public double[] mCoords;
		public MapnikQuery mQuery;
		
		public SqliteQueryCache(String tableName)
		{
			mTableName = tableName;
			mCursor = null;
			mCoords = null;
			mQuery = null;
		}
		
		public Cursor getCursor(double[] coords)
		{
			if (mCursor != null && coords != null && 
			    coords[0] == mCoords[0] && coords[1] == mCoords[1])
			{
				mCursor.moveToFirst();
				return mCursor;
			}
			return null;
		}
		
		public Cursor getCursor(MapnikQuery q)
		{
			if (mCursor != null && mQuery != null)
			{
				MapnikEnvelope qe = q.getBoundingBox();
				MapnikEnvelope me = mQuery.getBoundingBox();
				if (qe.getMaxX() == me.getMaxX() &&
				    qe.getMaxY() == me.getMaxY() &&
				    qe.getMinX() == me.getMinX() &&
				    qe.getMinY() == me.getMinY())
				{
				    mCursor.moveToFirst();
				    return mCursor;
				}
			}
			return null;
		}
		
		public void setCursor(Cursor c, double[] coords)
		{
			mCoords = coords;
			mCursor = c;
			mQuery  = null;
		}
		
		public void setCursor(Cursor c, MapnikQuery q)
		{
			mCoords = null;
			mCursor = c;
			mQuery  = q;
		}
	}
}

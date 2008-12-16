package org.andnav.osm.views.tiles.renderer;

import java.text.SimpleDateFormat;

import org.andnav.osm.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class OSMDatabase implements OSMDatabaseConstants {

	private static final String TAG = "OSMDatabase";
		// ===========================================================
		// Fields
		// ===========================================================

		protected final Context mCtx;
		protected final SQLiteDatabase mDatabase;
		protected final SimpleDateFormat DATE_FORMAT_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

		// ===========================================================
		// Constructors
		// ===========================================================

		public OSMDatabase(final Context context) {
			this.mCtx = context;
			String db_path = "/sdcard/" + context.getString(R.string.app_name) + "/maps/map.db";
			Log.d(TAG, "Opening " + db_path);
			this.mDatabase = SQLiteDatabase.openDatabase(db_path,
					null, SQLiteDatabase.OPEN_READONLY);
			
		}
	}


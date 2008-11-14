package org.andnav.osm.renderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.andnav.osm.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
			this.mDatabase = SQLiteDatabase.openDatabase("/sdcard/" + context.getString(R.string.app_name) + "/maps/map.db",
					null, SQLiteDatabase.OPEN_READONLY);
		}
	}


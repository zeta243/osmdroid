package org.andnav.osm.views.overlay;

import org.andnav.osm.adt.GeoPoint;
import org.andnav.osm.views.OSMMapView;
import org.andnav.osm.views.OSMMapView.OpenStreetMapViewProjection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class OSMMapViewLinearOverlay extends OSMMapViewOverlay {
	
	protected final Paint mPaint = new Paint();
	
	protected GeoPoint mStartPoint;
	protected GeoPoint mEndPoint;
	
	public OSMMapViewLinearOverlay(final Context ctx){
		mPaint.setARGB(128, 0, 0, 0);
	}
	
	public OSMMapViewLinearOverlay(final Context ctx, GeoPoint start, GeoPoint end){
		mPaint.setARGB(128, 0, 0, 0);
		mPaint.setStrokeWidth(0);
		mStartPoint = start;
		mEndPoint = end;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	protected void onDrawFinished(Canvas c, OSMMapView osmv) {
		return;
	}
	
	@Override
	public void onDraw(final Canvas c, final OSMMapView osmv) {
		
		if(this.mStartPoint != null && this.mEndPoint != null)
		{
			final OpenStreetMapViewProjection pj = osmv.getProjection();
			final Point startCoords = new Point();
			final Point endCoords   = new Point();
			
			pj.toPixels(this.mStartPoint, startCoords);
			pj.toPixels(this.mEndPoint, endCoords);
			
			c.drawLine(startCoords.x, startCoords.y, endCoords.x, endCoords.y, mPaint);
		}
	}
	
	public void setPoints(GeoPoint start, GeoPoint end)
	{
		mStartPoint = start;
		mEndPoint   = end;
	}
}

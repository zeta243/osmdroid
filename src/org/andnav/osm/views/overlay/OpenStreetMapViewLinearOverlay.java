package org.andnav.osm.views.overlay;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class OpenStreetMapViewLinearOverlay extends OpenStreetMapViewOverlay {
	
	protected final Paint mPaint = new Paint();
	
	protected GeoPoint mStartPoint;
	protected GeoPoint mEndPoint;
	
	public OpenStreetMapViewLinearOverlay(final Context ctx){
		mPaint.setARGB(128, 0, 0, 0);
	}
	
	public OpenStreetMapViewLinearOverlay(final Context ctx, GeoPoint start, GeoPoint end){
		mPaint.setARGB(128, 0, 0, 0);
		mPaint.setStrokeWidth(0);
		mStartPoint = start;
		mEndPoint = end;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	protected void onDrawFinished(Canvas c, OpenStreetMapView osmv) {
		return;
	}
	
	@Override
	public void onDraw(final Canvas c, final OpenStreetMapView osmv) {
		
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

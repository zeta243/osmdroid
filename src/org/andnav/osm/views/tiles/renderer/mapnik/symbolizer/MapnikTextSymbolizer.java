package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import java.util.Vector;

import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikCoordTransformer;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikGeometry;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikVertex;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Typeface;
import android.graphics.Paint.Align;

public class MapnikTextSymbolizer extends MapnikSymbolizer {

	public enum LabelPlacementEnum
	{
		POINT_PLACEMENT,
		LINE_PLACEMENT,
	}
	
	protected Typeface mTypeface;
	protected Paint mPaint;
	protected String mName;
	protected String mFaceName;
	
	protected int mSize;
	protected int mTextRatio;
	protected int mWrapWidth;
	protected int mLabelSpacing;
	protected int mLabelPositionTolerance;
	protected boolean mForceOddLabels;
	protected double mMaxCharAngleDelta;
	protected int mFill;
	protected int mHaloFill;
	protected int mHaloRadius;
	protected LabelPlacementEnum mLabelPlacement;
	protected double[] mAnchor = new double[2];
	protected double[] mDisplacement = new double[2];
	protected boolean mAvoidEdges;
	protected double mMinumumDistance;
	protected boolean mOverlap;
	
	public MapnikTextSymbolizer(String name, String faceName, int size, int colour)
	{
		mPaint = new Paint();		
		
		mTypeface = Typeface.create(faceName, Typeface.NORMAL);
		
		mPaint.setTypeface(mTypeface);
		mPaint.setTextSize(size);
		mPaint.setColor(colour);
		mPaint.setTextAlign(Align.CENTER);
		
		mName         = name;
		mFaceName     = faceName;
		mSize         = size;
		mTextRatio    = 0;
		mWrapWidth    = 0;
		mLabelSpacing = 0;
		mLabelPositionTolerance = 0;
		mForceOddLabels = false;
		mMaxCharAngleDelta = 0;
		mFill         = colour;
		mHaloFill     = Color.BLACK;
		mHaloRadius   = 0;
		mLabelPlacement = LabelPlacementEnum.POINT_PLACEMENT;
		mAnchor[0]    = 0;
		mAnchor[1]    = 0.5;
		mDisplacement[0] = 0;
		mDisplacement[1] = 0;
		mAvoidEdges   = false;
		mMinumumDistance = 0;
		mOverlap      = false;
	}
	
	public MapnikTextSymbolizer(MapnikTextSymbolizer s)
	{
		mPaint        = s.mPaint;
		mTypeface     = s.mTypeface;
		mName         = s.mName;
		mFaceName     = s.mFaceName;
		mSize         = s.mSize;
		mTextRatio    = s.mTextRatio;
		mWrapWidth    = s.mWrapWidth;
		mLabelSpacing = s.mLabelSpacing;
		mLabelPositionTolerance = s.mLabelPositionTolerance;
		mForceOddLabels = s.mForceOddLabels;
		mMaxCharAngleDelta = s.mMaxCharAngleDelta;
		mFill         = s.mFill;
		mHaloFill     = s.mHaloFill;
		mHaloRadius   = s.mHaloRadius;
		mLabelPlacement = s.mLabelPlacement;
		mAnchor[0]    = s.mAnchor[0];
		mAnchor[1]    = s.mAnchor[1];
		mDisplacement[0] = s.mDisplacement[0];
		mDisplacement[1] = s.mDisplacement[1];
		mAvoidEdges   = s.mAvoidEdges;
		this.mMinumumDistance = s.mMinumumDistance;
		mOverlap      = s.mOverlap;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getFaceName() {
		return mFaceName;
	}

	public void setFaceName(String faceName) {
		mFaceName = faceName;
	}

	public int getSize() {
		return mSize;
	}

	public void setSize(int size) {
		mSize = size;
	}

	public int getTextRatio() {
		return mTextRatio;
	}

	public void setTextRatio(int textRatio) {
		mTextRatio = textRatio;
	}

	public int getWrapWidth() {
		return mWrapWidth;
	}

	public void setWrapWidth(int wrapWidth) {
		mWrapWidth = wrapWidth;
	}

	public int getLabelSpacing() {
		return mLabelSpacing;
	}

	public void setLabelSpacing(int labelSpacing) {
		mLabelSpacing = labelSpacing;
	}

	public int getLabelPositionTolerance() {
		return mLabelPositionTolerance;
	}

	public void setLabelPositionTolerance(int labelPositionTolerance) {
		mLabelPositionTolerance = labelPositionTolerance;
	}

	public boolean isForceOddLabels() {
		return mForceOddLabels;
	}

	public void setForceOddLabels(boolean forceOddLabels) {
		mForceOddLabels = forceOddLabels;
	}

	public double getMaxCharAngleDelta() {
		return mMaxCharAngleDelta;
	}

	public void setMaxCharAngleDelta(double maxCharAngleDelta) {
		mMaxCharAngleDelta = maxCharAngleDelta;
	}

	public int getFill() {
		return mFill;
	}

	public void setFill(int fill) {
		mFill = fill;
	}

	public int getHaloFill() {
		return mHaloFill;
	}

	public void setHaloFill(int haloFill) {
		mHaloFill = haloFill;
	}

	public int getHaloRadius() {
		return mHaloRadius;
	}

	public void setHaloRadius(int haloRadius) {
		mHaloRadius = haloRadius;
	}

	public LabelPlacementEnum getLabelPlacement() {
		return mLabelPlacement;
	}

	public void setLabelPlacement(LabelPlacementEnum labelPlacement) {
		mLabelPlacement = labelPlacement;
	}

	public double[] getAnchor() {
		return mAnchor;
	}

	public void setAnchor(double[] anchor) {
		mAnchor = anchor;
	}

	public double[] getDisplacement() {
		return mDisplacement;
	}

	public void setDisplacement(double[] displacement) {
		mDisplacement = displacement;
	}

	public boolean isAvoidEdges() {
		return mAvoidEdges;
	}

	public void setAvoidEdges(boolean avoidEdges) {
		mAvoidEdges = avoidEdges;
	}

	public double getMinumumDistance() {
		return mMinumumDistance;
	}

	public void setMinumumDistance(double minumumDistance) {
		mMinumumDistance = minumumDistance;
	}

	public boolean isOverlap() {
		return mOverlap;
	}

	public void setOverlap(boolean overlap) {
		mOverlap = overlap;
	}
	
	@Override
	public void draw(Canvas canvas, MapnikCoordTransformer transformer,
			MapnikFeature feature) throws Exception {
		
		Vector<MapnikGeometry> geoms = feature.getGeometries();
		
		String value = feature.getProperties().get(this.mName).toString();
		float characterWidths[] = new float[value.length()];
		mPaint.getTextWidths(value, characterWidths);
		
		float stringWidth = 0;
        for (float n : characterWidths)
        	stringWidth += n;

		for (MapnikGeometry g : geoms)
		{
			if (g.numPoints() > 0)
			{
				Path path = this.getPath(feature, transformer);
				PathMeasure measure = new PathMeasure(path, false);
				float pathLen = measure.getLength();
				Matrix matrix = new Matrix();
				
			    if (mLabelPlacement == LabelPlacementEnum.POINT_PLACEMENT)
			    {
				    final int flags = PathMeasure.POSITION_MATRIX_FLAG;
				    measure.getMatrix(pathLen / 2, matrix, flags);
				    
				    g.rewind(0);
				    MapnikVertex v = g.getNextVertex();
				    double[] coords = new double[2];
				    
				    coords[0] = v.x;
				    coords[1] = v.y;
				    
				    transformer.forward(coords);
				    float newcoords[] = new float[2];
				    
				    newcoords[0] = (float)coords[0];
				    newcoords[1] = (float)coords[1];
				    matrix.mapPoints(newcoords);
				    
			    	canvas.drawText(value, newcoords[0], newcoords[1], mPaint);
			    }
			    else /* LabelPlacementEnum.LINE_PLACEMENT */
			    {
			    	if (g.numPoints() > 2)
			    	{
			    		float currentOffset = stringWidth / 2;
			    		while (currentOffset < pathLen)
			    		{
			    			float Y_offset = (Math.abs(mPaint.ascent()) - mPaint.descent()) / 2;
			    			canvas.drawTextOnPath(mName, path, currentOffset, Y_offset, mPaint);
			    			currentOffset += stringWidth * 1.5;
			    		}
			    	}
			    }
			}
		}
	}
}

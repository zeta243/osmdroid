package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikColour;

public class MapnikTextSymbolizer extends MapnikSymbolizer {

	public enum LabelPlacementEnum
	{
		POINT_PLACEMENT,
		LINE_PLACEMENT,
	}
	
	private String mName;
	private String mFaceName;
	private int mSize;
	private int mTextRatio;
	private int mWrapWidth;
	private int mLabelSpacing;
	private int mLabelPositionTolerance;
	private boolean mForceOddLabels;
	private double mMaxCharAngleDelta;
	private MapnikColour mFill;
	private MapnikColour mHaloFill;
	private int mHaloRadius;
	private LabelPlacementEnum mLabelPlacement;
	private double[] mAnchor = new double[2];
	private double[] mDisplacement = new double[2];
	boolean mAvoidEdges;
	double mMinumumDistance;
	boolean mOverlap;
	
	public MapnikTextSymbolizer(String name, String faceName, int size, MapnikColour fill)
	{
		mName         = name;
		mFaceName     = faceName;
		mSize         = size;
		mTextRatio    = 0;
		mWrapWidth    = 0;
		mLabelSpacing = 0;
		mLabelPositionTolerance = 0;
		mForceOddLabels = false;
		mMaxCharAngleDelta = 0;
		mFill         = fill;
		mHaloFill     = new MapnikColour(255,255,255);
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

	public MapnikColour getFill() {
		return mFill;
	}

	public void setFill(MapnikColour fill) {
		mFill = fill;
	}

	public MapnikColour getHaloFill() {
		return mHaloFill;
	}

	public void setHaloFill(MapnikColour haloFill) {
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
	
/*



 */
}

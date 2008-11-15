package org.andnav.osm.views.tiles.renderer.mapnik;

import java.util.Vector;

// Original from include/mapnik/stroke.hpp
//               src/stroke.cpp

public class MapnikStroke {

	public enum LineCapEnum {
		BUTT_CAP, SQUARE_CAP, ROUND_CAP, LineCapEnumMax
	}

	public enum LineJoinEnum {
		MITER_JOIN, MITER_REVERT_JOIN, ROUND_JOIN, BEVEL_JOIN, LineJoinEnum_MAX
	}

	private MapnikColour mColour;
	private float mWidth;
	private float mOpacity; // 0.0 - 1.0
	private LineCapEnum mLineCap;
	private LineJoinEnum mLineJoin;
	private Vector<float[]> mDash;

	public MapnikStroke() {
		// TODO: Defined in src/stroke.cpp
	}

	public MapnikStroke(MapnikColour c, float width) {
		// TODO: Defined in src/stroke.cpp
	}

	public MapnikStroke(MapnikStroke s) {
		// TODO: Defined in src/stroke.cpp
	}

	public void setColour(MapnikColour c) {
		// TODO: Defined in src/stroke.cpp
	}

	public MapnikColour getColour() {
		// TODO: Defined in src/stroke.cpp
		return null;
	}

	public float getWidth() {
		// TODO: Defined in src/stroke.cpp
		return 0;
	}

	public void setWidth(float w) {
		// TODO: Defined in src/stroke.cpp
	}

	public float getOpacity() {
		// TODO: Defined in src/stroke.cpp
		return 0;
	}

	public void setOpacity(float o) {
		// TODO: Defined in src/stroke.cpp
	}

	public LineCapEnum getLineCap() {
		// TODO: Defined in src/stroke.cpp
		return null;
	}

	public void setLineCap() {
		// TODO: Defined in src/stroke.cpp
	}

	public LineJoinEnum getLineJoin() {
		// TODO: Defined in src/stroke.cpp
		return null;
	}

	public void addDash(float dash, float gap) {
		// TODO: Defined in src/stroke.cpp
	}

	public boolean hasDash() {
		// TODO: Defined in src/stroke.cpp
		return false;
	}

	public Vector<float[]> getDashArray() {
		// TODO: Defined in src/stroke.cpp
		return null;
	}

	private void swap(MapnikStroke other) {
		// TODO: Defined in src/stroke.cpp
	}

	/*
	 * // TODO: These were in include/mapnik/stroke.hpp - I didnt figure out
	 * what they do yet DEFINE_ENUM( line_cap_e, line_cap_enum ); DEFINE_ENUM(
	 * line_join_e, line_join_enum );
	 */
}

package org.andnav.osm.views.tiles.renderer.mapnik.projector;

import org.andnav.osm.util.MyMath;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikProjection;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikProjection.MapnikProjectionDataType;

public class ProjectionTransformation {
	
	private MapnikProjection mSource;
	private MapnikProjection mDest;
	private boolean isSourceLatLong;
	private boolean isDestLatLong;
	
	public ProjectionTransformation(MapnikProjection source, MapnikProjection dest)
	{
		mSource = source;
		mDest = dest;
	}
	
	boolean forward(double[] coords)
	{
		if (mSource.mType == MapnikProjectionDataType.LatLong)
		{
			if (mDest.mType == MapnikProjectionDataType.Gudermann)
			{
				coords[0] = MyMath.gudermannInverse(coords[0]);
				coords[1] = MyMath.gudermannInverse(coords[1]);
			}
		}
		else if (mSource.mType == MapnikProjectionDataType.Gudermann)
		{
			if (mDest.mType == MapnikProjectionDataType.LatLong)
			{
				coords[0] = MyMath.gudermann(coords[0]);
				coords[1] = MyMath.gudermann(coords[1]);
			}
		}
		return true;
	}
	
	boolean backward(double[] coords)
	{
		if (mSource.mType == MapnikProjectionDataType.LatLong)
		{
			if (mDest.mType == MapnikProjectionDataType.Gudermann)
			{
				coords[0] = MyMath.gudermann(coords[0]);
				coords[1] = MyMath.gudermann(coords[1]);
			}
		}
		else if (mSource.mType == MapnikProjectionDataType.Gudermann)
		{
			if (mDest.mType == MapnikProjectionDataType.LatLong)
			{
				coords[0] = MyMath.gudermannInverse(coords[0]);
				coords[1] = MyMath.gudermannInverse(coords[1]);
			}
		}
		return true;
	}
}

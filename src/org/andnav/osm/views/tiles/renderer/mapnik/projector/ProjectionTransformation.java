package org.andnav.osm.views.tiles.renderer.mapnik.projector;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikProjection;

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
	
	
/*
    public:
        proj_transform(projection const& source,
                       projection const& dest);

        bool forward (double& x, double& y , double& z) const;
        bool backward (double& x, double& y , double& z) const;
*/
	
}

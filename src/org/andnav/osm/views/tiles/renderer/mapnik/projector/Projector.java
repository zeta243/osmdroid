package org.andnav.osm.views.tiles.renderer.mapnik.projector;

import java.util.Vector;

// This is from the libproj project, rather than mapnik.
// original from src/projects.h (C code)

public class Projector {
	
	public enum ProjectorDatumType
	{
		PJD_UNKNOWN,
		PARAM3,
		PARAM7,
		GRIDSHIFT,
        WGS84
	}
	
	private String mDescr;
	private Vector<ProjectorArg> params;
	
	private int over;
	private int geoc;
	private boolean isLatLong;
	private boolean isGeocent;
	
    private double a;  /* major axis or radius if es==0 */
    private double a_orig; /* major axis before any +proj related adjustment */
    private double es; /* e ^ 2 */
    private double es_orig; /* es before any +proj related adjustment */
    private double e;  /* eccentricity */
    private double ra; /* 1/A */
    private double one_es; /* 1 - e^2 */
    private double rone_es; /* 1/one_es */
    private double lam0, phi0; /* central longitude, latitude */
    private double x0, y0; /* easting and northing */
    private double k0;     /* general scaling factor */
    private double to_meter, fr_meter; /* cartesian scaling */
	
    private ProjectorDatumType datum_type;
    private double datum_params[];
    private double from_greenwich;
    private double long_wrap_center;

}

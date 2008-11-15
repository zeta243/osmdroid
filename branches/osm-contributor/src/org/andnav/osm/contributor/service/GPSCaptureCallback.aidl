

// Include your fully-qualified package statement.
package org.andnav.osm.contributor.service;

// See the list above for which classes need
// import statements (hint--most of them)

// import org.andnav.osm.contributor.service.GPSCaptureService;

oneway interface GPSCaptureCallback
{
	void updateLocation(in Location l);
}
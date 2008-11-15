

// Include your fully-qualified package statement.
package org.andnav.osm.contributor.service;

// See the list above for which classes need
// import statements (hint--most of them)

import org.andnav.osm.contributor.service.GPSCaptureCallback;

interface GPSCaptureInterface
{
    boolean startCapture(String captureName);
    boolean stopCapture(String captureName);
    boolean newSegment(String captureName);
    
    int getNumSatellites();
    boolean isRunning();
    
    boolean registerCallback(in GPSCaptureCallback c);
    boolean deRegisterCallback(in GPSCaptureCallback c);
}


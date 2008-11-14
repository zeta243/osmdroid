package org.andnav.osm.renderer;

import java.util.HashMap;

public class OSMNode implements OSMDatabaseConstants {
	public int id;
	public double latitude;
	public double longitude;
	public HashMap<String, String> tags;
	

	public OSMNode(int aId, double aLatitude, double aLongitude)
	{
		this.id = aId;
		this.latitude = aLatitude;
		this.longitude = aLongitude;
	}
}

package org.andnav.osm.contributor.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;

import org.andnav.osm.contributor.util.constants.OSMContributorConstants;

import android.location.Location;

public class GPXFileWriter implements OSMContributorConstants{

	private FileOutputStream mOutputStream;
	private String mCaptureName;
	private String mFileName;

	public GPXFileWriter(final String baseDir, final String name) throws IOException, IllegalArgumentException {
		mCaptureName = name;

		final Calendar c = Calendar.getInstance();
		if (mCaptureName != null)
			mFileName = mCaptureName + ".gpx.xml";
		else {
			throw new IllegalArgumentException();
		}

		final File path = new File(baseDir);
		path.mkdirs();

		final File f = new File(baseDir + "/", mFileName);
		// f.mkdirs();
		this.mOutputStream = new FileOutputStream(f);

		this.mOutputStream.write(GPX_TAG.getBytes());

		this.mOutputStream.write(new String("<time>" + Util.convertTimestampToUTCString(c.getTimeInMillis()) + "</time><trk><trkseg>\n").getBytes());
		this.mOutputStream.flush();
	}

	public String getName() {
		return mCaptureName;
	}

	public void appendLocation(Location location) throws IOException {
		final Calendar c = new GregorianCalendar();
		
		final Formatter f = new Formatter(this.mOutputStream);
		
		f.format(GPX_TAG_TRACK_SEGMENT_POINT, location.getLatitude(), location.getLongitude());
		
		f.format(GPX_TAG_TRACK_SEGMENT_POINT_TIME, Util.convertTimestampToUTCString(c.getTimeInMillis()));

		if(location.hasAltitude()){
			f.format(GPX_TAG_TRACK_SEGMENT_POINT_ELE, location.getAltitude());
		}
		
		if(location.hasBearing()){
			f.format(GPX_TAG_TRACK_SEGMENT_POINT_COURSE, location.getBearing());
		}
		
		if(location.hasSpeed()){
			f.format(GPX_TAG_TRACK_SEGMENT_POINT_SPEED, location.getSpeed() * 2.2369362920544); // in mph
		}
		
		this.mOutputStream.write(GPX_TAG_TRACK_SEGMENT_POINT_CLOSE.getBytes());
		
		this.mOutputStream.flush();
	}

	public void appendNewTrack() throws IOException {
		this.mOutputStream.write(GPX_TAG_TRACK_SEGMENT_CLOSE.getBytes());
		this.mOutputStream.write(GPX_TAG_TRACK_SEGMENT.getBytes());
		this.mOutputStream.flush();
	}

	public void finalizeOutputStream() throws IOException {
		this.mOutputStream.write(GPX_TAG_TRACK_SEGMENT_CLOSE.getBytes());
		this.mOutputStream.write(GPX_TAG_TRACK_CLOSE.getBytes());
		this.mOutputStream.write(GPX_TAG_CLOSE.getBytes());
		this.mOutputStream.flush();
		this.mOutputStream.close();
	}
}

package org.andnav.osm.contributor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.content.res.Resources;
import android.location.Location;
import android.os.RemoteException;
import android.util.Log;

public class GPSFileWriter {

	private static final String TAG = "GPSFileWriter";
	private FileOutputStream mOutputStream;
	private String mCaptureName;
	private String mFileName;
	private int mNumPoints = 0;
	
	GPSFileWriter(final String baseDir, final String name) throws IOException, IllegalArgumentException
	{
		mCaptureName = name;
		
		Calendar c = Calendar.getInstance();
		if (mCaptureName != null)
			mFileName = mCaptureName + ".gpx.xml";
		else
		{
			throw new IllegalArgumentException();
		}
		
		File path = new File(baseDir);
		path.mkdirs();
		
		File f = new File(baseDir + "/", mFileName);
		// f.mkdirs();
		mOutputStream = new FileOutputStream(f);
		
		/* TODO Please format the UTC-time-strings using:
		 * org.andnav.osm.contributor.util.Util.convertTimestampToUTCString(long timestamp);
		 */
		
		/* TODO Is there an effort using "new String("Hello world")" ?
		 * Also please avoid concatenating strings using the "+"-Operator. 
		 * User StringBuilder instead. Its much more efficient!
		 */

		/* TODO Please reuse the constants from: 
		 * org.andnav.osm.contributor.util.constants.OpenStreetMapContributorConstants
		 * 
		 * For an example how to construct the Strings have a look at:
		 * org.andnav.osm.contributor.util.RecordedRouteGPXFormatter.create(...) */ 
		mOutputStream.write(
new String(
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<gpx version=\"1.0\" creator=\"Follow My Leader\"\n" +
"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
"	xmlns=\"http://www.topografix.com/GPX/1/0\"\n" +
"	xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0\n" +
"		http://www.topografix.com/GPX/1/0/gpx.xsd\">\n").getBytes());
		
		String timestamp = String.format("%04d-%02d-%02dT%02d:%02d:%02dZ",
				c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
		
		mOutputStream.write(new String(
"	<time>" + timestamp + "</time>\n" +
"	<trk>\n" +
"   	<trkseg>\n").getBytes());
		mOutputStream.flush();
	}
	
	public String getName()
	{
		return mCaptureName;
	}
	
	public void appendLocation(Location location) throws IOException
	{
		Calendar c = Calendar.getInstance();
		String timestamp = String.format("%04d-%02d-%02dT%02d:%02d:%02dZ",
				c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
		
		mOutputStream.write(new String(
"			<trkpt lat=\"" + location.getLatitude() + "\" lon=\"" + location.getLongitude() + "\">\n" +
"				<ele>" + location.getAltitude() + "</ele>\n" +
"				<time>" + timestamp + "</time>\n" +
"				<course>" + location.getBearing() + "</course>\n" +
"				<speed>" + (location.getSpeed() * 2.2369362920544) + "</speed>\n" + // Converts from meters/second to miles/hour
"			</trkpt>\n").getBytes());
		mOutputStream.flush();
		mNumPoints++;
	}
	
	public void appendNewTrack() throws IOException
	{
		mOutputStream.write(new String(
"		</trkseg>\n" +
"		<trkseg>\n").getBytes());
		mOutputStream.flush();
	}
	
	public void finaliseOutputStream() throws IOException
	{
		mOutputStream.write(new String(
"		</trkseg>\n" +
"	</trk>\n" +
"</gpx>\n").getBytes());
		mOutputStream.flush();
		mOutputStream.close();
	}
}

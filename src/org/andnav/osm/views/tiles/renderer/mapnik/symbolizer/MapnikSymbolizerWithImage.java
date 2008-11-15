package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikImageData;

// Original from include/mapnik/symbolizer.hpp
//               src/symbolizer.cpp

public class MapnikSymbolizerWithImage implements MapnikSymbolizerWithImageInterface {

	protected MapnikImageData mImage;
	protected String mImageFilename;
	
	public MapnikSymbolizerWithImage(MapnikImageData image)
	{
		mImage = image;
	}
	
	public MapnikSymbolizerWithImage(String filename, String type, int width, int height) throws IOException, FileNotFoundException
	{
		mImageFilename = filename;
		
		FileInputStream s = new FileInputStream(new File(filename));
		
		byte[] imageData = new byte[width * height * 4];
		s.read(imageData, 0, width * height * 4);
		
		mImage = new MapnikImageData(width, height);
		mImage.setBytes(imageData);
	}
	
	public MapnikSymbolizerWithImage(MapnikSymbolizerWithImage symbolizer)
	{
		mImage = symbolizer.mImage;
		mImageFilename = symbolizer.mImageFilename;
	}

	public MapnikImageData getImage()
	{
		return mImage;
	}
	
	public String getFilename()
	{
		return mImageFilename;	
	}
	
	public void setImage(MapnikImageData image)
	{
		mImage = image; 
	}
}

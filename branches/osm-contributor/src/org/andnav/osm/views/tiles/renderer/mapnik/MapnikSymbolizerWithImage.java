package org.andnav.osm.views.tiles.renderer.mapnik;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

// Original from include/mapnik/symbolizer.hpp
//               src/symbolizer.cpp

public abstract class MapnikSymbolizerWithImage {

	protected MapnikImageData mImage;
	protected String mImageFilename;
	
	protected MapnikSymbolizerWithImage(MapnikImageData image)
	{
		mImage = image;
	}
	
	protected MapnikSymbolizerWithImage(String filename, String type, int width, int height) throws IOException, FileNotFoundException
	{
		mImageFilename = filename;
		
		FileInputStream s = new FileInputStream(new File(filename));
		
		byte[] imageData = new byte[width * height * 4];
		s.read(imageData, 0, width * height * 4);
		
		mImage = new MapnikImageData(width, height);
		mImage.setBytes(imageData);
	}
	
	protected MapnikSymbolizerWithImage(MapnikSymbolizerWithImage symbolizer)
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

package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikImageData;
import org.andnav.osm.views.tiles.renderer.mapnik.feature.MapnikFeature;
import org.andnav.osm.views.tiles.renderer.mapnik.geometry.MapnikCoordTransformer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;

// Original from include/mapnik/symbolizer.hpp
//               src/symbolizer.cpp

public class MapnikSymbolizerWithImage extends MapnikSymbolizer implements MapnikSymbolizerWithImageInterface {

	protected Bitmap mImage;
	// protected MapnikImageData mImage;
	protected String mImageFilename;
	
	public MapnikSymbolizerWithImage(int width, int height)
	{
		mImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mImageFilename = null;
		int pixels[] = {Color.BLACK};
		mImage.setPixels(pixels, 0, 1, 0, 0, width, height);
	}
	
	public MapnikSymbolizerWithImage(Bitmap image)
	{
		mImage = image;
		mImageFilename = null;
	}
	
	public MapnikSymbolizerWithImage(String filename) throws IOException, FileNotFoundException
	{
		mImageFilename = filename;
		
		mImage = BitmapFactory.decodeFile(filename);
	}
	
	public MapnikSymbolizerWithImage(MapnikSymbolizerWithImage symbolizer)
	{
		mImage = symbolizer.mImage;
		mImageFilename = symbolizer.mImageFilename;
	}

	public Bitmap getImage()
	{
		return mImage;
	}
	
	public String getFilename()
	{
		return mImageFilename;	
	}
	
	public void setImage(Bitmap image)
	{
		mImage = image; 
	}

	@Override
	public void draw(Canvas canvas, MapnikCoordTransformer transformer,
			MapnikFeature feature) throws Exception {
		// Do nothing. Drawing is performed by the subclass.
	}
}

package org.andnav.osm.views.tiles.renderer.mapnik;

import java.security.InvalidParameterException;

// Original from include/mapnik/image_data.hpp
// Template class, for either 8-bit or 32-bit images. We dont need 8-bit, so drop the template
// and use int[] instead.

// I suspect that this class can be replaced by a bitmap/canvas, however I want to get all
// the code ported before making any implementation changes.

public class MapnikImageData {

	private final int mWidth;
	private final int mHeight;
	private       int[] mData;

	public MapnikImageData(int width, int height)
	{
		mWidth  = width;
		mHeight = height;
		
		if (width == 0 || height == 0)
		{
		    throw new InvalidParameterException();	
		}
		mData = new int[width * height];
		for (int i = 0; i < mData.length; i++)
		{
			mData[i] = 0;
		}
	}
	
	public MapnikImageData(MapnikImageData d)
	{
		mWidth = d.mWidth;
		mHeight = d.mHeight;
		
		mData = new int[d.mWidth * d.mHeight];
		for (int i = 0; i < mData.length; i++)
		{
			mData[i] = d.mData[i];
		}
	}
	
	public int getWidth()
	{
		return mWidth;
	}
	
	public int getHeight()
	{
		return mWidth;
	}
	
	public void set(int value)
	{
		for (int i = 0; i < mData.length; i++)
		{
			mData[i] = value;
		}
	}
	
	public int[] getData()
	{
		return mData;
	}
	
	public byte[] getBytes()
	{
		byte[] data = new byte[mWidth * mHeight * 4];
		
		for (int intOffset = 0; intOffset < mData.length; intOffset++)
		{
			int byteOffset = intOffset * 4;
			data[byteOffset + 0] = (byte)(mData[intOffset] >> 24);
			data[byteOffset + 1] = (byte)(mData[intOffset] >> 16);
			data[byteOffset + 2] = (byte)(mData[intOffset] >> 8);
			data[byteOffset + 3] = (byte)(mData[intOffset] >> 0);
			
		}
		return data;		
	}
	
	public void setBytes(byte[] data)
	{
		for (int intOffset = 0; intOffset < mData.length; intOffset++)
		{
			int byteOffset = intOffset * 4;
			mData[intOffset] = (data[byteOffset + 0] << 24) +
			                   ((data[byteOffset + 1] & 0xff) << 16) +
			                   ((data[byteOffset + 2] & 0xff) << 8)  +
			                   ((data[byteOffset + 3] & 0xff) << 0);
		}
	}
	
	public int[] getRow(int row)
	{
		int[] data = new int[row * mWidth];
		
		for (int i = 0; i < mWidth; i++)
		{
			data[i] = mData[row * mWidth + i];
		}
		return data;
	}
	
	public void setRow(int row, int[] data)
	{
		for (int i = 0; i < mWidth; i++)
		{
			mData[row * mWidth + i] = data[i];
		}
	}	
}

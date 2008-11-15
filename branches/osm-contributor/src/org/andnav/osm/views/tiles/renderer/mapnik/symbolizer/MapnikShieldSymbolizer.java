package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikColour;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikImageData;

// Original from include/mapnik/shield_symbolizer.hpp

public class MapnikShieldSymbolizer extends MapnikTextSymbolizer implements MapnikSymbolizerWithImageInterface {

	MapnikSymbolizerWithImageInterface mSymbolizerImage;
	
	public MapnikShieldSymbolizer(String name, String faceName, int size,
			                      MapnikColour fill,
			                      String file,
			                      String type,
			                      int width, int height) throws FileNotFoundException, IOException
	{
		
		super(name, faceName, size, fill);
		mSymbolizerImage = new MapnikSymbolizerWithImage(file, type, width, height);
	}

	@Override
	public String getFilename() {
		return mSymbolizerImage.getFilename();
	}

	@Override
	public MapnikImageData getImage() {
		return mSymbolizerImage.getImage();
	}

	@Override
	public void setImage(MapnikImageData image) {
		mSymbolizerImage.setImage(image);
	}
}

package org.andnav.osm.views.tiles.renderer.mapnik.symbolizer;

import org.andnav.osm.views.tiles.renderer.mapnik.MapnikImageData;

public interface MapnikSymbolizerWithImageInterface {

	/* (non-Javadoc)
	 * @see org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikSymbolizerWithImageInterface#getImage()
	 */
	public abstract MapnikImageData getImage();

	/* (non-Javadoc)
	 * @see org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikSymbolizerWithImageInterface#getFilename()
	 */
	public abstract String getFilename();

	/* (non-Javadoc)
	 * @see org.andnav.osm.views.tiles.renderer.mapnik.symbolizer.MapnikSymbolizerWithImageInterface#setImage(org.andnav.osm.views.tiles.renderer.mapnik.MapnikImageData)
	 */
	public abstract void setImage(MapnikImageData image);

}
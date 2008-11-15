package org.andnav.osm.views.tiles.renderer.mapnik;

// original from include/mapnik/map.hpp

public class MapnikMap {
	
	static final int MIN_MAPSIZE = 16;
	static final int MAX_MAPSIZE = MIN_MAPSIZE << 10;

	private int mWidth;
	private int mHeight;
	private String mSrc;

	private MapnikColour mBackground;
	
	/*

    unsigned width_;
    unsigned height_;
    std::string  srs_;
    
    boost::optional<Color> background_;
    std::map<std::string,feature_type_style> styles_;
    std::vector<Layer> layers_;
    Envelope<double> currentExtent_;
    */

}

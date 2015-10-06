This page explains the internals of the OpenStreetMapView class and its companions.

# Scrolling #

Scrolling is now implemented with the Views standard scrolling mechanism: scrollTo(x,y), getScrollX(), getScrollY()

  * Values depend on the current zoom level
  * The world size in pixel is (2^zoomLevel x tileSize)
  * Scroll values are signed, the center is at Lat=0, Lon=0

# Zooming #

There are two important zoom levels:
  * Tile zoom level
  * Pixel zoom level = TileZoomLevel + log2(TileSizePx)
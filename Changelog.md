# Changelog #

This list only includes major highlights or breaking changes. Check the [changes list](https://code.google.com/p/osmdroid/source/list) for full commit logs.

## 4.2 ##

  * Added experimental HW-acceleration support. See [issue 413](https://code.google.com/p/osmdroid/issues/detail?id=413).
  * [Issue 426](https://code.google.com/p/osmdroid/issues/detail?id=426), [issue 479](https://code.google.com/p/osmdroid/issues/detail?id=479), [issue 489](https://code.google.com/p/osmdroid/issues/detail?id=489), [issue 520](https://code.google.com/p/osmdroid/issues/detail?id=520), [issue 521](https://code.google.com/p/osmdroid/issues/detail?id=521).

## 4.1 ##

  * Fix issue with OSM tile servers rejecting GETs due to missing user-agent. See [issue 515](https://code.google.com/p/osmdroid/issues/detail?id=515).
  * [Issue 417](https://code.google.com/p/osmdroid/issues/detail?id=417), [issue 477](https://code.google.com/p/osmdroid/issues/detail?id=477), [issue 489](https://code.google.com/p/osmdroid/issues/detail?id=489), [issue 491](https://code.google.com/p/osmdroid/issues/detail?id=491), [issue 498](https://code.google.com/p/osmdroid/issues/detail?id=498), [issue 500](https://code.google.com/p/osmdroid/issues/detail?id=500), [issue 507](https://code.google.com/p/osmdroid/issues/detail?id=507).

## 4.0 ##

  * Added compatibility layer for Google Maps API v2.
  * Added BitmapPool to reuse MapTiles and prevent constant Bitmap allocation during scrolling.
  * Added fix to ignore MapView 'clickable' setting that can prevent scrolling.
  * Added http client factory to MapTileDownloader.
  * Some changes to clean up the MapController. See [issue 471](https://code.google.com/p/osmdroid/issues/detail?id=471).
  * Changed zoom animations to match pinch-to-zoom animations for consistency and to allow overlays to prevent their contents from scrolling. See [issue 453](https://code.google.com/p/osmdroid/issues/detail?id=453).
  * [Issue 298](https://code.google.com/p/osmdroid/issues/detail?id=298), [issue 408](https://code.google.com/p/osmdroid/issues/detail?id=408), [issue 427](https://code.google.com/p/osmdroid/issues/detail?id=427), [issue 437](https://code.google.com/p/osmdroid/issues/detail?id=437), [issue 438](https://code.google.com/p/osmdroid/issues/detail?id=438), [issue 441](https://code.google.com/p/osmdroid/issues/detail?id=441), [issue 442](https://code.google.com/p/osmdroid/issues/detail?id=442), [issue 447](https://code.google.com/p/osmdroid/issues/detail?id=447), [issue 450](https://code.google.com/p/osmdroid/issues/detail?id=450), [issue 451](https://code.google.com/p/osmdroid/issues/detail?id=451), [issue 483](https://code.google.com/p/osmdroid/issues/detail?id=483).

## 3.0.10 ##

  * Added limited scrolling area
  * Added simple setable min/max zoomlevel on the MapView that overrides the values returned by the tile provider.
  * Pinch-to-zoom is relative to the pinch point, not the center of the screen.
  * New samples project with modern Fragments.
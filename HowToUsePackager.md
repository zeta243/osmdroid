# Introduction #

This page explains how to use the osmdroid tile packager.

**You will probably find it easier to use MobileAtlasCreator instead of this**

# Contents #

  * Why you might want to use it
  * Determining the map area
  * Run the tile packager
  * Using the output


## Why you might want to use it ##

You can use the tile packager in order to load tiles onto your device so that you will not need an internet connection when running an application that uses osmdroid.


## Determining the map area ##

The first thing you need to do is to decide the area that you want to download. This means determining the coordinates. The easiest way is to go to <a>OpenStreetMap.org</a>, view the area that you want to download, and then click on the "Export" tab. You can then click the "Manually select a different area" link and choose your area by dragging a box over it. The coordinates are displayed in the "Area to Export" box.

Below is an example of choosing the lovely town of Haarlem in The Netherlands.
This shows that the coordinates are:<br />
North: 52.4244<br />
East: 4.6746<br />
South: 52.3388<br />
West: 4.5949

![http://osmdroid.googlecode.com/files/osm_sample_export_area.png](http://osmdroid.googlecode.com/files/osm_sample_export_area.png)


## Run the tile packager ##

Download osmdroid-packager-x.xx.jar.

Here is an example command line to download the area selected in the example above:

```
set classpath=osmdroid-android-3.0.3.jar;osmdroid-packager-3.0.3.jar;slf4j-android-1.5.8.jar;sqlitejdbc-v056.jar
java org.andnav2.osm.mtp.OSMMapTilePackager -u http://tile.openstreetmap.org/%d/%d/%d.png -t Mapnik -d haarlem.zip -zmax 18 -n 52.4244 -s 52.3388 -e 4.6746 -w 4.5949
```
or
```
set classpath=osmdroid-android-3.0.4.jar;osmdroid-packager-3.0.4.jar;slf4j-android-1.5.8.jar;sqlitejdbc-v056.jar
java org.osmdroid.mtp.OSMMapTilePackager -u http://tile.openstreetmap.org/%d/%d/%d.png -t Mapnik -d haarlem.zip -zmax 18 -n 52.4244 -s 52.3388 -e 4.6746 -w 4.5949
```

Here's what the parameters mean:<br />
|`-u`|`http://tile.openstreetmap.org/%d/%d/%d.png`|This is the pattern for tiles for the Mapnik format.|
|:---|:-------------------------------------------|:---------------------------------------------------|
|`-t`|`Mapnik`                                    |Location of the temporary download location. This should be the renderer name if you intend to use the zip directly in osmdroid.|
|`-d`|`haarlem.zip`                               |Zip file to create when finished.                   |
|`-zmax`|`18`                                        |Maximum zoom level to download tiles for.           |

The -d parameter is optional, in which case it won't create a zip file.  In that case it also won't delete the temp files, since these are actually the required result.
You can also use a .gemf or .sqlite extension in which case it will create an archive of the corresponding type.


## Using the output ##

Just copy the zip to /sdcard/osmdroid on your device.
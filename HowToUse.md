# Introduction #

This page explains how to include osmdroid source in your own application.

This method is now redundant since osmdroid is available as a jar that can be included in your application.  Please follow the instructions in HowToUseJar instead.

osmdroid's OpenStreetMapView is basically a replacement for Google's MapView class.
In order to use it you should copy OpenStreetMapView.java and its dependent files into your project.


# Contents #

  * Files to copy
  * Dummy R.java
  * Manifest
  * Using it

## Files to copy ##

Copy OpenStreetMapView.java into your project, keeping the same package name.  Then just keep copying files until it all the missing dependencies are resolved.
(You should actually do the following step first).

## Dummy R.java ##

osmdroid source code uses resources in the package org.andnav.osm.  This is generated in the osmdroid application, but resources in your application will use your own package. In order to allow you to copy osmdroid code unmodified, you can create a dummy version of R.java which references your namespace.  Below is an example (replace "your.package" with your real package name):

```
package org.andnav.osm;

public final class R {

    public static final class drawable {
        public static final int person = your.package.R.drawable.person;
        public static final int direction_arrow = your.package.R.drawable.direction_arrow;
    }

    public static final class string {
        public static final int osmarender = your.package.R.string.osmarender;
        public static final int mapnik = your.package.R.string.mapnik;
        public static final int cyclemap = your.package.R.string.cyclemap;
        public static final int openareal_sat = your.package.R.string.openareal_sat;
        public static final int trails = your.package.R.string.trails;
        public static final int relief = your.package.R.string.relief;
        public static final int cloudmade_small = your.package.R.string.cloudmade_small;
        public static final int cloudmade_standard = your.package.R.string.cloudmade_standard;
    }

}
```

## Manifest ##

Add the osmdroid service to your AndroidManifest.xml:
```
        <service android:name="org.andnav.osm.services.OpenStreetMapTileProviderService"
                 android:process=":remote"
                 android:label="OpenStreetMapTileProviderService">
            <intent-filter>
                <action android:name="org.andnav.osm.services.IOpenStreetMapTileProviderService" />
              </intent-filter>
        </service>
```

## Using it ##

Now you can just use OpenStreetMapView in much the same way as <a href='http://code.google.com/android/add-ons/google-apis/reference/com/google/android/maps/MapView.html'>MapView</a>.
You can also use any overlays that you wish from the org.andnav.osm.views.overlay package. There are many samples activities included with osmdroid.
**TODO update to use Maven**
-
get jar from [Maven repository](https://oss.sonatype.org/content/groups/public/org/osmdroid/)

# Introduction #

This page explains how to use osmdroid in your own application.

osmdroid's MapView is basically a replacement for Google's <a href='http://code.google.com/android/add-ons/google-apis/maps-overview.html'>MapView</a> class.
In order to use it you should include osmdroid-android-x.xx.jar and its dependent files into your project.


# Contents #

  * Dependencies
  * Using it
  * Manifest
  * Resources


## Dependencies ##

Your project must be Android level 3 or higher (1.5 or higher).

Include osmdroid-android-x.xx.jar and slf4j-android-1.5.8.jar in your project.

If you are going to use OSMUploader then you must also include apache-mime4j-0.6.jar and httpmime-4.0.1.jar.


## Using it ##

Now you can just use MapView in much the same way as the Google <a href='http://code.google.com/android/add-ons/google-apis/maps-overview.html'>MapView</a>.
You can also use any overlays that you wish from the org.osmdroid.views.overlay package. There are many samples activities included with osmdroid.


## Manifest ##

You will need the following permissions in your manifest file:
```
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/> 
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

If you want to use the <a href='http://developers.cloudmade.com/projects/show/tiles'>Cloudmade</a> tiles you'll need to include the following meta data:
```
<meta-data android:name="CLOUDMADE_KEY" android:value="your API key" />
```


## Resources ##

All resources used by osmdroid are included in the jar.  However, if you wish you can use your own resources.  You will especially want to do this in order to localise your application.

To see an example of using resources from the application, have a look at MapActivity.java and its use of ResourceProxyImpl.java.
## Prerequisites ##

### External libraries ###

osmdroid requires the slf4j-android logging library. If you are using Maven then it will be downloaded for you. If you are using a jar then you must download the slf4j-android library jar from http://www.slf4j.org/android/ and place it in your libs folder.


### Manifest additions ###

  * You should be targeting the latest API in your project. We support compatibility back to API 7. Your manifest should have a uses-sdk tag similar to:
```
<uses-sdk android:targetSdkVersion="16" android:minSdkVersion="7" />
```
  * You should turn off hardware acceleration in the manifest. We have experimental HW acceleration support but users will still run into some issues. See [issue 413](https://code.google.com/p/osmdroid/issues/detail?id=413) for more information.
  * osmdroid requires certain permissions in the Android project manifest to perform correctly. You will need to add the following permissions in your manifest file:
```
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/> 
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
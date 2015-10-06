**TODO update to use Maven**
-
get jar from [Maven repository](https://oss.sonatype.org/content/groups/public/org/osmdroid/)


## Install the Android SDK ##
Install the Android SDK, including Eclipse & the Android Eclipse
extension, as described in the official documentation:
http://developer.android.com/sdk/index.html
Make sure to install at least the Android SDK Platform 1.5 (API 3).
Make sure to create an Android Virtual Machine by selecting the following:
  * Window -> Android SDK and AVD Manager
  * select Virtual Devices -> New...
  * enter:
    * Name: avm15
    * Target: Android 1.5 - API Level 3
    * SD Card: Size: 1024 MiB (this is important)
  * click Create AVD
After this is done, set the ANDROID\_SDK\_PLATFORM classpath variable in
Eclipse, by selecting:
  * Window -> Preferences...
  * Java -> Build Path -> Classpath Variables
  * click New...
  * enter:
    * Name: ANDROID\_SDK\_PLATFORM
    * Path: /path/to/.../android-sdk-linux\_86/platforms/android-3/ (android-3 isn't the only choice but at least that API level)
  * click OK
  * click OK
## Check out the sources ##
Check out the osmdroid sources as describe here:
http://code.google.com/p/osmdroid/source/checkout
the quick steps are:
<pre>
svn checkout http://osmdroid.googlecode.com/svn/trunk/ osmdroid-read-only<br>
</pre>
## Import ##
In Eclipse, right-click on the Package area, and select the following:
  * click on Import...
  * select General -> Existing Projects into Workspace
  * click Next
  * click Browse...
  * select the checked out projects' directories
    * osmdroid-android (import as a java project)
    * OSMMapTilePackager (import as a java project)
    * OpenStreetMapViewer (mport as an android project)
  * click OK
  * click Finish

## Compile ##
The projects are built using the eclipse "Java Builder".
You may also build using the, Eclipse generated, ant build file (build.xml).
When the project is imported eclipse makes an effort to identify various project properties, it is not perfect and frequently makes mistakes or omissions.

### Set Java compliance level ###
If your Eclipse default Java compliance level is not 1.6, set it on a
per-project basis for osmdroid-android, OSMMapTilePackager and OpenStreetMap.

**'Project properties' -> 'Java Compiler' -> 'Compiler compliance level'**

### Compile osmdroid-android ###
This is a java project which makes uses of android libraries.

The checked out project will likely have missing library dependencies.
These can be easily corrected.

**'Project properties' -> 'Java Build Path' -> 'Libraries'**

Make sure the following jar files and libraries are defined ( .classpath):
  * "lib/apache-mime4j-0.4.jar" : Jar
  * "lib/httpmime-4.0-beta1.jar" : Jar
  * "lib/slf4j-android-1.5.8.jar" : Jar
  * "JUnit 4.libraryclasspath" : Library
  * "ANDROID\_SDK\_PLATFORM/android.jar" : var
  * "Java JRE System Library" : Library


### Compile OpenStreetMapViewer ###
This is an android project.
It does not use ant to perform its build.

### Compile OSMMapTilePackager ###
This is a straight java project.

## Try out OpenStreetMapViewer in an emulator ##
First, start an emulator by executing the following from within Eclipse:
  * Window -> Android SDK and AVD Manager
  * select your virtual device
  * click Start...
  * wait for the emulator to start (takes a long time)
then, right-click the OpenStreetMap project, and select:
  * Run As -> Android Application
and see the application downloaded to the emulator & run


## Creating a builder within Eclipse ##
The following should be unnecessary most of the time.

Eclipse typically doesn't use ant builders.
To do so you will need to make some adjustments.
You may find it helpful to make sure ant builds correctly without Eclipse.

Open the project properties for the target project.
Select the 'Builders' section, you will likely see a "Java Builder".
We will be creating an 'Ant Builder', press the 'New' button.
In the ensuing dialog choose 'Ant Build'.

The 'Name' field is the name of the new builder, 'Ant Builder'.
Using the 'Browse Workspace' buttons, choose the build file (build.xml) and the project root.

Any special instructions for targets? clean, build, etc.

OK returns eclipse to the Builders section of the project properties dialog.
Ensure that 'Java Builder' is unchecked, this will probably cause Eclipse to complain, a complaint you may ignore.
Selecting 'OK' applies the changes.

Now you have an Ant based eclipse project, ant output going to the Console view.
## Photo Location Finder: JPEG to KML Matcher
This application is purpose-built to assist in uploading images to Wikimedia Commons,
where specifying the precise location of the photo's origin can be essential.

Initially, I developed this software to streamline my own workflow. Manually identifying and inputting
the location was cumbersome and time-consuming. To optimize this process, I utilized the Android
application [Geo Tracker](https://play.google.com/store/apps/details?id=com.ilyabogdanovich.geotracker&hl=en),
developed by Ilia Bogdanovich, to track my routes and subsequently export them to KML files.

The software I've developed facilitates a more efficient search through these KML files, significantly simplifying
and speeding up the task of pinpointing photo locations.

### How It Works
* **Platform:** It's a desktop application developed using JavaFX.
* **Prerequisites:** Users need to have the [Java FX SDK](https://openjfx.io/) installed, and Java 17 is also required.
* **Application Interface:**
  * The first text field accepts a JPEG file via drag and drop. Once a valid file is uploaded,
  the application extracts the original creation timestamp from its EXIF metadata.
  * The second field is for a KML file. Upon dragging and dropping a valid KML file,
  the app fetches the entire route - pinpointing locations at specific timestamps.
  * The third field displays the search result.
* **Output Information:**
  * The application offers the last recorded point before the photo was taken.
  I noticed that Geo Tracker doesn't record a new point until there's movement.
  Given that most photos are taken while stationary, this data is crucial.
  * It also provides the point closest in time to when the photo was taken (either before
  or after the shot).
  * Points where no movement was detected by Geo Tracker for over 10 seconds, termed as "stop points," are identified.
  These are sought within a time frame of 2 minutes before and after the photo was clicked.
  * Each of the mentioned points includes information about latitude and longitude, a template for the "Location" used
  in Wikimedia Commons with appropriately set parameters, and a link to Google Maps with those parameters.
* **Time Adjustment Feature:** the timestamp extracted from the JPEG might often be in an incorrect timezone.
To rectify this, there's an option to adjust the timestamp relative to UTC. For instance, when I take photos
in Poland during summer (observing Central European Summer Time), I set the adjustment to -2.00.

For the effective use of this application, it's crucial to synchronize the time of the camera
with the time on the phone. Only then can the photo's precise location be accurately determined.

### Disclaimer:
This particular application was crafted primarily for personal use. It has undergone testing only with KML
files produced by Geo Tracker. Users who opt to utilize this tool should be aware that it hasn't
been subjected to extensive commercial-grade testing. I've made it available, hoping it might be beneficial
to others. However, those who use it should proceed cautiously and be mindful of its limitations.

### Licensing:
The application's code is available under the Apache License, Version 2.0, January 2004.

### Building the project
```
./gradlew shadowJar
```
### Binary version
* If you don't want to build the application yourself, you can use the JAR file built by me, [available here](https://drive.google.com/drive/folders/1_c_1Wsqzidcj243XkCU99KZyPhaLlgRO?usp=sharing).
* You still need [Java, in version 17 at least](https://www.oracle.com/java/technologies/downloads/#java17), installed in your system.

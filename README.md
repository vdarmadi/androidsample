# My Application

These are the steps to integrate google map into your android project

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 

### Prerequisites

Get a developer key for your android app:
1. Go to: https://developers.google.com/maps/documentation/android-api/

2. Find get a key in this page: see figures below
```
[step_1]: https://raw.githubusercontent.com/vdarmadi/androidsample/master/art/Screen1.png
[step_2]: https://raw.githubusercontent.com/vdarmadi/androidsample/master/art/Screen2.png
[step_3]: https://raw.githubusercontent.com/vdarmadi/androidsample/master/art/Screen3.png
[step_4]: https://raw.githubusercontent.com/vdarmadi/androidsample/master/art/Screen4.png
[step_5]: https://raw.githubusercontent.com/vdarmadi/androidsample/master/art/Screen5.png
[step_6]: https://raw.githubusercontent.com/vdarmadi/androidsample/master/art/Screen6.png
[step_7]: https://raw.githubusercontent.com/vdarmadi/androidsample/master/art/Screen7.png
[step_8]: https://raw.githubusercontent.com/vdarmadi/androidsample/master/art/Screen8.png
```

### App configuration

Add dependencies to google map to your android project

1. To your build gradle project level
```
classpath 'com.google.gms:google-services:3.1.0'
```

2. To your build gradle app level
```
compile 'com.google.android.gms:play-services-maps:11.0.4'
compile 'com.google.android.gms:play-services-location:11.0.4'
compile 'com.google.maps.android:android-maps-utils:0.4.3'
```

3. Put plugin on the end of the build gradle app level
```
apply plugin: 'com.google.gms.google-services'
```

4. Sync the gradle, if all goes well you should be good to continue to the next step.

### Adding google map key to your android project

When syncing successfully done, add google_maps_api resource value to your project. 
This can be done by adding new resource file to your debug and release values directory and give it google_maps_api.xml name. 
When you don't have the debug and release directory on your app you can create it manually by adding debug and release directory in the same level as the main directory, and you can then add res/values directory to both debug and release directory.

Xml structure can be seen below:
```
<resources>
    <!--
    TODO: Before you run your application, you need a Google Maps API key.

    To get one, follow this link, follow the directions and press "Create" at the end:

    https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID&r=7A:38:5D:A1:D7:C4:49:4A:85:E9:2A:90:4D:EE:E5:0E:C9:F2:AA:AE%3Bcom.ssudio.julofeature

    You can also add your credentials to an existing key, using these values:

    Package name:
    7A:38:5D:A1:D7:C4:49:4A:85:E9:2A:90:4D:EE:E5:0E:C9:F2:AA:AE

    SHA-1 certificate fingerprint:
    7A:38:5D:A1:D7:C4:49:4A:85:E9:2A:90:4D:EE:E5:0E:C9:F2:AA:AE

    Alternatively, follow the directions here:
    https://developers.google.com/maps/documentation/android/start#get-key

    Once you have your key (it starts with "AIza"), replace the "google_maps_key"
    string in this file.
    -->
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">
        AIzaSyCXv8vAGAbx9t6CVZzFpxY769Pjj6X4om4
    </string>
</resources>
```

Don't forget to add your google maps key to your android manifest under application tag
 ```
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="@string/google_maps_key" />
```

#### Note
Don't forget to add your release SHA1 certificate fingerprint to your API Key. Forgetting doing so will cause your google map does not shown.

### Personal Statement
A slightly easier way is to force your android app to create all of the step on "Adding google map key to your android project", by directly adding maps activity to your project. It will then force the project to create the directory (debug and release with the res/values directory) while also adding the entry on the manifest.

## Running the tests

UI Integration test using espresso and emulator to emulate specified coordinate and test against this coordinate

### Break down into end to end tests

Couple simple test to check wheather the map displayed a marker correctly on a single point can be seen in the MapAddressActivityInstrumentTest,
1. validating mapShouldDisplayed
```
@Test
public void mapShouldDisplayed()
```

2. validating a map should contain a marker when initialized
```
@Test
public void mapShouldShowMarkerOnLoad()
```

## Authors

* **Parama Dharmika** - *Initial work*

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Google map api example code


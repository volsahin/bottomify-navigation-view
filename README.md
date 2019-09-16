# bottomify-navigation-view
A nice looking Spotify like bottom navigation view

![alt tag](https://github.com/volsahin/bottomify-navigation-view/blob/develop/assets/bottomify.gif)
![alt tag](https://github.com/volsahin/bottomify-navigation-view/blob/develop/assets/spotify_bottom.png)

## Usage

### Create Menu File

Create a menu file below resource folder. Right click to res then New > Android Resource File, type a file name and make sure you choose ResourceType as Menu. Here is a sample menu file. You can add android:checked="true" if you want that menu item active at the begin

```xml

<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/action_home"
        android:icon="@drawable/ic_home_black_24dp"
        android:checked="true"
        android:title="@string/home" />
    <item
        android:id="@+id/action_browse"
        android:icon="@drawable/ic_speaker_group_black_24dp"
        android:title="@string/browse" />
    <item
        android:id="@+id/action_search"
        android:icon="@drawable/ic_search_black_24dp"
        android:title="@string/search" />
    <item
        android:id="@+id/action_radio"
        android:icon="@drawable/ic_radio_black_24dp"
        android:title="@string/radio" />
    <item
        android:id="@+id/action_library"
        android:icon="@drawable/ic_library_music_black_24dp"
        android:title="@string/your_library" />
</menu>

```

### Bottomify to Layout

Add Bottomify in your layout xml

```xml
    <com.volcaniccoder.bottomify.BottomifyNavigationView
        android:id="@+id/bottomify_nav"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/bottomifyBackgroundColor"
        android:orientation="horizontal"
        app:menu="@menu/navigation_items"
        app:active_color="@color/bottomifyActiveColor"
        app:passive_color="@color/bottomifyPassiveColor"
        app:pressed_color="@color/bottomifyPressedColor"
        app:item_text_size="10sp"
        app:item_padding="4dp"
        app:animation_duration="300"
        app:scale_percent="5" />
}

```

Here are some comments about what xml attributes do

```kotlin

       /**
         * app:menu -> Provide a menu for bottom view items
         * app:active_color -> The color of the active and choosen menu item
         * app:passive_color -> The color of non active menu items
         * app:pressed_color -> The color when you press on menu item
         * app:item_text_size -> The size of the menu item text
         * app:item_padding -> The padding of the menu item
         * app:animation_duration="300" -> The amount of time of the click animation
         * app:scale_percent="5" -> The percent of downsizing animation. If its 50 view will downsize to half and full again
         */

```
    
### Code Side

If you want to be notified about the change of navigation item you can implement OnNavigationItemChangeListener

```kotlin

        val bottomify = findViewById<BottomifyNavigationView>(R.id.bottomify_nav)
        bottomify.setOnNavigationItemChangedListener(object : OnNavigationItemChangeListener {
            override fun onNavigationItemChanged(navigationItem: BottomifyNavigationView.NavigationItem) {
                Toast.makeText(this@MainActivity,
                        "Selected item at index ${navigationItem.position}",
                        Toast.LENGTH_SHORT).show()
            }
        })

```
If you want to set active item not by a click but programmatically

```kotlin
        bottomify.setActiveNavigationIndex(2)
```

## Influence

### Spotify

<img src="https://lh3.googleusercontent.com/UrY7BAZ-XfXGpfkeWg0zCCeo-7ras4DCoRalC_WXXWTK9q5b0Iw7B0YQMsVxZaNB7DM=s360-rw" 
height="128" width="128">

This project influenced by Spotify's good looking bottom bar
## Download

### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```groovy

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

### Step 2. Add the dependency

```groovy

  	dependencies {
		implementation 'com.github.volsahin:bottomify-navigation-view:1.0.2'
	}
```

## License

    Copyright 2018 Volkan Şahin

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.




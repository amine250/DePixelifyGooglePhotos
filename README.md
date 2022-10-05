# DePixelifyGooglePhotos
Disable device spoofing for Google Photos in some custom ROMs

## Some context
Some custom ROMs, particularly [Evolution X](https://github.com/Evolution-X/frameworks_base/blob/tiramisu/core/java/com/android/internal/util/evolution/PixelPropsUtils.java#L202), [CrDroid](https://github.com/crdroidandroid/android_frameworks_base/blob/4929f6320c34ef19e0d031c8d18a383e9b0b3005/core/java/com/android/internal/util/crdroid/PixelPropsUtils.java#L208) and [Pixel Experience](https://github.com/PixelExperience/frameworks_base/blob/1cffe54c8ab4a893f4bdafc62cf738607fe00075/core/java/com/android/internal/util/custom/PixelPropsUtils.java#L52) feature an option to spoof your Android device in Google Photos to get unlimited photos storage.

If the user turns this option ON, the device is spoofed to Pixel XL, which gets unlimited `Original quality` and `Storage saver` storage.

If the user turns this option OFF, the device is wrongly spoofed to Pixel 5, which only gets unlimited `Storage saver` storage.

Not to mention the risks of getting your Google account banned when using this, the user choice here is not respected.

## How to use?
1. Install Magisk, [LSPosed](https://github.com/LSPosed/LSPosed) Or [EdXposed](https://github.com/ElderDrivers/EdXposed).
2. Install the apk of this app (available from Releases page.)
3. Open LSPosed / EdXposed app and enable the module. For LSPosed, Google Photos will be automatically selected.
4. Force close Google Photos app. (If needed, you might need to clear data of Google Photos app).

## How to uninstall
1. Go to Settings -> Applications
2. Find DePixelifyGooglePhotos
3. Uninstall and reboot eventually

## How does it work?

This module aims to hook to the `setProps` method of the `PixelPropsUtils` class to revert back the changes it makes to the Google Photos app.

## Important

* For the moment, this module only works with Evolution X custom ROM.
* This module has been tested on LSPosed.
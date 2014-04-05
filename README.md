# Brief Muter for Android

Sometimes there is a need to mute the phone for a short period of time. For
example, if there is a meeting, you may want to have the phone silent during
the meeting. Furthermore, you usually want to unmute the phone after the
meeting, but forgetting that is easy.

This app automates the unmuting allowing the user to specify the length of
the mute period. After the period, the phone is automatically unmuted.

## How to compile and run

Make sure you have [lein droid](https://github.com/clojure-android/lein-droid)
installed.

Also, make sure you have an Android emulator running. Then you can just enter
the following command to build the project and install it to the emulator:

```bash
lein droid doall
```

For more instructions, see lein droid
[tutorial](https://github.com/clojure-android/lein-droid/wiki/Tutorial).

## Creating APK

A debug APK that can be installed on a phone is created as follows:

```bash
lein droid apk
```

And a release APK is created the following way:

```bash
lein droid release
```

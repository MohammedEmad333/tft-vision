# TFT Vision

An open-source Android companion app for Teamfight Tactics.

**Status: v0.1 prototype, not a verified build.**

## Features

- A movable, collapsible floating **static** guide for use alongside TFT Mobile.
- Basic strategy templates to review before a match.
- A post-game screenshot picker and preview.

This starter **does not** read the game screen, recognize champions, identify current game state, provide real-time recommendations, or use Riot's API.

## Build

1. Install Android Studio, JDK 17, and Android SDK API 35.
2. Open the repository as an Android Studio project.
3. Use Gradle 8.11.1 (Android Studio can generate a wrapper with `gradle wrapper --gradle-version 8.11.1`).
4. Sync, then run the `app` configuration on an Android 8.0+ device.
5. Allow "Display over other apps", start the guide, and launch TFT Mobile.

A GitHub Actions workflow attempts a debug build and exposes the APK as an artifact. The build has not yet been verified to succeed.

## Usage and compliance

This app is unofficial and is not endorsed by Riot Games. Static learning guides do not inspect or react to live gameplay. Avoid adding live game-state recognition that dictates in-match decisions without checking Riot's current rules and obtaining any necessary permission.

- [Riot TFT documentation](https://developer.riotgames.com/docs/tft)
- [Android overlay docs](https://developer.android.com/reference/android/view/WindowManager.LayoutParams#TYPE_APPLICATION_OVERLAY)

## Roadmap

- [ ] Verify successful Android build and on-device overlay behavior
- [ ] Improve accessibility and layout scaling
- [ ] Add offline **post-game** screenshot analysis with user confirmation
- [ ] Replace placeholder guides with maintained static reference content

Licensed under MIT (see LICENSE).
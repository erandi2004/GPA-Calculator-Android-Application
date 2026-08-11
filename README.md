# GPA Calculator Android App

A clean Android GPA calculator created with Java and XML for Android Studio.

## Features

- Add or remove course rows
- Enter course name and credit value
- Select grades from A+ to F
- Select `P (Excluded)` for pass/fail courses that should not affect GPA
- Weighted GPA calculation on a 4.0 scale
- Input validation, reset button, and rotation-state support
- Works offline and uses no third-party runtime libraries

## Grade scale

| Grade | Point |
|---|---:|
| A+ / A | 4.0 |
| A- | 3.7 |
| B+ | 3.3 |
| B | 3.0 |
| B- | 2.7 |
| C+ | 2.3 |
| C | 2.0 |
| C- | 1.7 |
| D+ | 1.3 |
| D | 1.0 |
| F | 0.0 |
| P | Excluded |

## Open and run in Android Studio

1. Extract the ZIP file.
2. Open Android Studio.
3. Select **Open** and choose the `GPA_Calculator_Android` folder.
4. Wait for the Gradle sync to finish. Android Studio may download the required SDK/Gradle files the first time.
5. Start an emulator or connect an Android phone with USB debugging enabled.
6. Press **Run**.

## Build an APK

In Android Studio select:

`Build > Build Bundle(s) / APK(s) > Build APK(s)`

The debug APK will be created at:

`app/build/outputs/apk/debug/app-debug.apk`

## Customize

- App name and text: `app/src/main/res/values/strings.xml`
- Colors: `app/src/main/res/values/colors.xml`
- Grade values: `MainActivity.java`
- Package name: `com.sadil.gpacalculator`

Developed by Sadil Nethwan.

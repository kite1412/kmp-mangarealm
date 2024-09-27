# MangaRealm
MangaDex Client application for Android built using Kotlin Multiplatform. Read, manage, personalize, and keep track of your manga list.

![Android](https://img.shields.io/badge/Android-68c06e?logo=android&style=for-the-badge&logoColor=white) ![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin%20Multiplatform-8b48fc?logo=kotlin&style=for-the-badge&logoColor=c116e2)<br/>

## Installation
### Requirements
- Android Studio
- Android Debug Bridge (ADB)
- Android API level 24 / Android 7 (Nougat) or higher

### Steps
1. In Android Studio, navigate to **Build > Generate Signed APP Bundle / APK**
2. Select **APK** to build signed APK
3. Choose an existing `.jks` file or create a new one to sign the APK
4. Select **release** as Build Variants then create, wait for the APK file generation to complete
5. In project's root directory, run  following command:

   ```bash
   adb install composeApp/release/composeApp-release.apk

## Application Requirements
- MangaDex personal API client, refer to https://api.mangadex.org/docs/02-authentication/personal-clients/

## License
This project is personal and not for distribution.

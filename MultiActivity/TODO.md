# TODO List for MultiActivity Project Implementation

- [x] Update `app/build.gradle.kts`: Change `minSdk = 24` to `minSdk = 26`
- [x] Create `app/src/main/java/com/example/multiactivity/SecondActivity.kt`: Implement ComponentActivity with SecondScreen composable (Text "Anda di Layar Kedua" and Button "KEMBALI" calling finish())
- [x] Update `app/src/main/AndroidManifest.xml`: Add declaration for SecondActivity
- [x] Edit `app/src/main/java/com/example/multiactivity/MainActivity.kt`: Replace Greeting with FirstScreen composable (Text "Ini Layar Pertama" and Button "KE LAYAR KEDUA" starting Intent to SecondActivity)
- [x] Build the project using Gradle
- [ ] Run the app on emulator/device to verify navigation

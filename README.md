# My Health BC

#### Health Gateway is used to retrieve and store your proof of vaccination, access your health records, view COVID-19 test results, access official resources to navigate your way around Covid-19 and stay up to date with important alerts and official announcements

## Table of Contents

- [Project Resources](#project-resources)
- [Architecture](#architecture)
    - [Folder Structure](#folder-structure)
    - [Libraries](#libraries)
- [Configuration](#configuration)
    - [Build Variants](#build-variants)
    - [Environment Variables](#environment-variables)
    - [APIs](#apis)
- [Deployment](#deployment)
    - [Versioning](#versioning)
    - [Internal](#internal)
    - [External](#external)
- [Contributors](#contributors)

## Project Resources
---

- [Google Play](https://play.google.com/store/apps/details?id=ca.bc.gov.myhealth)

## Architecture
---

Language: __Kotlin__

Architecture: __MVVM__

    -  Architecture used for app: MVVM
        - One activity with multiple fragments is used. View models associated with fragments seperate the UI from business logic.
        - Does it differ from standard architecture? No.
        - Include UML diagram if needed. NA

Dependency Injection: __Hilt__

Concurrency: __Coroutines, Kotlin Flow__

### Folder Structure
1. App module contains Activities, Fragments, ViewModels, Adapters, CustomViews, and Helper classes
2. Data module contains classes to access local storage, remote data source and their supporting classes
3. Repository module contains repositories containing business logic and background workers.
4. Common module contains data transfer object classes which help in parsing data between data source to UI

### Libraries

- [Material design](https://material.io/develop/android/docs/getting-started): Material Components for Android
- [Jetpack navigation](https://developer.android.com/guide/navigation): For screen navigation
- [Lifecycle-aware components](https://developer.android.com/jetpack/androidx/releases/lifecycle): For usage of Lifecycle-aware components
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android): For dependency injection
- [Biometric](https://developer.android.com/jetpack/androidx/releases/biometric): For biometric authentication
- [Google barcode scanner](https://developers.google.com/ml-kit/vision/barcode-scanning/android): For scanning QR
- [CameraX](https://developer.android.com/jetpack/androidx/releases/camera): For camera funtionalities
- [Custom Chrome Tabs](https://developer.android.com/jetpack/androidx/releases/browser): To support custom chrome tabs
- [Queue.it](https://queue-it.com/developers/rest-api/): For connecting helath gateway endpoints
- [Room](https://developer.android.com/jetpack/androidx/releases/room): For local storage
- [Retrofit](https://square.github.io/retrofit/): For network calls
- [GSON](https://github.com/google/gson): For serialization/deserialization, and converting Java Objects into JSON, and back
- [Crypto](https://developer.android.com/jetpack/androidx/releases/security): For storing data in an encrypted format
- [SQL Cipher](https://www.zetetic.net/sqlcipher/sqlcipher-for-android/): For encrypting Sqlite DB data
- [SHC decoder](https://maven.pkg.github.com/FreshworksStudio/BCVAX-Android): For decoding SHC data to check vaccination status
- [Snowplow](https://docs.snowplowanalytics.com/docs/collecting-data/collecting-from-own-applications/mobile-trackers/previous-versions/android-tracker/): For analytics
- [App Auth Android](https://github.com/openid/AppAuth-Android): For performing single sign on during BCSC login
- [Workmanager](https://developer.android.com/topic/libraries/architecture/workmanager/basics): For scheduling background tasks

## Configuration
---
After cloning the project, open the **local.properties** file under the project root folder, add the following information and Sync Project with Gradle Files:
> USER = your_user_name
> TOKEN = your_github_personal_access_token

### Generating Personal Access Token
1. Log in to your Github account
2. Go to **Settings/Developer settings**
3. On **Personal access tokens**, click **Generate new token**
4. Add a note - *i.e.*: **my-health-bc-android**
5. Set the expiration
6. Check the following fields:
  - [x] **workflow**
  - [x] **write:packages**
  - [x] **admin:org**
  - [x] **admin:public_key**
  - [x] **gist**
  - [x] **write:discussion**
  - [x] **project**

8. Click **Generate token**

>Make sure to copy your personal access token now. You won’t be able to see it again.

### Build Variants

Build variants used in the app

- demoDebug
- demoRelease
- prodDebug
- prodRelease
- stageDebug
- stageRelease

### Environment Variables

- Not applicable

### Prerequisite

- Before running the project on IDE, update local.properties file with valid USER and TOKEN values.
### APIs

- https://dev.healthgateway.gov.bc.ca/api/medicationservice/swagger/index.html
- https://dev.healthgateway.gov.bc.ca/api/immunizationservice/swagger/index.html
- https://dev.healthgateway.gov.bc.ca/api/patientservice/swagger/index.html
- https://dev.healthgateway.gov.bc.ca/api/laboratoryservice/swagger/index.html
- https://dev.healthgateway.gov.bc.ca/swagger/index.html

## Deployment
---

### Versioning

In version name 1.2.3, 1 stands for major change and 2 stands for backend change, 3 stands for minor change or patch fix.

### Internal

How is the app deployed and where?

Builds generated automatically, distributed through Firebase/Slack channel. CI/CD is implemented using Github actions

### External

How is the app deployed and where?

Builds generated manually, distributed through Google Play.

## Contributors
---

List past and present contributors. Will S, Pinakin Kansara, Amit Metri, Rashmi Bambhania, Bruno Savoini
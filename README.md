# My Health BC
#### [![branch: develop](https://img.shields.io/badge/Lifecycle-Maturing-007EC6)](https://github.com/bcgov/myhealthBC-android/tree/develop)
<a href='https://play.google.com/store/apps/details?id=ca.bc.gov.myhealth&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'>
<img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width="150" height="50"/>
</a>

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
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
- [Android Studio](https://developer.android.com/studio)
---

## Architecture

<img src="./documents/HG-Architecture.png" width="750" />

This application is using Google recommended best practices and architecture. This application mainly contain 3 layers

UI Layer : Displays application data on the screen

Domain Layer : Simplify and reuse the interactions between the UI and data layers

Data Layer : Contains the business logic of your app and exposes application data.



### MVVM

<img src="./documents/mvvm.png" width="750" />

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
- [CameraX](https://developer.android.com/jetpack/androidx/releases/camera): For camera functionalities
- [Custom Chrome Tabs](https://developer.android.com/jetpack/androidx/releases/browser): To support custom chrome tabs
- [Room](https://developer.android.com/jetpack/androidx/releases/room): For local storage
- [Retrofit](https://square.github.io/retrofit/): For network calls
- [GSON](https://github.com/google/gson): For serialization/deserialization, and converting Java Objects into JSON, and back
- [Crypto](https://developer.android.com/jetpack/androidx/releases/security): For storing data in an encrypted format
- [SQL Cipher](https://www.zetetic.net/sqlcipher/sqlcipher-for-android/): For encrypting Sqlite DB data
- [SHC decoder](https://maven.pkg.github.com/FreshworksStudio/BCVAX-Android): For decoding SHC data to check vaccination status
- [Snowplow](https://docs.snowplowanalytics.com/docs/collecting-data/collecting-from-own-applications/mobile-trackers/previous-versions/android-tracker/): For analytics
- [App Auth Android](https://github.com/openid/AppAuth-Android): For performing single sign on during BCSC login
- [Workmanager](https://developer.android.com/topic/libraries/architecture/workmanager/basics): For scheduling background tasks

---

## Configuration

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

- devDebug
- devRelease
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

---

## Deployment


### Versioning

In version name 1.2.3, 1 stands for major change and 2 stands for backend change, 3 stands for minor change or patch fix.

### Internal

How is the app deployed and where?

Builds generated automatically, distributed on the Actions tab. CI/CD is implemented using Github actions

### External

How is the app deployed and where?

Builds generated manually, distributed through Google Play.

---

## Contributors

List past and present contributors. Will S, Pinakin Kansara, Amit Metri, Rashmi Bambhania, Bruno Savoini

---
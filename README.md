# Cafephile — Coffee Shop



## About

`Cafephile` is a simple, clean Android app that demonstrates a coffee shop user experience:

* browse a catalog of drinks and products
* view product details and images
* add items to a shopping cart
* select payment options and complete checkout
* mark favorites and view ratings

This repository contains the Android (Kotlin + Jetpack Compose) source code, assets and Gradle configuration.

---

## Screenshots

> Add screenshots in `/assets/screenshots/` and update paths below.

![Screenshot 1](assets/screenshots/screenshot-1.png)
![Screenshot 2](assets/screenshots/screenshot-2.png)

---

## Features

* Product listing with images and badges (new, discounted, popular)
* Product detail screen with description, ingredients, and add-to-cart
* Shopping cart with quantity update and subtotal calculation
* Checkout form (billing info) and selection of payment method
* Save cart/order data locally (SharedPreferences / local DB)
* Theme and color palette matching a coffee aesthetic
* Lightweight architecture suitable for learning Compose and basic state management

---

## Tech stack

* Kotlin
* Android (minSdk 21+ recommended)
* Jetpack Compose for UI
* ViewModel + LiveData / State for state management
* Gradle (Kotlin DSL or Groovy)
* Optional: Room / SQLite for persistence

---

## Project structure

```
├─ app/
│  ├─ src/main/java/...    # Kotlin sources (UI, models, viewmodels)
│  ├─ src/main/res        # resources (layouts, drawables, strings)
│  └─ build.gradle
├─ assets/                # images, screenshots
├─ build.gradle
└─ settings.gradle
```

---

## Getting started

### Requirements

* Android Studio (Arctic Fox or later recommended)
* JDK 11+ (match project Gradle configuration)
* Android SDK (appropriate platforms and build tools installed)

### Clone & open

```bash
# clone the repo
git clone https://github.com/Ainzar-00/Cafephile_Coffee_Shop.git
cd Cafephile_Coffee_Shop

# open in Android Studio: File → Open → select the project folder
```

### Build and run

1. In Android Studio: `File → Sync Project with Gradle Files`.
2. If required, `Build → Clean Project` then `Build → Rebuild Project`.
3. Run on an emulator or connected device: select target and click Run.

Command-line (optional):

```bash
# from project root
./gradlew assembleDebug
# install to a connected device
./gradlew installDebug
```

### Release build (APK / AAB)

Create a signed release in Android Studio: `Build → Generate Signed Bundle / APK`.

Or from CLI (assemble release, but signing config must be in place):

```bash
./gradlew assembleRelease
```

---

## Configuration & common tasks

### Change app name / package name

* Update `app/src/main/AndroidManifest.xml` `android:label` if you changed display name.
* Update `settings.gradle` (root project name):

```gradle
rootProject.name = "Cafephile_Coffee_Shop"
```

* If you renamed the project folder manually, run: `File → Invalidate Caches / Restart` in Android Studio and re-open the project.

### Fix Gradle sync issues

1. `File → Sync Project with Gradle Files`.
2. If errors persist: `Build → Clean Project` → `Build → Rebuild Project`.
3. Close Android Studio and delete `.gradle/` and `**/build/` folders, then reopen the project.

---

## Contributing

Contributions are welcome. Typical workflow:

```bash
# fork the repo, clone your fork
git checkout -b feature/your-feature
# make changes, commit
git add .
git commit -m "feat: add ..."
git push origin feature/your-feature
# open a Pull Request on GitHub
```

Please keep commits clear and small. Add screenshots for UI changes.

---

## License

This project is open-source — add a license file (`LICENSE`) (e.g., MIT) if you want to make the terms explicit.

---

## Contact

If you want help with the project (build problems, feature suggestions, or README edits) open an issue or reach out via your GitHub profile: `Ainzar-00`.

---

*Generated README — edit images, badges and sections to match your repo exactly.*

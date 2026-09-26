# StudyTrack

A native Android app (Kotlin + Jetpack Compose) for students to record their
**spending**, **classes**, and **daily activities** — built entirely offline,
with **no AI or network integration** of any kind. All data is stored locally
on-device using a Room (SQLite) database.

The UI layout is adapted from an HR dashboard design: the "Planned Absences"
weekly grid became a colored-dot activity calendar, the "Future Events" /
"Onboarding" two-column cards became "Recent Spending" / "Today's Classes",
and the AI-assistant panel was replaced with a plain **Quick Add** panel
(buttons only — no chat, no AI).

## Features

- **Record Spending** — amount, category (Food, Transport, Books & Supplies,
  Fees, Entertainment, Other), note, date
- **Add a Class** — subject, type (Lecture, Lab, Tutorial, Seminar, Exam,
  Other), teacher, room, note, attended toggle, date
- **Log Activities** — title, category (Study, Assignment, Exam Prep,
  Personal, Errand, Other), details, completed toggle, date
- **Dashboard** — weekly calendar strip, today's totals, today's classes,
  recent spending and activities
- **History** — full combined list, filterable by type, with running totals
- 100% local storage (Room/SQLite) — nothing ever leaves the device

## Get an APK without installing anything (GitHub Actions)

This repo includes a GitHub Actions workflow (`.github/workflows/build-apk.yml`)
that builds the APK for you in the cloud — you never need Android Studio.

1. Create a new **public or private GitHub repository** and push this project
   to it:
   ```bash
   cd StudyTrack
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/<your-username>/<your-repo>.git
   git push -u origin main
   ```
2. Go to the **Actions** tab of your repository on GitHub. The "Build APK"
   workflow runs automatically on every push to `main`.
3. Open the latest successful run and scroll to **Artifacts** — download
   `daily-tracker-debug-apk.zip`, which contains `app-debug.apk`.
4. Transfer the APK to your phone (email it to yourself, use Google Drive,
   or `adb install app-debug.apk`) and install it. You'll need to allow
   "Install unknown apps" for whichever app you used to open the file.

### Get a permanent download link (Releases)

Artifacts from step 3 expire after 90 days. For a stable link, push a version
tag instead:

```bash
git tag v1.0.0
git push origin v1.0.0
```

This triggers the same workflow, which will also publish a **GitHub Release**
for `v1.0.0` with `app-debug.apk` attached — a permanent link you can share
or bookmark, under the **Releases** section of your repo.

> The debug APK is signed with Android's default debug key, which is fine for
> installing on your own device but not for the Play Store. If you later want
> a Play-Store-ready release build, you'll need to add your own signing
> keystore as a repository secret and extend the workflow — ask if you'd like
> that set up.

## Building locally in Android Studio (optional)

1. Install [Android Studio](https://developer.android.com/studio) (Giraffe or
   newer).
2. **File → Open** and select the `StudyTrack` folder.
3. Let Gradle sync (it will download the Android SDK platform 34 components
   automatically if needed), then click **Run ▶** with a device or emulator
   connected.

## If you already had the old version installed

The database was reshaped (the old "sales" table became "classes"), so the
app now runs a destructive migration on first launch after this update —
your existing Spending/Activity entries are kept, but any old records will
be cleared once when the schema changes. This only happens the one time.

## Project structure

```
app/src/main/java/com/example/dailytracker/
├── data/            Room entities, DAOs, database, repositories
├── viewmodel/        ViewModels for Dashboard, Add screens, and History
├── ui/theme/         Colors, typography, Material3 theme
├── ui/components/    Reusable composables (cards, calendar strip, nav bar)
├── ui/screens/        Dashboard, Add Spending/Class/Activity, History screens
├── navigation/       Bottom-nav + NavHost wiring
├── util/             Date and currency formatting helpers
├── MainActivity.kt
└── DailyTrackerApplication.kt
```

## Requirements

- minSdk 26 (Android 8.0+)
- targetSdk / compileSdk 34
- Kotlin 1.9.24, Jetpack Compose (BOM 2024.06.00), Room 2.6.1

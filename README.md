# BatteryWidget 🔋

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blue.svg)](https://kotlinlang.org)
[![Android Minimum API](https://img.shields.io/badge/Minimum%20API-24-brightgreen.svg)](https://android-arsenal.com/api?level=24)

<p align="center">
  <img src="https://github.com/user-attachments/assets/dbe49d74-f245-4b64-90c9-c9a546982680" alt="Battery Widget Icon"/>
</p>

A modern Android home screen widget application built with **Kotlin**. Utilizing the latest **Jetpack Glance** technology, it provides users with real-time, intuitive battery status and hardware information, offering a smooth user experience and highly customizable settings.

## ✨ Features & UI

The main application is built with **MVVM Architecture** and **Jetpack Compose**, presenting data through a clean, card-based design.

| Main Dashboard | Settings |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/8ac90852-6d9b-4af8-bbcd-27b727e8543c" alt="Main App UI" width="250"/> | <img src="https://github.com/user-attachments/assets/83e9a88c-2a95-4cca-a8d5-1309ff5a1d8e" alt="Settings UI" width="250"/> |
| **Real-time Hardware Status**: Accurately fetches current battery level, voltage, temperature, and charging/discharging current. | **Highly Customizable**: Securely stores update frequency (Alarm Interval) and UI preferences using Jetpack DataStore. |

## 🚀 Live Demos

| Modern UI & Settings Navigation | Precise Background Scheduling & Real-time Updates |
| :---: | :---: |
| Smooth transitions and interactive experience built entirely with Jetpack Compose. | Integrates `AlarmManager` and `BroadcastReceiver`. The widget accurately reflects the latest status through periodic updates. |
| <br> <video src="https://github.com/user-attachments/assets/9845d1b2-1e0c-4ffc-bfcb-67314b93c66a" width="250"></video> | <br> <video src="https://github.com/user-attachments/assets/026a2ecd-0fe0-432b-b0d5-490fc4c1655b" width="250"></video> |

## 🛠 Tech Stack

* **Language**: [Kotlin](https://kotlinlang.org/)
* **Main UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Declarative UI framework)
* **Widget UI**: [Jetpack Glance](https://developer.android.com/jetpack/compose/glance) (Modern widget development with Compose-style API)
* **Architecture**: MVVM (Model-View-ViewModel)
* **Local Storage**: [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Handling asynchronous preferences with Kotlin Coroutines & Flow)
* **Background Tasks**: `AlarmManager` & `BroadcastReceiver`

## 📂 Project Structure

| Directory / File | Description |
| :--- | :--- |
| **`widget/`** | Contains core logic and declarative UI layout updates for the **Jetpack Glance** widget. |
| **`ui/theme/`** | Global theme configurations, colors, and typography for Jetpack Compose. |
| **`MainActivity.kt`** | Application entry point, handles Compose UI rendering and basic lifecycles. |
| **`MainPageViewModel.kt`** | Manages business logic and UI state for the main dashboard. |
| **`BatteryInfoCaller.kt`** | Encapsulates calls to system APIs for fetching battery and hardware status. |
| **`SharedDataStore.kt`** | Wrapper for Preferences DataStore, ensuring safe and efficient read/write of widget settings. |

## 💻 Getting Started

1. Ensure you have the latest version of Android Studio installed.
2. Clone the repository: 
   ```bash
   git clone [https://github.com/steven96034/BatteryWidget.git](https://github.com/steven96034/BatteryWidget.git)

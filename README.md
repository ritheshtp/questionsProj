# Questions App

A modern Android quiz application built with Jetpack Compose and Navigation 3. This project demonstrates a robust architecture for a feature-rich questionnaire experience, including timed questions, streak tracking, and a comprehensive result summary.

## Features

- **Timed Quiz**: Each question has a 10-second timer to add excitement and challenge.
- **Streak Tracking**: Tracks consecutive correct answers and maintains the user's longest streak.
- **Dynamic Content**: Questions are loaded dynamically from a JSON data source.
- **Review Mode**: Allows users to review their answers after completing the quiz.
- **Adaptive UI**: Built using Material 3 and adaptive navigation components for a consistent experience across different device sizes.
- **Modern Navigation**: Utilizes Jetpack Navigation 3 for seamless screen transitions.

## Tech Stack

- **Kotlin**: Primary programming language.
- **Jetpack Compose**: For building a modern, declarative UI.
- **Material 3**: For modern design and adaptive layouts.
- **Navigation 3**: For managing app navigation and backstack.
- **Hilt**: For Dependency Injection (DI).
- **Kotlin Coroutines & Flow**: For asynchronous programming and reactive UI updates.
- **Kotlinx Serialization**: For parsing JSON question data.
- **Timber**: For structured logging.

## Project Structure

The project follows a modular architecture:

- **`:app`**: Contains the main application logic, features (`load`, `question`, `result`), and UI components.
- **`:core`**: A library module that holds shared data models and core DI modules.

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio.
3. Build and run the app on an emulator or a physical device.

## Data Source

Questions are currently loaded from `app/src/main/res/raw/data.json`. You can modify this file to add or change questions.

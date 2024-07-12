# Mobile Security Final Project

This Android application demonstrates notification handling and Firebase integration, focusing on mobile security aspects.

## Purpose

The application serves as a demonstration of:
- Using BroadcastReceiver to listen for notifications.
- Managing notification permissions and settings through system intents.
- Storing notification data in Firebase Realtime Database.
- Displaying collected notifications in a RecyclerView.

## Permissions Required

To function properly, the application requires the following permissions:
- **Notification Access**: Needed to listen for incoming notifications and display them in the app.
- **Notification Listener**: Required for the app to access and read notifications from the system.

## Features

- **Notification Collection**: The app collects notifications from the system using a BroadcastReceiver.
- **Firebase Integration**: Notifications are uploaded to Firebase Realtime Database for storage and retrieval.
- **Permissions Management**: Provides buttons to navigate users to system settings for granting necessary permissions.
- **Notification Display**: Displays collected notifications in a RecyclerView within the app.
- **Clear Notifications**: Allows users to clear all stored notifications from Firebase.

## Screenshots & Video example

### Firebase realtime database:
<img src="https://github.com/user-attachments/assets/b396dd9b-be6b-47e1-bde0-3180596e2070" alt="Image 1" width="400" height="800">

### Application screen:
<img src="https://github.com/user-attachments/assets/4e41600c-81cd-4d86-80ea-29a7382d80d2" alt="Image 2" width="400" height="800">

### Application notification
<img src="https://github.com/user-attachments/assets/493721e8-a29a-4a0e-a2a1-3b32de82534a" alt="Image 3" width="400" height="800">

### Application example:
https://github.com/user-attachments/assets/68693245-3ccb-4e33-8916-133e1d6628f2

## Getting Started

To run this application on your local machine:
1. Clone this repository.
2. Open the project in Android Studio.
3. Connect an Android device or use an emulator.
4. **Don't forget to add your google-service.json to /app/ directory, (mine is not here because private key is there of course)**.
5. Build and run the application.
6. Grant the permissions to create notifications.
7. Grant the permissions to listen notifications.
8. Close the application and notice the notifications is still there until you stop the permissions.
9. View all incoming notifications in the applications screen.

## Dependencies

- Firebase Realtime Database
- Firebase UI Database


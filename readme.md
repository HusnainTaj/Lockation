![lockation wide](https://github.com/user-attachments/assets/a9673047-4a21-40df-9550-9110dcde69a5)

# Lockation

Lockation is an Android application that automatically unlocks your apps based on your Location or WiFi.

<img src="https://github.com/user-attachments/assets/c838cf47-9a8c-442d-bcc5-b027d3b79560" width="18%"></img> <img src="https://github.com/user-attachments/assets/7b056e9d-8b9b-4571-938d-adea157e8e8c" width="18%"></img> <img src="https://github.com/user-attachments/assets/e8f62309-2296-48c7-9147-15c297be9191" width="18%"></img> <img src="https://github.com/user-attachments/assets/d61ee7e4-72e7-40bf-a4b3-7d384ad06407" width="18%"></img> <img src="https://github.com/user-attachments/assets/77f8ea4b-e9c0-4fc7-a06b-6daad9d5962e" width="18%"></img> 

> [!important]
> Lockation was a hobby project made for learning Android Development. Bugs and security vulnerabilities may exist, so it should not be used to protect your apps.
>
> This repo should only be used for learning and educational purposes.

## Features

-   Monitors when an app opens to show the lock screen if necessary.
-   Automatically unlocks your apps if you are within a certain area (`Safe Location`), or connected to a known WiFi (`Safe WiFi`).
-   Conversely, automatically locks your apps if the device is not within a `Safe Location` or not connected to a `Safe WiFi`.
-   Storing data in a local database using Room.
-   Premium Subscription Management using Android Billing Client

## Design

Figma: [https://www.figma.com/design/wJ9Tqrl4eLuJB3khmK7svp/Lockation](https://www.figma.com/design/wJ9Tqrl4eLuJB3khmK7svp/Lockation)

<img src="https://github.com/user-attachments/assets/8d17fac9-f11c-48bd-8ef5-67ae101433e7" width="15%"></img> <img src="https://github.com/user-attachments/assets/0bb9769c-484f-4d34-b87a-d27aa0dacdd5" width="15%"></img> <img src="https://github.com/user-attachments/assets/e2b5e187-4509-4e53-9dc6-cce138026769" width="15%"></img> <img src="https://github.com/user-attachments/assets/80477f04-a29c-402e-9721-9f022501700a" width="15%"></img> <img src="https://github.com/user-attachments/assets/8aa3e930-1bed-4a59-9cdb-a0595d9d8852" width="15%"></img> <img src="https://github.com/user-attachments/assets/0e7037e2-095f-4a90-8731-ecb186b38d71" width="15%"></img> 

## Permissions Required

Due to the nature of this app, it requires some special/sensitive permissions in order to work:

-   `Location` - Required to get information about your current Location and connected WiFi to un/lock apps.
-   `Background Location` - Required to be able to monitor Location and WiFi changes to un/lock the apps.
-   `Notifications` - Required to inform you about the applock status and any important actions needed.
-   `Usage Stats` - Access to device's usage statistics is required to monitor when an app opens to show the lock screen if necessary.
-   `Overlay` - Required to show the lock screen, while Lockation is in the background, when you open a locked app.

## Technologies and Services used

-   Java/Kotlin
-   Jetpack Compose
-   Gradle
-   AndroidX Room Database
-   Android Billing Client

## Building the Project

1. Clone the repository.
2. Open the project in `Android Studio Iguana | 2023.2.1 Patch 1` or later.
3. Sync the Gradle files and build the project.

## License

The project is available as open source under the terms of the [MIT License](http://opensource.org/licenses/MIT).

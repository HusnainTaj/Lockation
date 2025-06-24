![lockation wide](https://github.com/user-attachments/assets/451945f7-f7ee-417b-ad76-f4af0e92555b)

# Lockation

Lockation is an Android application that automatically unlocks your apps based on your Location or WiFi.

<img src="https://github.com/user-attachments/assets/d74da65a-306b-461b-8bfd-b02b4cd37b98" width="18%"></img> <img src="https://github.com/user-attachments/assets/2d2153c9-576c-4712-bad1-a49b50f93169" width="18%"></img> <img src="https://github.com/user-attachments/assets/b5633ba4-10aa-46b1-9468-7660d8430969" width="18%"></img> <img src="https://github.com/user-attachments/assets/9732855c-c00a-4a81-b571-61c416a7cda8" width="18%"></img> <img src="https://github.com/user-attachments/assets/75a6bd00-7020-4ea6-b364-58142bda7a0f" width="18%"></img> 

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

<img src="https://github.com/user-attachments/assets/7cba2ad2-60b6-41fd-871d-cc2329f28cd5" width="15%"></img> <img src="https://github.com/user-attachments/assets/d39a3409-2011-45d6-8eca-26d4a06a911f" width="15%"></img> <img src="https://github.com/user-attachments/assets/22eccb10-fc3a-44bf-bd11-c63780e7a252" width="15%"></img> <img src="https://github.com/user-attachments/assets/339f8d17-94ee-4eae-a5ad-836174b84687" width="15%"></img> <img src="https://github.com/user-attachments/assets/04b23d5d-7e2d-47eb-b3f7-6fe2f5afa4ea" width="15%"></img> <img src="https://github.com/user-attachments/assets/c147c657-7671-4e90-bb46-a7402625feaf" width="15%"></img> 

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

# 🍅 Tamater - Food Delivery App

**Tamater** is a modern, high-performance food delivery application built with **Kotlin**. This project showcases the implementation of a seamless user experience for browsing restaurants and ordering food, utilizing the latest Android development practices.

---

## 📸 Project Preview

| Home Screen | Track Order | User Profile |
| :---: | :---: | :---: |
| ![Home](https://res.cloudinary.com/dxbpzj3mh/image/upload/v1761248414/Screenshot_home_screen_cwcopl.jpg) | ![Order](https://res.cloudinary.com/dxbpzj3mh/image/upload/v1761248414/Screenshot_home_screen_cwcopl.jpg) | ![Profile](https://res.cloudinary.com/dxbpzj3mh/image/upload/v1761248416/Screenshot_profile_screen_f9ezh5.jpg) |

---

## 🚀 Key Features

*   **Single Activity Architecture:** Implemented using the **Kotlin Navigation Framework** for efficient fragment management and smooth transitions.
*   **Real-time Interaction:** Integrated with Firebase for live data updates and user authentication.
*   **Location Services:** Uses Google Maps SDK to provide accurate location picking and delivery tracking.
*   **Modern UI:** Clean, responsive design following Material Design guidelines.

---

## 🛠 Tech Stack & Tools

*   **Language:** Kotlin
*   **Navigation:** Jetpack Navigation Component (Safe Args for type-safety)
*   **UI Toolkit:** XML / Material Components
*   **Backend:** Firebase (Auth & Database)
*   **Maps:** Google Maps SDK for Android

---

## ⚙️ Setup Instructions

To get this project running locally for testing or review, please follow these configuration steps:

### 1. Add Firebase Configuration
The project requires a `google-services.json` file to communicate with Firebase:
1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Create a new project and register the Android app using your package name.
3.  Download the `google-services.json` file.
4.  Paste the file into the **`app`** folder directory:
    > `Project_Root/app/google-services.json`

### 2. Configure Google Maps API Key
To enable the map functionality, you must add your API key:
1.  Obtain an API key from the [Google Cloud Console](https://console.cloud.google.com/).
2.  Open the `res/values/strings.xml` file in the project.
3.  Add the following string resource:
    ```xml
    <string name="google_maps_key" translatable="false">YOUR_API_KEY_HERE</string>
    ```

---

## 💡 Why This Project?

This project was built to demonstrate my proficiency in:
*   **Architectural Components:** Organizing code using MVVM and structured navigation graphs.
*   **ThirdCertainly! Since you are preparing this for an interview, I have crafted a professional, high-quality `README.md` in English. It highlights your technical choices (like the Navigation Framework) and follows industry-standard formatting.

---


## 👤 Contact
**Sachin Vishwakarma**  
*Full Stack & Android Developer*  
https://www.linkedin.com/in/sachin-vishwakarma098/ | https://coding-india.web.app/

---

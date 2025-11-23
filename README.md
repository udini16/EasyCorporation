# ♻️ Recycle Request App

A mobile application designed to streamline the connection between residents and recycling collection services. This app allows users to schedule pickups easily while enabling administrators to manage requests, track collections, and calculate recycling values.

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)

## 📱 Project Overview
The **Recycle Request App** addresses the difficulty of coordinating recycling collection. It features a dual-role system:
1.  **Users** can submit collection requests with item details and location.
2.  **Admins** can view requests, collect items physically, input weight, and update the status.

## 🚀 Key Features

### 👤 User Module
* **Secure Login:** Role-based authentication.
* **Submit Request:** Users can select item types, provide addresses, dates, and add specific notes for collection.
* **Request History:** View a list of current and past requests with status updates (Pending, Completed).
* **Cancel Request:** Ability to cancel a request before it is processed.

### 🛡️ Admin Module
* **Request Management:** View a dashboard of all submitted requests from users.
* **Process Collection:**
    * Physically collect items.
    * Input item weight.
    * **Automated Calculation:** System calculates `Total Price = Weight * Price_per_kg`.
* **Status Updates:** Mark requests as "Completed" after collection.
* **Inventory Management:** Add new recyclable items or update existing ones (price/details).

## 🛠️ Technical Architecture
The project is built using native **Android (Java)** and follows a structured architecture for maintainability:

| Package | Description |
| :--- | :--- |
| **`model`** | Contains POJO classes representing data entities (e.g., `User`, `Request`, `RecyclableItem`). |
| **`adapter`** | RecyclerView adapters for displaying lists (Request Lists, Item Lists). |
| **`remote`** | Handles network operations and API connections. |
| **`sharedpref`** | Manages local session storage (User Login State/Tokens). |
| **`activities`** | UI logic (e.g., `LoginActivity`, `FormRequest`, `AdminMainActivity`). |

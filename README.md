# Virtual Gradebook System

A comprehensive Java desktop application for school administration, designed to streamline academic management and communication. The system offers a modern interface and dedicated tools for different types of users.

## Key Features

### User Roles & Access Control
The application provides distinct interfaces and functionalities based on the logged-in user's role:
* **Students**: Can view their grades, check their class timetable, and communicate with teachers.
* **Teachers**: Can manage student grades, view their teaching schedules, and handle consultations.
* **Principals**: Have administrative access to manage teachers, classes, and student assignments.

### Academic Management
* **Dynamic Grade Tracking**: Visual representation of academic progress with color-coded grade tiles.
* **Interactive Timetable**: A custom-drawn graphical schedule for tracking daily lessons and classrooms.
* **Class Administration**: Tools for creating school classes and managing student transfers between them.

### Communication & Reporting
* **Internal Messaging**: A built-in module for sending and receiving secure messages between users.
* **Activity Logging**: Automated tracking of system events and user actions to a dedicated log file.
* **File Persistence**: Reliable data storage using a custom file-based system, ensuring data is saved between sessions.

## Technology Stack
* **Language**: Java 11+
* **GUI**: Java Swing with **FlatLaf** for a modern, clean look
* **Build Tool**: Maven
* **Data Storage**: Structured text files (.txt)

## Project Structure
The code is organized into logical packages to ensure clarity and maintainability:
* `controller`: Application flow and login logic.
* `model`: Data structures for users, school entities, and messages.
* `service`: Business logic for authentication and data access.
* `view`: All graphical components and role-specific dashboards.
* `persistence`: Data context and collection management.

## How to Run
1. **Prerequisites**: Ensure you have **JDK 11** or newer installed.
2. **Setup**: Clone the repository and import it as a **Maven project** in your IDE (e.g., Eclipse or IntelliJ).
3. **Data Files**: Verify that the `.txt` data files (users, classes, etc.) are present in the project root folder.
4. **Execution**: Run the `Main.java` class located in the `pl.dziennik.main` package.

---

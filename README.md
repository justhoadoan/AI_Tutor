# 📚 AI-Tutor: Your Personal AI-Powered Learning Assistant



**AI-Tutor** is a powerful desktop application designed to help students efficiently manage their learning materials and leverage an intelligent AI assistant powered by **RAG (Retrieval-Augmented Generation)**. The application is built with a **"Local-First"** architecture, ensuring all your data and documents are processed securely on your personal computer, offering absolute privacy.

---

## ✨ Key Features

-   **🗂️ Smart Document Management:** Upload and categorize your documents (PDF, DOCX, etc.) by semester and subject.
-   **🤖 Intelligent RAG Chatbot:** Ask questions in natural language and receive accurate answers sourced directly from your documents.
-   **✍️ Automatic Quiz Generation:** Transform static study materials into interactive multiple-choice quizzes to reinforce your knowledge.
-   **📜 Chat History:** Easily review previous questions and answers.
-   **🔒 Absolute Privacy:** All data is processed 100% on your local machine. Nothing is ever uploaded to the cloud.

---

## 🛠️ Tech Stack
| Domain       | Technology                                                                                                                                                                                                                                                                   |
| :----------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Frontend** | ![Tauri](https://img.shields.io/badge/Tauri-24C8E0?style=for-the-badge&logo=tauri&logoColor=white) ![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)                                                                           |
| **Backend**  | ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white) ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)                                                                         |
| **AI**       | ![Spring AI](https://img.shields.io/badge/Spring_AI-6DB33F?style=for-the-badge&logo=spring&logoColor=white) ![Google Gemini](https://img.shields.io/badge/Google%20Gemini-4285F4?style=for-the-badge&logo=googlegemini&logoColor=white)                                             |
| **Database** | ![Postgres](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white) ![ChromaDB](https://img.shields.io/badge/ChromaDB-6E44FF?style=for-the-badge&logoColor=white)                                                                     |
| **DevOps**   | ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)                                                                                                                                                                            |                                                                        |

---

## 🚀 Quick Start Guide

To get the project up and running, please ensure you have the required tools installed and follow the steps below.
### **Prerequisites**

To build and run this project, you need to have the following tools installed on your local machine:

| Tool         | Version / Note                                |
| :-------------- | :-------------------------------------------- |
| **Git**         | For cloning and managing the source code.     |
| **Java JDK**    | Version 21 or later.                          |
| **Maven**       | Version 3.8 or later.                         |
| **Node.js**     | Version 18 or later (which includes npm).     |
| **Docker**      | The latest version of Docker Desktop.         |

### **Background Services**

This project relies on the following services, which are automatically managed by `docker-compose`. You do not need to install them separately.

| Service      | Description                               |
| :----------- | :---------------------------------------- |
| **PostgreSQL** | Relational database for storing metadata. |
| **ChromaDB**   | Vector database for semantic search (RAG).|

### **Step 1: Clone the Repository**

Open your terminal and run the following command:
```bash
git clone https://github.com/your-username/ai-tutor.git
cd ai-tutor
```
<!-- Replace 'your-username/ai-tutor.git' with your actual repository URL -->

### **Step 2: Configure Environment Variables**

The project uses a `.env` file to manage sensitive information.

1. Download the `.env` file (it's located in a security place!!) 
2.  **Important:** Add the `.env` file to your `.gitignore` file to prevent it from being committed to GitHub.
    ```
    # .gitignore
    .env
    ```

### **Step 3: Launch Background Services (Databases)**

Make sure **Docker Desktop is running**, then open a terminal in the root directory and run the following command to start PostgreSQL and ChromaDB.

```bash
docker-compose up -d
```
This command will pull the necessary images and run the containers in detached mode.

### **Step 4: Run the Backend (Spring Boot)**

Open a **new terminal**, navigate to the `backend` directory, and use Maven to run the Spring Boot application.

```bash
cd backend
mvn spring-boot:run
```
Wait until you see the "Started LearningAppApplication" message in the logs. The backend will be running at `http://localhost:8888`.

### **Step 5: Run the Frontend (Tauri)**

Open a **third terminal**, navigate to the `frontend` directory (you will need to create this directory and the Tauri project within it), install dependencies, and launch the application.

```bash
cd frontend

# Install dependencies (only required the first time)
npm install

# Launch the desktop application
npm run tauri dev
```
A desktop window will appear, and you are ready to use the application!

---

## 🏛️ System Architecture

This project uses a **Monorepo** architecture with clearly separated components:

-   **`/backend`**: Contains the Spring Boot application, which handles all heavy-lifting logic, including RAG, document parsing, and API provision.
-   **`/frontend`**: Contains the Tauri application (React/Vue/etc.), responsible for the user interface and experience.
-   **`docker-compose.yaml`**: Manages the background services like PostgreSQL and ChromaDB.



---



---

## 📄 License

This project is licensed under the MIT License. See the `LICENSE` file for more details.
<!-- Note: Create a LICENSE file in your repository. -->

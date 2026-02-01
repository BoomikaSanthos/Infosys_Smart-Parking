# 🚗 SmartPark Pro – Intelligent Parking Management System

SmartPark Pro is a high-performance, full-stack parking management solution. It features a robust **Java Spring Boot** backend and an interactive **React** frontend, designed to provide real-time parking availability, seamless booking experiences, and powerful administrative analytics.

---

## 🏗️ System Architecture

The project is divided into two primary modules:
1.  **Backend-Java**: A Spring Boot REST API handling business logic, security, and data persistence.
2.  **frontend**: A modern React application providing the user interface.

---

## 🌟 Key Features

### 👤 User Features
*   **Real-time Availability:** View parking slots on interactive maps (Google Maps/Leaflet) and grid views.
*   **Seamless Booking:** Select slots and book for specific time ranges with automatic validation.
*   **Secure Authentication:** JWT-based login and registration system.
*   **Email Confirmations:** Automated emails sent upon successful booking.

### 🛠️ Admin Features
*   **Management Dashboard:** Add, update, or remove parking slots and manage affiliated companies.
*   **Data Analytics:** Visualize parking trends, revenue, and usage statistics via graphical charts.
*   **User Oversight:** Monitor active bookings and user activities.

---

## 💻 Tech Stack

### Backend (Java)
- **Framework:** Spring Boot 3.x
- **Security:** Spring Security & JWT (JSON Web Tokens)
- **Database:** MongoDB Atlas
- **Mail:** Spring Boot Starter Mail (SMTP)
- **Build Tool:** Maven

### Frontend (React)
- **Framework:** React 19
- **Routing:** React Router 7
- **Visualization:** Chart.js, Recharts, & Leaflet
- **Styling:** CSS3 & Lucide React Icons
- **HTTP Client:** Axios

---

## 🚀 Getting Started

### Prerequisites
*   **JDK 17** or higher
*   **Node.js 18** or higher
*   **Maven**
*   **MongoDB Atlas** account (or local instance)

---

### 🔧 Installation & Setup

#### 1. Backend (Java Spring Boot)
1.  Navigate to the backend folder:
    ```bash
    cd Backend-Java
    ```
2.  Configure `src/main/resources/application.properties`:
    *   Set your MongoDB URI (`spring.data.mongodb.uri`).
    *   Set your JWT Secret (`jwt.secret`).
    *   Configure your Email/SMTP credentials.
3.  Build and run:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
    *API will be available at: `http://localhost:5000`*

#### 2. Frontend (React)
1.  Navigate to the frontend folder:
    ```bash
    cd frontend
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Set up environment variables (create a `.env` file):
    ```env
    REACT_APP_GOOGLE_MAPS_API_KEY=your_key_here
    ```
4.  Run the application:
    ```bash
    npm start
    ```
    *Web app will be available at: `http://localhost:3000`*

---

## 📂 Project Structure

```text
Smart_Park/
├── Backend-Java/          # Spring Boot REST API
│   ├── src/main/java/     # Application logic (Controllers, Services, Models)
│   ├── src/main/resources/# Configuration (application.properties)
│   └── pom.xml            # Maven configuration
├── frontend/              # React Web Application
│   ├── src/               # UI components, pages, and styles
│   ├── public/            # Static assets
│   └── package.json       # Node.js dependencies
└── README.md              # Project documentation
```

---

## 📄 License
This project is licensed under the **ISC License**.

---
*Developed with ❤️ by the SmartPark Team.*

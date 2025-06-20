## QEEMLA SKIN DAN BODY CARE
A desktop-based inventory and activity log management application built using Java Swing, developed for Qeemla, a local skin & body care business from Gresik.
This application is designed to help staff efficiently manage products, monitor expiration dates, log activities, and ensure smooth daily operations through an intuitive and clean GUI.

## ✨ Features
- 🔐 Login system for secure access
- 📦 Product management with details such as:
     Product code, name, category, price, stock, production & expiration date
     Product image support
- 📷 Webcam integration for product photo input
- 🗃 Barcode scanning feature for product code automation
- 📅 Expired product detection and reminder
- 📜 Activity log (audit log) to monitor all actions (add, edit, delete)
- 📊 Exportable reports 
- 🚪 Logout functionality to maintain user security

## 📦 Built With
1. Java Swing – Desktop GUI Framework
2. MySQL – Database Backend
3. MySQL Connector/J – JDBC Driver
4. OpenCV – Webcam Capture
5. ZXing – Barcode Scanner Integration
6. JCalendar – Date Picker Component
7. Apache POI – Export to Excel Feature

## How To Run
1. Clone or Download the Project
git clone https://github.com/clarissaclea/final-project-pbo-f-kel1
2. Open the project in your preferred Java IDE (e.g., IntelliJ IDEA, NetBeans)
3. Add JAR Dependencies classpath / libraries project:
- lib/mysql-connector-java-x.x.x.jar
- lib/opencv-x.x.jar
- lib/zxing-core-x.x.x.jar
- lib/jcalendar-x.x.jar
- lib/poi-x.x.x.jar dan poi-ooxml-x.x.x.jar
4. Setup MySQL Database
5. Edit Connection Configuration
String url = "jdbc:mysql://localhost:3306/qeemla_db";
String username = "root";
String password = "";
6. Run the App


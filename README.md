# **Reklama8**

## **Overview**
**Reklama8 - The Smart Marketplace Ad Aggregator** is a web application that collects listings from multiple online platforms, specifically **Reklama5** and **Pazar3**, and consolidates them into a single user-friendly interface. Built with **Spring Boot** and **Angular**, this application allows users to log in, specify their desired ads, and get notified via email when matching listings become available.

## **Features**
- 🚀 **Unified Search**: Browse and filter listings from multiple sources in one place.
- 🔒 **User Authentication**: Secure login system to save user preferences.
- 🔍 **Saved Searches**: Users can store search queries for future use.
- 📩 **Personalized Notifications**: Get email alerts when new ads matching your criteria appear.
- 📊 **Efficient Marketplace Browsing**: Simplifies the process of finding and tracking new listings.

## **Tech Stack**
- **Backend**: Spring Boot (REST API, data processing)
- **Frontend**: Angular (user interface, state management)
- **Database**: H2 (for development/testing), expandable to MySQL/PostgreSQL
- **Scraping & Automation**: Python script running at scheduled intervals to fetch new listings

## **Installation**
### **Prerequisites**
Ensure you have the following installed:
- Java 21+
- Node.js & npm
- Angular CLI
- Python 3+

### **Backend Setup**
1. Clone the repository:
   ```sh
   git clone https://github.com/your-repository/Reklama8.git
   cd Reklama8
   ```
2. Navigate to the backend folder and build the Spring Boot application:
   ```sh
   cd backend
   ./mvnw spring-boot:run
   ```

### **Frontend Setup**
1. Navigate to the frontend folder:
   ```sh
   cd Reklama8/UI
   ```
2. Install dependencies:
   ```sh
   npm install
   ```
3. Start the development server:
   ```sh
   ng serve
   ```

### **Python Scraper**
1. Navigate to the scraper directory:
   ```sh
   cd crawlers
   ```
2. Install required packages:
   ```sh
   pip install requests
   pip install beautifulsoup4
   ```
3. Run the scraper script:
   ```sh
   python listings.py
   ```

## **Usage**
- 📝 Register and log in to save search preferences.
- 🗂 Browse aggregated listings from multiple sources.
- 📧 Enable email notifications to get updates on new listings that match your criteria.


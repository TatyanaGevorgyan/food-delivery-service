## About

This project is a console application that simulates a food delivery service. A customer logs in with a username and password, sees the list of available foods, and builds a shopping cart by choosing a food and the number of pieces. If the same food is chosen again, its amount is updated, and choosing zero removes it from the cart. Before every change the application checks that the total price of the cart does not exceed the customer's balance. When the customer confirms, the cart becomes an order, the price is subtracted from the balance, and the order is saved to a CSV file.

The main goal of the project is clean design rather than the user interface. The code is divided into layers (domain model, business logic, data access and view), and the layers know about each other only through interfaces. This makes it possible to replace the file storage or the console interface without touching the business logic.

Data is read from CSV files at startup. Foods and customers are loaded from foods.csv and customers.csv, and previously saved orders are loaded from orders.csv, so new order IDs continue from the last existing one.

## Project structure

food-delivery-service
├── pom.xml
├── README.md
├── .gitignore
├── src
│   └── main
│       └── java
│           └── com
│               └── epam
│                   └── training
│                       └── food
│                           ├── data
│                           │   ├── CustomerReader.java
│                           │   ├── DataStore.java
│                           │   ├── FileDataStore.java
│                           │   ├── FoodReader.java
│                           │   ├── OrderReader.java
│                           │   ├── OrderWriter.java
│                           │   └── Reader.java
│                           ├── domain
│                           │   ├── Cart.java
│                           │   ├── Credentials.java
│                           │   ├── Customer.java
│                           │   ├── Food.java
│                           │   ├── Order.java
│                           │   └── OrderItem.java
│                           ├── service
│                           │   ├── AuthenticationException.java
│                           │   ├── DefaultFoodDeliveryService.java
│                           │   ├── FoodDeliveryService.java
│                           │   └── LowBalanceException.java
│                           ├── values
│                           │   └── FoodSelection.java
│                           └── view
│                               └── View.java
└── test
    ├── customers.csv
    └── foods.csv

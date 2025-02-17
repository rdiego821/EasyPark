# EasyPark

# 🧾 Invoice & Payment System

This project is a *microservices-based* invoicing and payment system. It allows invoice creation and payment processing. API documentation via **Swagger*.

## 🚀 Technologies Used

| Technology        | Version |
|------------------|---------|
| Java            | 17      |
| Spring Boot     | 3.4.2   |
| Spring Data JPA | 3.1.0   |
| Swagger OpenAPI | 3.0     |

---

## 📌 API Documentation with Swagger
The project includes Swagger OpenAPI for interactive API documentation.
Access it via:

Invoice Service → http://localhost:8081/swagger-ui.html
Payment Service → http://localhost:8082/swagger-ui.html

## 📦 Installation & Execution
🔧 1️⃣ Clone the Repository
```
git clone https://github.com/your-username/invoice-payment-system.git
cd invoice-payment-system
```

## ▶️ 3️⃣ Run the Microservices
Build and run each service:

```
cd common && mvn clean install
cd ../invoice-service && mvn spring-boot:run
cd ../payment-service && mvn spring-boot:run
```
## 🎯 API Examples
Here are some cURL commands to test the APIs.

📄 Invoices (Invoice Service)
✅ Get all invoices

```
curl -X GET "http://localhost:8081/api/invoices"
```

✅ Get invoice by ID

```
curl -X GET "http://localhost:8081/api/invoices/{id}"
```

✅ Create a new invoice

```
curl -X POST "http://localhost:8081/api/invoices" \
-H "Content-Type: application/json" \
-d '{
"customerName": "John Doe",
"invoiceItems": [
{ "productName": "Laptop", "price": 1200.00, "description": "Dell XPS 15" }
]
}'
```
✅ Search invoices by customer or item name

```
curl -X GET "http://localhost:8081/api/invoices/search?keyword=laptop"
```

## 💳 Payments (Payment Service)
✅ Get all payments
```
curl -X GET "http://localhost:8082/api/payments"
```

✅ Process a payment
```
curl -X POST "http://localhost:8082/api/payments" \
-H "Content-Type: application/json" \
-d '{
"invoiceId": 1,
"amount": 1200.00,
"paymentMethod": "CREDIT_CARD"
}'
```

## 📌 Testing
✅ Unit Tests with JUnit5 & Mockito
Run tests for each service:
```
mvn test
```



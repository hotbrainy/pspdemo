# PSP Demo App

This project is a simple PSP system that handles payment requests and simulates interactions with
two payment acquirers based on the BIN (Bank Identification Number) card number routing
rules.

## Requirements:

### 1. API for Payment Processing:

- Endpoint Development: Create an API endpoint to accept payment details: card number,
  expiry date, CVV, amount, currency, and merchant ID.
- Validation: Validate the card number using Luhn's algorithm. Ensure all fields are
  correctly formatted and complete.
- Transaction Status: Initialize transaction status as 'Pending' in your data storage.

### 2. Acquirer Routing:

- BIN Routing Logic: Implement a routing mechanism based on the BIN card number.
  Calculate the sum of the digits in the BIN:
- If the sum is even, route the transaction to Acquirer A.
- If the sum is odd, route the transaction to Acquirer B.

## Install

### Clone

```shell
git clone https://github.com/hotbrainy/pspdemo.git
```

### Prerequisites

- [OpenJDK 21](https://openjdk.org/install/)
  ```shell
  wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb
    
  sudo apt install ./jdk-21_linux-x64_bin.deb
    
  java --version
  ```
- [Gradle 8.8](https://gradle.org/install/)
- [Docker](https://docs.docker.com/engine/install/ubuntu/)
- [Postman](https://learning.postman.com/docs/getting-started/installation/installation-and-updates/#install-postman-on-linux)
- [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

## Run

### Development

```shell
./gradlew bootRun
```

Host URL:  http://localhost:8080

### Production

- Build

```shell
./gradlew build
```

- Run

```shell
java -jar ./build/libs/psp-demo-0.0.1-SNAPSHOT.jar
```

### Test

```shell
./gradlew test
```

## Deployment

### Build

```shell
docker build . --tag psp-demo
```

### Run

```shell
docker run -dp 8080:8080 psp-demo
```

Host URL `http://localhost:8080/`

## Data Models

### PaymentRequest

| Field      | Type   | Example   |
|------------|--------|-----------|
| cardNumber | string | 16 digits |
| expiryDate | string | 07/33     |
| cvv        | string | 553       |
| amount     | double | 1200.50   |
| currency   | string | USD       |

# Transaction

| Field      | Type   | Example                   |
|------------|--------|---------------------------|
| cardNumber | string | 16 digits                 |
| status     | string | PENDING, APPROVED, DENIED |
| merchantId | string | UUID                      |

## API

| Verb | URI                       | Data           | Desc                      |
|------|---------------------------|----------------|---------------------------|
| GET  | /api/payment-request      |                | Get all payment requests  |
| GET  | /api/payment-request/{id} |                | Get payment request by id |
| POST | /api/payment-request      | PaymentRequest | Get all payment requests  |
| GET  | /api/transactions         |                | Get transactions          |
| GET  | /api/transactions/{id}    |                | Get transaction by id     |

## Postman

You can import the postman collection file.

`pspdemo.postman_collection.json`


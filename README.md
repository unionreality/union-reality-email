# emailService

Spring Boot microservice that generates Union Reality payment receipt PDFs and sends booking confirmation emails with the PDF attached.

## API

### `POST /api/v1/receipt/send-email`

Sends the HTML booking email to the given recipients, BCCs Chaitra (`MAIL_BCC_CHAITRA`), CCs accounts (`MAIL_CC`), and attaches the receipt PDF.

```bash
# Health
curl -sS http://localhost:8080/actuator/health

# Send booking email + PDF attachment
curl -sS -X POST http://localhost:8080/api/v1/receipt/send-email \
  -H "Content-Type: application/json" \
  -d '{
  "toEmails": ["customer@example.com"],
  "receiptNo": "URC 11 013 21318",
  "date": "27/09/2026",
  "project": "DEEPASHREE ENCLAVE",
  "siteNo": "40",
  "measurement": "1200sqft",
  "facing": "NorthWest Facing",
  "clientName": "Surya Kiran H P",
  "salutation": "Mr.",
  "mobile": "+91 9876543210",
  "customerEmail": "customer@example.com",
  "items": [{ "description": "Booking amount", "amount": 100000 }]
}'

# PDF only (save to file)
curl -sS -X POST http://localhost:8080/api/v1/receipt/pdf \
  -H "Content-Type: application/json" \
  -d '{
  "toEmails": ["customer@example.com"],
  "receiptNo": "URC 11 013 21318",
  "date": "27/09/2026",
  "project": "DEEPASHREE ENCLAVE",
  "siteNo": "40",
  "measurement": "1200sqft",
  "facing": "NorthWest Facing",
  "clientName": "Surya Kiran H P",
  "items": [{ "description": "Booking amount", "amount": 100000 }]
}' -o receipt.pdf
```

Request body (same for both endpoints):

```json
{
  "toEmails": ["customer@example.com"],
  "receiptNo": "URC 11 013 21318",
  "date": "27/09/2026",
  "project": "DEEPASHREE ENCLAVE",
  "siteNo": "40",
  "measurement": "1200sqft",
  "facing": "NorthWest Facing",
  "clientName": "Surya Kiran H P",
  "salutation": "Mr.",
  "mobile": "+91 9876543210",
  "customerEmail": "customer@example.com",
  "items": [{ "description": "Booking amount", "amount": 100000 }]
}
```

### `POST /api/v1/receipt/pdf`

Returns the receipt PDF only (same request body as above).

## Run locally

```bash
cp .env.example .env
# export variables from .env
mvn spring-boot:run
```

## Deploy

- **Union Reality UI (Vercel):** set `VITE_EMAIL_SERVICE_URL` to this service’s public URL.
- **emailService:** deploy the Docker image to Render, Railway, Fly.io, or any Java host (Vercel does not run Spring Boot).

```bash
docker build -t email-service .
docker run -p 8080:8080 --env-file .env email-service
```

Health check: `GET /actuator/health`

# Smart Clinic Management System - Database Design

## MySQL Database Design

The relational database manages structured operational records where referential integrity and strict relationships are required.

### Table: patients
- `id`: INT, Primary Key, AUTO_INCREMENT, Not Null
- `first_name`: VARCHAR(50), Not Null
- `last_name`: VARCHAR(50), Not Null
- `email`: VARCHAR(100), Unique, Not Null
- `phone`: VARCHAR(20), Not Null
- `date_of_birth`: DATE, Not Null
- `created_at`: DATETIME, DEFAULT CURRENT_TIMESTAMP

### Table: doctors
- `id`: INT, Primary Key, AUTO_INCREMENT, Not Null
- `first_name`: VARCHAR(50), Not Null
- `last_name`: VARCHAR(50), Not Null
- `specialization`: VARCHAR(100), Not Null
- `email`: VARCHAR(100), Unique, Not Null
- `phone`: VARCHAR(20), Not Null
- `consultation_fee`: DECIMAL(10,2), Not Null

### Table: appointments
- `id`: INT, Primary Key, AUTO_INCREMENT, Not Null
- `doctor_id`: INT, Foreign Key -> doctors(id), Not Null
- `patient_id`: INT, Foreign Key -> patients(id), Not Null
- `appointment_time`: DATETIME, Not Null
- `status`: INT, Not Null (0 = Scheduled, 1 = Completed, 2 = Cancelled)
- `reason`: VARCHAR(255)

### Table: admin
- `id`: INT, Primary Key, AUTO_INCREMENT, Not Null
- `username`: VARCHAR(50), Unique, Not Null
- `password_hash`: VARCHAR(255), Not Null
- `email`: VARCHAR(100), Unique, Not Null
- `created_at`: DATETIME, DEFAULT CURRENT_TIMESTAMP

---

## MongoDB Collection Design

MongoDB is used to store flexible prescription documents with nested medication details, metadata, and pharmacy information without requiring complex joins.

### Collection: prescriptions
```json
{
  "_id": "ObjectId('670c3a8e9f2b1d001a4e8912')",
  "patientId": 101,
  "patientName": "John Smith",
  "doctorId": 12,
  "appointmentId": 51,
  "issueDate": "2026-10-13T15:30:00Z",
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours as needed",
      "duration": "5 days"
    },
    {
      "name": "Amoxicillin",
      "dosage": "250mg",
      "frequency": "Three times daily",
      "duration": "7 days"
    }
  ],
  "doctorNotes": "Take 1 tablet every 6 hours with food. Drink plenty of fluids.",
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street",
    "phone": "415-555-0192"
  },
  "tags": ["antibiotic", "pain-relief"]
}

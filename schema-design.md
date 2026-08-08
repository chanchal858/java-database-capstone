# Schema Design

## MySQL Database Design

The MySQL database stores structured clinic data such as patients, doctors, appointments, and administrators. These tables use primary keys and foreign keys to maintain relationships between patients, doctors, and appointments.

### Table: patients

- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), Not Null
- email: VARCHAR(150), Not Null, Unique
- password: VARCHAR(255), Not Null
- phone: VARCHAR(20)
- created_at: DATETIME, Not Null

### Table: doctors

- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), Not Null
- email: VARCHAR(150), Not Null, Unique
- password: VARCHAR(255), Not Null
- specialization: VARCHAR(100), Not Null
- phone: VARCHAR(20)
- created_at: DATETIME, Not Null

### Table: appointments

- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- patient_id: INT, Foreign Key → patients(id), Not Null
- appointment_time: DATETIME, Not Null
- duration_minutes: INT, Not Null
- status: VARCHAR(20), Not Null
- created_at: DATETIME, Not Null

The doctor_id and patient_id fields establish relationships between appointments, doctors, and patients. Appointment history should be retained so that past appointments are not lost when they are completed.

A doctor should not be allowed to have overlapping appointments. Appointment availability should be checked before creating a new appointment.

### Table: admin

- id: INT, Primary Key, Auto Increment
- username: VARCHAR(100), Not Null, Unique
- password: VARCHAR(255), Not Null
- created_at: DATETIME, Not Null

The admin table stores administrator login information. The username must be unique so that two administrators cannot use the same username.

## MongoDB Collection Design

MongoDB is used for flexible data such as prescriptions and doctor notes. The prescription document can contain nested information and additional metadata without requiring changes to a fixed relational table structure.

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "patientId": 101,
  "doctorId": 25,
  "appointmentId": 51,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours",
      "duration": "5 days"
    }
  ],
  "doctorNotes": "Take the medication after meals and get adequate rest.",
  "tags": [
    "fever",
    "pain"
  ],
  "metadata": {
    "createdBy": "doctor",
    "createdAt": "2026-08-09T10:30:00",
    "refillCount": 2
  }
}

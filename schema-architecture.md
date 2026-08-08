# Smart Clinic Management System - Architecture

## Section 1: Architecture Summary

The Smart Clinic Management System is a three-tier Spring Boot application that uses both MVC and REST APIs. Thymeleaf is used to create server-rendered web pages for the Admin and Doctor dashboards, while REST APIs are used by modules such as appointments, patient dashboards, and patient records. The presentation layer communicates with the Spring Boot application layer, which contains controllers, services, and business logic.

The application uses two databases. MySQL stores structured information such as patients, doctors, appointments, and admin records using Spring Data JPA. MongoDB stores flexible document-based information such as prescriptions using Spring Data MongoDB. Controllers send requests to the service layer, and the service layer communicates with the appropriate repositories to access the required database.

## Section 2: Numbered Flow of Data and Control

1. The user accesses an Admin Dashboard, Doctor Dashboard, Appointment module, Patient Dashboard, or Patient Record module.

2. The user's request is sent to the appropriate controller. Thymeleaf controllers handle requests for web pages, while REST controllers handle API requests.

3. The controller passes the request to the Service Layer, where business rules, validations, and application workflows are processed.

4. The Service Layer communicates with the appropriate Repository Layer to retrieve or save data.

5. MySQL repositories use Spring Data JPA to access structured data such as patients, doctors, appointments, and admin records, while the MongoDB repository handles document-based prescription data.

6. The data retrieved from the databases is mapped into Java model objects. MySQL data is represented using JPA entities, while MongoDB data is represented using document models.

7. The processed data is returned to the user. In an MVC flow, the controller sends the model to a Thymeleaf template to generate an HTML page. In a REST flow, the data is converted into JSON and returned to the API client.

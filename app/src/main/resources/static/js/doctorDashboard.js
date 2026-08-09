import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

let appointmentTableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0];
let token = localStorage.getItem("token");
let patientName = null;

// Search patient
document.getElementById("searchBar").addEventListener("input", function () {
    patientName = this.value.trim();

    if (patientName === "") {
        patientName = "null";
    }

    loadAppointments();
});

// Today's appointments
document.getElementById("todayButton").addEventListener("click", function () {
    selectedDate = new Date().toISOString().split("T")[0];

    document.getElementById("datePicker").value = selectedDate;

    loadAppointments();
});

// Date picker
document.getElementById("datePicker").addEventListener("change", function () {
    selectedDate = this.value;
    loadAppointments();
});

// Load appointments
async function loadAppointments() {
    try {
        const appointments = await getAllAppointments(
            selectedDate,
            patientName,
            token
        );

        appointmentTableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {
            const row = document.createElement("tr");

            const cell = document.createElement("td");
            cell.colSpan = 5;
            cell.textContent = "No Appointments found for today";

            row.appendChild(cell);
            appointmentTableBody.appendChild(row);

            return;
        }

        appointments.forEach((appointment) => {
            const patient = appointment.patient;

            const row = createPatientRow(
                patient,
                appointment
            );

            appointmentTableBody.appendChild(row);
        });

    } catch (error) {
        console.error("Error loading appointments:", error);

        appointmentTableBody.innerHTML = "";

        const row = document.createElement("tr");

        const cell = document.createElement("td");
        cell.colSpan = 5;
        cell.textContent = "Unable to load appointments";

        row.appendChild(cell);
        appointmentTableBody.appendChild(row);
    }
}

// Initial page load
document.addEventListener("DOMContentLoaded", () => {

    const datePicker = document.getElementById("datePicker");

    if (datePicker) {
        datePicker.value = selectedDate;
    }

    loadAppointments();
});

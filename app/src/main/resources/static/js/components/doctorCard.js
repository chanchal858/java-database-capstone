import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "./modals.js";


export function createDoctorCard(doctor) {

    // Create main card
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    // Get current user's role
    const role = localStorage.getItem("userRole");


    // Create doctor information section
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");


    // Doctor name
    const name = document.createElement("h3");
    name.textContent = doctor.name;


    // Doctor specialization
    const specialization = document.createElement("p");
    specialization.textContent =
        `Specialization: ${doctor.specialty || doctor.specialization || "N/A"}`;


    // Doctor email
    const email = document.createElement("p");
    email.textContent =
        `Email: ${doctor.email || "N/A"}`;


    // Doctor availability
    const availability = document.createElement("p");

    const availableTimes = doctor.availableTimes || [];

    availability.textContent =
        `Available Times: ${
            Array.isArray(availableTimes)
                ? availableTimes.join(", ")
                : availableTimes
        }`;


    // Add doctor information to card
    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);


    // Create button container
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");


    // ADMIN
    if (role === "admin") {

        const removeBtn = document.createElement("button");

        removeBtn.textContent = "Delete";
        removeBtn.classList.add("delete-btn");

        removeBtn.addEventListener("click", async () => {

            const confirmed = confirm(
                `Are you sure you want to delete Dr. ${doctor.name}?`
            );

            if (!confirmed) {
                return;
            }

            const token = localStorage.getItem("token");

            try {

                await deleteDoctor(doctor.id, token);

                // Remove card from page
                card.remove();

                alert("Doctor deleted successfully.");

            } catch (error) {

                console.error("Error deleting doctor:", error);

                alert("Failed to delete doctor.");
            }
        });

        actionsDiv.appendChild(removeBtn);
    }


    // PATIENT - NOT LOGGED IN
    else if (role === "patient") {

        const bookNow = document.createElement("button");

        bookNow.textContent = "Book Now";
        bookNow.classList.add("book-btn");

        bookNow.addEventListener("click", () => {

            alert("Patient needs to login first.");

        });

        actionsDiv.appendChild(bookNow);
    }


    // LOGGED-IN PATIENT
    else if (role === "loggedPatient") {

        const bookNow = document.createElement("button");

        bookNow.textContent = "Book Now";
        bookNow.classList.add("book-btn");

        bookNow.addEventListener("click", async (e) => {

            try {

                const token = localStorage.getItem("token");

                const patientData = await getPatientData(token);

                showBookingOverlay(
                    e,
                    doctor,
                    patientData
                );

            } catch (error) {

                console.error(
                    "Error getting patient data:",
                    error
                );

                alert("Unable to load patient information.");
            }
        });

        actionsDiv.appendChild(bookNow);
    }


    // Assemble complete card
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);


    // Return card
    return card;
}

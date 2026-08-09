import { openModal } from "./components/modals.js";
import {
    getDoctors,
    filterDoctors,
    saveDoctor
} from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";


// Load doctors when the page is ready
document.addEventListener("DOMContentLoaded", () => {

    const addDocBtn = document.getElementById("addDocBtn");
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    // Add Doctor button
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => {
            openModal("addDoctor");
        });
    }

    // Load all doctors
    loadDoctorCards();

    // Search and filter
    if (searchBar) {
        searchBar.addEventListener("input", filterDoctorsOnChange);
    }

    if (filterTime) {
        filterTime.addEventListener("change", filterDoctorsOnChange);
    }

    if (filterSpecialty) {
        filterSpecialty.addEventListener("change", filterDoctorsOnChange);
    }
});


// Fetch and display all doctors
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error loading doctors:", error);
    }
}


// Display doctor cards
function renderDoctorCards(doctors) {

    const contentDiv = document.getElementById("content");

    if (!contentDiv) {
        return;
    }

    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found</p>";
        return;
    }

    doctors.forEach((doctor) => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}


// Search and filter doctors
async function filterDoctorsOnChange() {

    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    const name = searchBar ? searchBar.value : "";
    const time = filterTime ? filterTime.value : "";
    const specialty = filterSpecialty ? filterSpecialty.value : "";

    try {
        const doctors = await filterDoctors(name, time, specialty);
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error filtering doctors:", error);
    }
}


// Add doctor from modal form
export async function adminAddDoctor(event) {

    event.preventDefault();

    const token = localStorage.getItem("token");

    if (!token) {
        alert("Admin session expired. Please log in again.");
        return;
    }

    const doctor = {
        name: document.getElementById("name")?.value,
        specialty: document.getElementById("specialty")?.value,
        email: document.getElementById("email")?.value,
        password: document.getElementById("password")?.value,
        phone: document.getElementById("phone")?.value,
        availableTimes: []
    };

    // Collect selected availability times
    const checkboxes = document.querySelectorAll(
        'input[name="availableTimes"]:checked'
    );

    checkboxes.forEach((checkbox) => {
        doctor.availableTimes.push(checkbox.value);
    });

    try {

        const result = await saveDoctor(doctor, token);

        if (result.success) {
            alert("Doctor added successfully.");

            const modal = document.getElementById("modal");

            if (modal) {
                modal.style.display = "none";
            }

            await loadDoctorCards();

        } else {
            alert(result.message || "Failed to add doctor.");
        }

    } catch (error) {
        console.error("Error adding doctor:", error);
        alert("Something went wrong while adding the doctor.");
    }
}

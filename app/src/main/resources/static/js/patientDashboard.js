import { createDoctorCard } from "./components/doctorCard.js";
import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { patientLogin, patientSignup } from "./services/patientServices.js";

// Load doctors when page loads
document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    const signupBtn = document.getElementById("patientSignup");
    if (signupBtn) {
        signupBtn.addEventListener("click", () => {
            openModal("patientSignup");
        });
    }

    const loginBtn = document.getElementById("patientLogin");
    if (loginBtn) {
        loginBtn.addEventListener("click", () => {
            openModal("patientLogin");
        });
    }

    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

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

// Load all doctors
async function loadDoctorCards() {
    const contentDiv = document.getElementById("content");

    try {
        const doctors = await getDoctors();

        contentDiv.innerHTML = "";

        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error loading doctors:", error);
        contentDiv.innerHTML = "<p>Unable to load doctors.</p>";
    }
}

// Render doctor cards
function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");

    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML =
            "<p>No doctors found with the given filters.</p>";
        return;
    }

    doctors.forEach((doctor) => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

// Search and filter doctors
async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar").value;
    const time = document.getElementById("filterTime").value;
    const specialty = document.getElementById("filterSpecialty").value;

    try {
        const doctors = await filterDoctors(name, time, specialty);

        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error filtering doctors:", error);

        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "<p>Unable to filter doctors.</p>";
    }
}

// Patient signup
window.signupPatient = async function () {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const phone = document.getElementById("phone").value;
    const address = document.getElementById("address").value;

    const data = {
        name,
        email,
        password,
        phone,
        address
    };

    try {
        const response = await patientSignup(data);

        if (response.success) {
            alert(response.message || "Signup successful!");

            document.getElementById("modal").style.display = "none";

            window.location.reload();
        } else {
            alert(response.message || "Signup failed.");
        }
    } catch (error) {
        console.error("Signup error:", error);
        alert("An error occurred during signup.");
    }
};

// Patient login
window.loginPatient = async function () {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const data = {
        email,
        password
    };

    try {
        const response = await patientLogin(data);

        if (response.ok) {
            const result = await response.json();

            localStorage.setItem("token", result.token);
            localStorage.setItem("userRole", "loggedPatient");

            window.location.href = "loggedPatientDashboard.html";
        } else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        console.error("Login error:", error);
        alert("An error occurred during login.");
    }
};

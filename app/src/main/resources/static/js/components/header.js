function renderHeader() {

    // Do not show role-based header on homepage
    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Check for invalid or expired session
    if (
        (role === "loggedPatient" ||
         role === "admin" ||
         role === "doctor") &&
        !token
    ) {
        localStorage.removeItem("userRole");

        alert("Session expired or invalid login. Please log in again.");

        window.location.href = "/";

        return;
    }

    const headerDiv = document.getElementById("header");

    if (!headerDiv) {
        return;
    }

    let headerContent = "";

    // Admin header
    if (role === "admin") {

        headerContent += `
            <header class="header">
                <button
                    id="addDocBtn"
                    class="adminBtn"
                    onclick="openModal('addDoctor')">
                    Add Doctor
                </button>

                <a href="#" onclick="logout()">Logout</a>
            </header>
        `;
    }

    // Doctor header
    else if (role === "doctor") {

        headerContent += `
            <header class="header">
                <a href="/">Home</a>

                <a href="#" onclick="logout()">Logout</a>
            </header>
        `;
    }

    // Patient header
    else if (role === "patient") {

        headerContent += `
            <header class="header">
                <a href="/">Login</a>

                <a href="/pages/patientDashboard.html">
                    Sign Up
                </a>
            </header>
        `;
    }

    // Logged-in patient header
    else if (role === "loggedPatient") {

        headerContent += `
            <header class="header">
                <a href="/pages/patientDashboard.html">
                    Home
                </a>

                <a href="/pages/patientAppointments.html">
                    Appointments
                </a>

                <a href="#" onclick="logoutPatient()">
                    Logout
                </a>
            </header>
        `;
    }

    // Insert generated header
    headerDiv.innerHTML = headerContent;

    // Attach event listeners
    attachHeaderButtonListeners();
}


// Attach listeners to dynamically created header buttons
function attachHeaderButtonListeners() {

    const addDocBtn = document.getElementById("addDocBtn");

    if (addDocBtn) {
        addDocBtn.addEventListener("click", function () {
            if (typeof openModal === "function") {
                openModal("addDoctor");
            }
        });
    }
}


// Logout for admin and doctor
function logout() {

    localStorage.removeItem("token");
    localStorage.removeItem("userRole");

    window.location.href = "/";
}


// Logout for logged-in patient
function logoutPatient() {

    localStorage.removeItem("token");

    localStorage.setItem("userRole", "patient");

    window.location.href = "/pages/patientDashboard.html";
}


// Render header when page loads
document.addEventListener("DOMContentLoaded", function () {
    renderHeader();
});

import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";


const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";


window.onload = function () {

    const adminBtn = document.getElementById("adminLogin");

    if (adminBtn) {
        adminBtn.addEventListener("click", () => {
            openModal("adminLogin");
        });
    }


    const doctorBtn = document.getElementById("doctorLogin");

    if (doctorBtn) {
        doctorBtn.addEventListener("click", () => {
            openModal("doctorLogin");
        });
    }
};


// Admin Login
async function adminLoginHandler() {

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const admin = {
        username: username,
        password: password
    };


    try {

        const response = await fetch(ADMIN_API, {
            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(admin)
        });


        if (response.ok) {

            const data = await response.json();

            localStorage.setItem("token", data.token);

            selectRole("admin");

        } else {

            alert("Invalid credentials!");
        }

    } catch (error) {

        console.error("Admin login error:", error);

        alert("An unexpected error occurred. Please try again.");
    }
}


// Doctor Login
async function doctorLoginHandler() {

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const doctor = {
        email: email,
        password: password
    };


    try {

        const response = await fetch(DOCTOR_API, {
            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(doctor)
        });


        if (response.ok) {

            const data = await response.json();

            localStorage.setItem("token", data.token);

            selectRole("doctor");

        } else {

            alert("Invalid credentials!");
        }

    } catch (error) {

        console.error("Doctor login error:", error);

        alert("An unexpected error occurred. Please try again.");
    }
}


// Make login handlers globally accessible
window.adminLoginHandler = adminLoginHandler;
window.doctorLoginHandler = doctorLoginHandler;

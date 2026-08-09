import { API_BASE_URL } from "../config/config.js";

const PATIENT_API = API_BASE_URL + "/patient";


// Patient Signup
export async function patientSignup(data) {

    try {

        // Send patient registration data to backend
        const response = await fetch(
            PATIENT_API,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(data)
            }
        );

        const result = await response.json();

        if (!response.ok) {

            return {
                success: false,
                message: result.message || "Patient signup failed"
            };
        }

        return {
            success: true,
            message: result.message || "Patient registered successfully"
        };

    } catch (error) {

        console.error("Patient signup error:", error);

        return {
            success: false,
            message: "Unable to register patient"
        };
    }
}


// Patient Login
export async function patientLogin(data) {

    try {

        // Send patient login credentials
        const response = await fetch(
            PATIENT_API + "/login",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(data)
            }
        );

        return response;

    } catch (error) {

        console.error("Patient login error:", error);

        return null;
    }
}


// Get logged-in patient data
export async function getPatientData(token) {

    try {

        // Request patient information using authentication token
        const response = await fetch(
            PATIENT_API,
            {
                method: "GET",

                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }
        );

        if (!response.ok) {
            throw new Error("Failed to fetch patient data");
        }

        const patient = await response.json();

        return patient;

    } catch (error) {

        console.error("Error fetching patient data:", error);

        return null;
    }
}


// Get patient appointments
export async function getPatientAppointments(id, token, user) {

    try {

        // Build URL depending on the requesting user
        let url;

        if (user === "doctor") {
            url = `${API_BASE_URL}/doctor/${id}/appointments`;
        } else {
            url = `${PATIENT_API}/${id}/appointments`;
        }

        // Request appointments
        const response = await fetch(
            url,
            {
                method: "GET",

                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }
        );

        if (!response.ok) {
            throw new Error("Failed to fetch appointments");
        }

        const appointments = await response.json();

        return appointments;

    } catch (error) {

        console.error(
            "Error fetching patient appointments:",
            error
        );

        return null;
    }
}


// Filter appointments
export async function filterAppointments(
    condition,
    name,
    token
) {

    try {

        // Build query parameters
        const params = new URLSearchParams();

        if (condition) {
            params.append("condition", condition);
        }

        if (name) {
            params.append("name", name);
        }

        const queryString = params.toString();

        const url = queryString
            ? `${PATIENT_API}/appointments?${queryString}`
            : `${PATIENT_API}/appointments`;

        // Request filtered appointments
        const response = await fetch(
            url,
            {
                method: "GET",

                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }
        );

        if (!response.ok) {
            throw new Error("Failed to filter appointments");
        }

        const appointments = await response.json();

        return appointments;

    } catch (error) {

        console.error(
            "Error filtering appointments:",
            error
        );

        alert("Unable to filter appointments.");

        return [];
    }
}

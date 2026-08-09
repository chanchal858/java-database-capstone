import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + "/doctor";


// Get all doctors
export async function getDoctors() {

    try {

        const response = await fetch(DOCTOR_API);

        if (!response.ok) {
            throw new Error("Failed to fetch doctors");
        }

        const doctors = await response.json();

        return doctors;

    } catch (error) {

        console.error("Error fetching doctors:", error);

        return [];
    }
}


// Delete a doctor
export async function deleteDoctor(id, token) {

    try {

        const response = await fetch(
            `${DOCTOR_API}/${id}`,
            {
                method: "DELETE",

                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }
        );

        const data = await response.json();

        if (!response.ok) {

            return {
                success: false,
                message: data.message || "Failed to delete doctor"
            };
        }

        return {
            success: true,
            message: data.message || "Doctor deleted successfully"
        };

    } catch (error) {

        console.error("Error deleting doctor:", error);

        return {
            success: false,
            message: "An error occurred while deleting the doctor"
        };
    }
}


// Save / Add a doctor
export async function saveDoctor(doctor, token) {

    try {

        const response = await fetch(
            DOCTOR_API,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },

                body: JSON.stringify(doctor)
            }
        );

        const data = await response.json();

        if (!response.ok) {

            return {
                success: false,
                message: data.message || "Failed to save doctor"
            };
        }

        return {
            success: true,
            message: data.message || "Doctor saved successfully",
            data: data
        };

    } catch (error) {

        console.error("Error saving doctor:", error);

        return {
            success: false,
            message: "An error occurred while saving the doctor"
        };
    }
}


// Filter doctors
export async function filterDoctors(name, time, specialty) {

    try {

        const params = new URLSearchParams();

        if (name) {
            params.append("name", name);
        }

        if (time) {
            params.append("time", time);
        }

        if (specialty) {
            params.append("specialty", specialty);
        }

        const url = params.toString()
            ? `${DOCTOR_API}?${params.toString()}`
            : DOCTOR_API;

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error("Failed to filter doctors");
        }

        const doctors = await response.json();

        return doctors;

    } catch (error) {

        console.error("Error filtering doctors:", error);

        return [];
    }
}

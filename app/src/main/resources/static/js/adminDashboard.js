import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    const addDocBtn = document.getElementById('addDocBtn');
    if (addDocBtn) {
        addDocBtn.addEventListener('click', () => {
            openModal('addDoctor');
        });
    }

    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime") || document.getElementById("sortByTime");
    const filterSpecialty = document.getElementById("filterSpecialty") || document.getElementById("filterBySpecialty");

    if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
    if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
    if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});

export async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error loading doctors:", error);
    }
}

export function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;
    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found</p>";
        return;
    }

    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

async function filterDoctorsOnChange() {
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime") || document.getElementById("sortByTime");
    const filterSpecialty = document.getElementById("filterSpecialty") || document.getElementById("filterBySpecialty");

    const name = searchBar ? searchBar.value.trim() : "";
    const time = filterTime ? filterTime.value : "";
    const specialty = filterSpecialty ? filterSpecialty.value : "";

    try {
        const doctors = await filterDoctors(name || null, time || null, specialty || null);
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error filtering doctors:", error);
    }
}

window.adminAddDoctor = async function () {
    const name = document.getElementById("docName")?.value;
    const specialty = document.getElementById("docSpecialty")?.value;
    const email = document.getElementById("docEmail")?.value;
    const password = document.getElementById("docPassword")?.value;
    const phone = document.getElementById("docPhone")?.value;

    const availabilityCheckboxes = document.querySelectorAll('input[name="availability"]:checked');
    const availableTimes = Array.from(availabilityCheckboxes).map(cb => cb.value);

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Unauthorized! Please log in as Admin.");
        return;
    }

    const doctor = {
        name,
        specialty,
        email,
        password,
        phone,
        availableTimes
    };

    try {
        const result = await saveDoctor(doctor, token);
        if (result.success) {
            alert(result.message || "Doctor added successfully!");
            const modal = document.getElementById("modal");
            if (modal) modal.style.display = "none";
            await loadDoctorCards();
        } else {
            alert(result.message || "Failed to add doctor.");
        }
    } catch (error) {
        console.error("Error adding doctor:", error);
        alert("An error occurred while saving the doctor.");
    }
};
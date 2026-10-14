import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

const tableBody = document.getElementById('patientTableBody');
let selectedDate = new Date().toISOString().split('T')[0];
const token = localStorage.getItem('token');
let patientName = null;

document.addEventListener('DOMContentLoaded', () => {
    if (typeof renderContent === 'function') {
        renderContent();
    }

    const searchBar = document.getElementById('searchBar');
    if (searchBar) {
        searchBar.addEventListener('input', (e) => {
            const val = e.target.value.trim();
            patientName = val !== '' ? val : 'null';
            loadAppointments();
        });
    }

    const todayButton = document.getElementById('todayButton') || document.getElementById('todayAppointmentsBtn');
    const datePicker = document.getElementById('datePicker') || document.getElementById('appointmentDateFilter');

    if (todayButton) {
        todayButton.addEventListener('click', () => {
            selectedDate = new Date().toISOString().split('T')[0];
            if (datePicker) {
                datePicker.value = selectedDate;
            }
            loadAppointments();
        });
    }

    if (datePicker) {
        datePicker.value = selectedDate;
        datePicker.addEventListener('change', (e) => {
            selectedDate = e.target.value;
            loadAppointments();
        });
    }

    loadAppointments();
});

export async function loadAppointments() {
    if (!tableBody) return;

    try {
        const appointments = await getAllAppointments(selectedDate, patientName || 'null', token);
        tableBody.innerHTML = '';

        if (!appointments || appointments.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="noPatientRecord">No Appointments found for today.</td>
                </tr>`;
            return;
        }

        appointments.forEach(appointment => {
            const patient = appointment.patient || appointment;
            const row = createPatientRow(patient, appointment);
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading appointments:', error);
        tableBody.innerHTML = `
            <tr>
                <td colspan="5" class="noPatientRecord">Error loading appointments. Please try again later.</td>
            </tr>`;
    }
}
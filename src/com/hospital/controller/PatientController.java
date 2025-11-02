package com.hospital.controller;

import com.hospital.dao.PatientDAO;
import com.hospital.dao.PatientTextFileDAO; // <-- MODIFIED
import com.hospital.model.Patient;

import java.io.BufferedWriter; // <-- NEW
import java.io.FileWriter; // <-- NEW
import java.io.IOException; // <-- NEW
import java.nio.file.Path; // <-- NEW
import java.nio.file.Paths; // <-- NEW
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * PatientController.java
 * The "brain" of the application.
 *
 * This version is UPDATED to:
 * 1. Use the new PatientTextFileDAO (which saves to .txt AND .dat).
 * 2. Re-implement the "printBill" logic, as it is now a controller-level task.
 */
public class PatientController {

    private PatientDAO patientDAO;
    private DateTimeFormatter dateFormatter;
    private NumberFormat currencyFormatter;
    private final String DATA_DIR_NAME = ".hospitalapp"; // For saving bills
    private final Path dataDir; // For saving bills

    public PatientController() {
        // --- THIS IS THE BIG CHANGE ---
        // We are now using the Text File DAO
        // This DAO will automatically save a .dat backup!
        this.patientDAO = new PatientTextFileDAO();
        // ------------------------------
        
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN")); // For ₹

        // Set up the directory path for saving bills
        String homeDir = System.getProperty("user.home");
        this.dataDir = Paths.get(homeDir, DATA_DIR_NAME);
    }

    /**
     * Validates and adds a new patient.
     */
    public Patient addNewPatient(
            String name, String ageStr, String contact, String address, String admissionDateStr,
            String doctor, String treatment, String history, String baseBillStr, String insuranceStr)
            throws Exception {

        // --- 1. Validate All Inputs ---
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Patient Name cannot be empty.");
        }
        int age = validateInteger(ageStr, 0, 150, "Age");
        double baseBill = validateDouble(baseBillStr, 0, Double.MAX_VALUE, "Base Bill");
        double insurance = validateDouble(insuranceStr, 0, 100, "Insurance %");
        LocalDate admissionDate = validateDate(admissionDateStr, "Admission Date", true); // Required

        // --- 2. Create Patient Object ---
        Patient patient = new Patient();
        patient.setName(name.trim());
        patient.setAge(age);
        patient.setContact(contact.trim());
        patient.setAddress(address);
        patient.setDateOfAdmission(admissionDate);
        patient.setDoctorAssigned(doctor.trim());
        patient.setTreatmentGiven(treatment.trim());
        patient.setMedicalHistory(history);
        patient.setBaseBillAmount(baseBill);
        patient.setInsuranceDiscountPercent(insurance);
        patient.calculateFinalBill();
        patient.setDischarged(false);
        patient.setDateOfDischarge(null);

        // --- 3. Save via DAO and return ---
        // The DAO will generate and set the ID
        return patientDAO.addPatient(patient);
    }

    /**
     * Validates and updates an existing patient.
     */
    public void updatePatient(
            String patientId, String name, String ageStr, String contact, String address, String admissionDateStr,
            String doctor, String treatment, String history, String baseBillStr, String insuranceStr,
            boolean isDischarged, String dischargeDateStr)
            throws Exception {

        // --- 1. Get Existing Patient ---
        Patient patient = patientDAO.getPatient(patientId);
        if (patient == null) {
            throw new Exception("Failed to find patient to update.");
        }

        // --- 2. Validate All Inputs ---
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Patient Name cannot be empty.");
        }
        int age = validateInteger(ageStr, 0, 150, "Age");
        double baseBill = validateDouble(baseBillStr, 0, Double.MAX_VALUE, "Base Bill");
        double insurance = validateDouble(insuranceStr, 0, 100, "Insurance %");
        LocalDate admissionDate = validateDate(admissionDateStr, "Admission Date", true); // Required
        LocalDate dischargeDate = null;
        
        if (isDischarged) {
            dischargeDate = validateDate(dischargeDateStr, "Discharge Date", true); // Required if checked
            if (dischargeDate.isBefore(admissionDate)) {
                throw new Exception("Discharge Date cannot be before Admission Date.");
            }
        }

        // --- 3. Update Patient Object ---
        patient.setName(name.trim());
        patient.setAge(age);
        patient.setContact(contact.trim());
        patient.setAddress(address);
        patient.setDateOfAdmission(admissionDate);
        patient.setDoctorAssigned(doctor.trim());
        patient.setTreatmentGiven(treatment.trim());
        patient.setMedicalHistory(history);
        patient.setBaseBillAmount(baseBill);
        patient.setInsuranceDiscountPercent(insurance);
        patient.calculateFinalBill();
        patient.setDischarged(isDischarged);
        patient.setDateOfDischarge(dischargeDate);

        // --- 4. Save via DAO ---
        patientDAO.updatePatient(patient);
    }

    /**
     * Discharges a patient (sets date to today).
     */
    public void dischargePatient(String patientId) throws Exception {
        Patient patient = patientDAO.getPatient(patientId);
        if (patient == null) {
            throw new Exception("Failed to find patient to discharge.");
        }
        
        patient.setDischarged(true);
        patient.setDateOfDischarge(LocalDate.now());
        patientDAO.updatePatient(patient);
    }
    
    /**
     * --- NEW: Print Bill Logic ---
     * This logic now lives in the controller.
     * It creates a formatted .txt file for the given patient.
     * @return The absolute path to the saved bill file.
     */
    public String printBill(String patientId) throws Exception {
        Patient patient = getPatient(patientId);
        if (patient == null) {
            throw new Exception("Patient not found.");
        }

        String billFileName = "Bill_" + patient.getPatientId() + "_" + patient.getName().replace(" ", "_") + ".txt";
        Path billPath = dataDir.resolve(billFileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(billPath.toFile()))) {
            writer.write("****************************************"); writer.newLine();
            writer.write("         NEWLIFE HOSPITAL BILL          "); writer.newLine();
            writer.write("****************************************"); writer.newLine();
            writer.newLine();
            writer.write("Patient ID:    " + patient.getPatientId()); writer.newLine();
            writer.write("Patient Name:  " + patient.getName()); writer.newLine();
            writer.write("Doctor:        " + patient.getDoctorAssigned()); writer.newLine();
            writer.newLine();
            writer.write("----------------------------------------"); writer.newLine();
            writer.write("          BILLING DETAILS               "); writer.newLine();
            writer.write("----------------------------------------"); writer.newLine();
            writer.newLine();
            writer.write(String.format("%-25s: %s%n", "Base Bill Amount", formatCurrency(patient.getBaseBillAmount())));
            writer.write(String.format("%-25s: %.1f %%", "Insurance Discount", patient.getInsuranceDiscountPercent())); writer.newLine();
            writer.newLine();
            writer.write("----------------------------------------"); writer.newLine();
            writer.write(String.format("%-25s: %s%n", "TOTAL FINAL BILL", formatCurrency(patient.getFinalBillAmount())));
            writer.write("----------------------------------------"); writer.newLine();
            writer.newLine();
            writer.write("Status: " + (patient.isDischarged() ? "Discharged" : "Admitted")); writer.newLine();
            if (patient.isDischarged() && patient.getDateOfDischarge() != null) {
                writer.write("Discharge Date: " + formatDate(patient.getDateOfDischarge())); writer.newLine();
            }
            writer.newLine();
            writer.write("Thank you and get well soon!"); writer.newLine();
            
        } catch (IOException e) {
            throw new Exception("Could not write bill file: " + e.getMessage());
        }
        
        return billPath.toAbsolutePath().toString();
    }


    // --- Data Passthrough Methods ---

    public Patient getPatient(String patientId) throws Exception {
        return patientDAO.getPatient(patientId);
    }

    public List<Patient> getAllPatients() throws Exception {
        return patientDAO.getAllPatients();
    }

    public double getTotalCollectedBills() throws Exception {
        return getAllPatients().stream()
                .filter(Patient::isDischarged)
                .mapToDouble(Patient::getFinalBillAmount)
                .sum();
    }

    // --- Validation & Formatting ---

    private int validateInteger(String input, int min, int max, String fieldName) throws Exception {
        try {
            int value = Integer.parseInt(input.trim());
            if (value < min || value > max) {
                throw new Exception(fieldName + " must be between " + min + " and " + max + ".");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new Exception(fieldName + " must be a valid whole number.");
        }
    }

    private double validateDouble(String input, double min, double max, String fieldName) throws Exception {
        try {
            double value = Double.parseDouble(input.trim());
            if (value < min || value > max) {
                throw new Exception(fieldName + " must be between " + min + " and " + max + ".");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new Exception(fieldName + " must be a valid number (e.g., 100.0 or 50).");
        }
    }

    private LocalDate validateDate(String dateStr, String fieldName, boolean isRequired) throws Exception {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            if (isRequired) {
                throw new Exception(fieldName + " cannot be empty. Use YYYY-MM-DD format.");
            }
            return null; // Not required and empty, so return null
        }
        try {
            return LocalDate.parse(dateStr.trim(), dateFormatter);
        } catch (DateTimeParseException e) {
            throw new Exception(fieldName + " must be in YYYY-MM-DD format.");
        }
    }

    public String formatDate(LocalDate date) {
        return (date != null) ? date.format(dateFormatter) : "";
    }

    public String formatCurrency(double amount) {
        return currencyFormatter.format(amount);
    }

    public String formatDouble(double amount) {
        // Formats to 2 decimal places, e.g., "50.00" or "12.50"
        return String.format("%.2f", amount);
    }
}


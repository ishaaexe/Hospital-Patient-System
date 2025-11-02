package com.hospital.dao;

import com.hospital.model.Patient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * PatientTextFileDAO.java
 * This is the PRIMARY DAO for the application.
 * It reads and writes to the human-readable patients.txt file.
 * It ALSO calls the PatientDatBackup helper to save a .dat backup.
 */
public class PatientTextFileDAO implements PatientDAO {

    private final String DATA_FILE_NAME = "patients.txt"; // The .txt main file
    private final String DATA_DIR_NAME = ".hospitalapp";
    private final Path dataFile;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final String DELIMITER = "||"; // A robust delimiter

    // The new backup helper
    private PatientDatBackup datBackup;

    public PatientTextFileDAO() {
        // 1. Set up the main .txt file path
        String homeDir = System.getProperty("user.home");
        Path dataDir = Paths.get(homeDir, DATA_DIR_NAME);
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectories(dataDir);
            } catch (IOException e) {
                System.err.println("Could not create data directory: " + e.getMessage());
            }
        }
        this.dataFile = dataDir.resolve(DATA_FILE_NAME);

        // 2. Initialize the .dat backup helper
        this.datBackup = new PatientDatBackup();
    }

    /**
     * Adds a new patient to the .txt file.
     */
    @Override
    public Patient addPatient(Patient patient) throws Exception {
        List<Patient> patients = getAllPatients();
        
        // Generate a new ID (this logic can be complex, e.g., based on date/count)
        String newId = generatePatientId(patients);
        patient.setPatientId(newId);
        
        patients.add(patient);
        savePatients(patients); // This will save to both .txt and .dat
        return patient;
    }

    /**
     * Updates an existing patient in the .txt file.
     */
    @Override
    public void updatePatient(Patient patient) throws Exception {
        List<Patient> patients = getAllPatients();
        boolean found = false;
        
        // Find the patient in the list and replace them
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getPatientId().equals(patient.getPatientId())) {
                patients.set(i, patient);
                found = true;
                break;
            }
        }
        
        if (!found) {
            throw new Exception("Patient not found, could not update.");
        }
        savePatients(patients); // This will save to both .txt and .dat
    }

    /**
     * Retrieves a single patient from the .txt file.
     */
    @Override
    public Patient getPatient(String patientId) throws Exception {
        return getAllPatients().stream()
            .filter(p -> p.getPatientId().equals(patientId))
            .findFirst()
            .orElse(null); // Return null if not found
    }

    /**
     * Retrieves all patients from the .txt file.
     * This is the "source of truth".
     */
    @Override
    public List<Patient> getAllPatients() throws Exception {
        List<Patient> patients = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            // If the .txt file is missing, try to restore from the .dat backup
            // This is a more advanced recovery feature
            return new ArrayList<>(); // For now, just return an empty list
        }

        // Use try-with-resources to read the .txt file
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Patient patient = fromText(line);
                if (patient != null) {
                    patients.add(patient);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error loading patients from file: " + e.getMessage());
        }
        return patients;
    }

    /**
     * Generates a new unique patient ID.
     * Format: YYYYMMDD-001
     */
    private String generatePatientId(List<Patient> patients) {
        LocalDate today = LocalDate.now();
        String prefix = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Count how many patients already exist from today
        long count = patients.stream()
            .filter(p -> p.getPatientId().startsWith(prefix))
            .count();
            
        return prefix + "-" + String.format("%03d", count + 1);
    }

    /**
     * Saves the patient list to both the .txt file and the .dat backup.
     */
    private void savePatients(List<Patient> patients) throws Exception {
        // --- 1. Save to main .txt file (Atomic Write) ---
        Path tempFile = dataFile.getParent().resolve(DATA_FILE_NAME + ".tmp");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            for (Patient patient : patients) {
                writer.write(toText(patient));
                writer.newLine(); // Add a new line for each patient
            }
        } catch (IOException e) {
            throw new Exception("Error saving patients to temporary .txt file: " + e.getMessage());
        }

        try {
            // Move the temp file to the final file
            Files.move(tempFile, dataFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new Exception("Error finalizing patient data .txt save: " + e.getMessage());
        }

        // --- 2. Save to .dat backup file ---
        // This is the new step
        datBackup.saveBackup(patients);
    }

    /**
     * Converts a Patient object into a single line of text for the .txt file.
     */
    private String toText(Patient p) {
        String[] parts = {
            p.getPatientId(),
            p.getName(),
            String.valueOf(p.getAge()),
            p.getContact(),
            p.getAddress().replace("\n", "<NL>"), // Handle newlines
            p.getMedicalHistory().replace("\n", "<NL>"), // Handle newlines
            p.getDoctorAssigned(),
            p.getTreatmentGiven(),
            formatDate(p.getDateOfAdmission()),
            formatDate(p.getDateOfDischarge()),
            String.valueOf(p.isDischarged()),
            String.valueOf(p.getBaseBillAmount()),
            String.valueOf(p.getInsuranceDiscountPercent()),
            String.valueOf(p.getFinalBillAmount())
        };
        // Join all parts with our delimiter
        return String.join(DELIMITER, parts);
    }

    /**
     * Converts a single line of text from the .txt file into a Patient object.
     */
    private Patient fromText(String line) {
        try {
            String[] parts = line.split("\\|\\|"); // Must escape the pipe
            if (parts.length < 14) return null; // Corrupted line
            
            Patient p = new Patient();
            p.setPatientId(parts[0]);
            p.setName(parts[1]);
            p.setAge(Integer.parseInt(parts[2]));
            p.setContact(parts[3]);
            p.setAddress(parts[4].replace("<NL>", "\n"));
            p.setMedicalHistory(parts[5].replace("<NL>", "\n"));
            p.setDoctorAssigned(parts[6]);
            p.setTreatmentGiven(parts[7]);
            p.setDateOfAdmission(parseDate(parts[8]));
            p.setDateOfDischarge(parseDate(parts[9]));
            p.setDischarged(Boolean.parseBoolean(parts[10]));
            p.setBaseBillAmount(Double.parseDouble(parts[11]));
            p.setInsuranceDiscountPercent(Double.parseDouble(parts[12]));
            p.setFinalBillAmount(Double.parseDouble(parts[13]));
            return p;
        } catch (Exception e) {
            // If one line is bad, just skip it
            System.err.println("Skipping corrupted line in patients.txt: " + line);
            return null;
        }
    }

    // --- Date Helper Utilities ---

    private String formatDate(LocalDate date) {
        // Only format if the date is not null
        return (date != null) ? date.format(dateFormatter) : "null";
    }

    private LocalDate parseDate(String dateStr) {
        // Handle "null" strings or empty strings
        if (dateStr == null || dateStr.equals("null") || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, dateFormatter);
    }
}


package com.hospital.dao;

import com.hospital.model.Patient;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * PatientDatBackup.java
 * This is a new helper class, not a full DAO.
 * Its only job is to save a backup of the patient list to a .dat file
 * using Byte Streams (ObjectOutputStream).
 */
public class PatientDatBackup {

    private final String BACKUP_FILE_NAME = "patients.dat"; // The .dat backup
    private final String DATA_DIR_NAME = ".hospitalapp";
    private final Path dataFile;

    public PatientDatBackup() {
        // Find the .hospitalapp folder
        String homeDir = System.getProperty("user.home");
        Path dataDir = Paths.get(homeDir, DATA_DIR_NAME);
        // We assume this directory exists because the main DAO creates it
        this.dataFile = dataDir.resolve(BACKUP_FILE_NAME);
    }

    /**
     * Saves the provided list of patients to patients.dat as a binary backup.
     * This uses an atomic write (save to .tmp, then rename) to prevent corruption.
     * @param patients The complete list of patients to save.
     */
    public void saveBackup(List<Patient> patients) {
        if (patients == null) {
            return; // Do nothing if there's no data
        }

        // Define a temporary file for the atomic save
        Path tempFile = dataFile.getParent().resolve(BACKUP_FILE_NAME + ".tmp");

        // Use try-with-resources to write the object (Byte Stream)
        try (FileOutputStream fos = new FileOutputStream(tempFile.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            // Write the entire list as a single object
            oos.writeObject(patients);
            
        } catch (IOException e) {
            System.err.println("Error: Failed to write to .dat backup file: " + e.getMessage());
            e.printStackTrace();
            return; // Stop if the write fails
        }

        // 2. If the write was successful, move the .tmp file to the final .dat file
        try {
            Files.move(tempFile, dataFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            System.err.println("Error: Failed to finalize .dat backup file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

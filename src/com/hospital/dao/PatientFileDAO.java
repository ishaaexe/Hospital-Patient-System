package com.hospital.dao;

import com.hospital.model.Patient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PatientFileDAO implements PatientDAO {

    private final String dataFilePath;
    private final String dataFileTempPath;

    public PatientFileDAO() {
        String homeDir = System.getProperty("user.home");
        
        File appDir = new File(homeDir, ".hospitalapp");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        
        this.dataFilePath = new File(appDir, "patients.dat").getAbsolutePath();
        this.dataFileTempPath = new File(appDir, "patients.dat.tmp").getAbsolutePath();
    }

    private String generateNextPatientId(List<Patient> patients) {
        int maxSeq = 0;
        for (Patient p : patients) {
            try {
                String[] parts = p.getPatientId().split("-");
                if (parts.length == 2) {
                    int seq = Integer.parseInt(parts[1]);
                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                }
            } catch (Exception e) {
                System.err.println("Warning: Ignoring malformed patient ID: " + p.getPatientId());
            }
        }
        
        int nextSeq = maxSeq + 1;
        
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        return String.format("%s-%03d", datePart, nextSeq);
    }


    @Override
    public List<Patient> getAllPatients() throws Exception {
        return loadPatients();
    }

    @Override
    public Patient getPatient(String patientId) throws Exception {
        List<Patient> patients = loadPatients();
        return patients.stream()
            .filter(p -> p.getPatientId().equals(patientId))
            .findFirst()
            .orElseThrow(() -> new Exception("Patient not found with ID: " + patientId));
    }

    @Override
    public void addPatient(Patient patient) throws Exception {
        List<Patient> patients = loadPatients();
        
        patient.setPatientId(generateNextPatientId(patients));
        
        patients.add(patient);
        savePatients(patients);
    }

    @Override
    public void updatePatient(Patient updatedPatient) throws Exception {
        List<Patient> patients = loadPatients();
        
        Optional<Patient> existingPatient = patients.stream()
            .filter(p -> p.getPatientId().equals(updatedPatient.getPatientId()))
            .findFirst();

        if (existingPatient.isPresent()) {
            int index = patients.indexOf(existingPatient.get());
            patients.set(index, updatedPatient);
            
            savePatients(patients);
        } else {
            throw new Exception("Failed to update: Patient not found with ID " + updatedPatient.getPatientId());
        }
    }

    @Override
    public Map<String, Patient> getAllPatientsAsMap() throws Exception {
        List<Patient> patients = loadPatients();
        Map<String, Patient> patientMap = new LinkedHashMap<>();
        for (Patient p : patients) {
            patientMap.put(p.getPatientId(), p);
        }
        return patientMap;
    }


    private List<Patient> loadPatients() throws Exception {
        File dataFile = new File(dataFilePath);
        
        if (!dataFile.exists()) {
            return new ArrayList<>();
        }

        try (FileInputStream fis = new FileInputStream(dataFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            
            Object readObject = ois.readObject();

            if (readObject instanceof Map) {
                return new ArrayList<>(((Map<String, Patient>) readObject).values());
            } 
            else if (readObject instanceof List) {
                return (List<Patient>) readObject;
            }
            else {
                return new ArrayList<>();
            }

        } catch (java.io.EOFException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to load patient data from " + dataFilePath);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void savePatients(List<Patient> patients) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(dataFileTempPath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            oos.writeObject(patients);
            
        } catch (Exception e) {
            throw new Exception("Failed to write to temporary save file: " + e.getMessage(), e);
        }

        try {
            Files.move(
                Paths.get(dataFileTempPath),
                Paths.get(dataFilePath),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            );
        } catch (Exception e) {
            throw new Exception("CRITICAL: Failed to save patient data: " + e.getMessage(), e);
        }
    }
}
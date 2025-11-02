package com.hospital.dao;

import com.hospital.model.Patient;
import java.util.List;

/**
 * PatientDAO.java
 * This is the "contract" for all data access objects.
 * It defines *what* actions are possible, but not *how* they are done.
 * This version is clean and does not include the "printBill" method,
 * as that is not a core data storage function.
 */
public interface PatientDAO {

    /**
     * Adds a new patient to the data storage.
     * @param patient The patient object to add.
     * @return The patient object, updated with any new data (like a generated ID).
     * @throws Exception if the add operation fails.
     */
    Patient addPatient(Patient patient) throws Exception;

    /**
     * Updates an existing patient's record in the data storage.
     * @param patient The patient object with updated information.
     * @throws Exception if the update operation fails.
     */
    void updatePatient(Patient patient) throws Exception;

    /**
     * Retrieves a single patient by their unique ID.
     * @param patientId The ID of the patient to find.
     * @return The Patient object, or null if not found.
     * @throws Exception if the read operation fails.
     */
    Patient getPatient(String patientId) throws Exception;

    /**
     * Retrieves a list of all patients in the data storage.
     * @return A List of all Patient objects.
     * @throws Exception if the read operation fails.
     */
    List<Patient> getAllPatients() throws Exception;
    
}


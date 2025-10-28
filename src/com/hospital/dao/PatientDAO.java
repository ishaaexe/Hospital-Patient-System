package com.hospital.dao;

import com.hospital.model.Patient;
import java.util.List;
import java.util.Map;

public interface PatientDAO {

    void addPatient(Patient patient) throws Exception;

    Patient getPatient(String patientId) throws Exception;

    void updatePatient(Patient patient) throws Exception;

    List<Patient> getAllPatients() throws Exception;
    
    Map<String, Patient> getAllPatientsAsMap() throws Exception;

}
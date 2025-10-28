package com.hospital.controller;

import com.hospital.dao.PatientDAO;
import com.hospital.dao.PatientFileDAO;
import com.hospital.model.Patient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PatientController {

    private PatientDAO patientDAO;
    private DateTimeFormatter dateFormatter;
    private NumberFormat currencyFormatter;
    private NumberFormat doubleFormatter;

    public PatientController() {
        this.patientDAO = new PatientFileDAO();
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        this.doubleFormatter = NumberFormat.getNumberInstance(Locale.US);
        this.doubleFormatter.setMinimumFractionDigits(1);
        this.doubleFormatter.setMaximumFractionDigits(2);

        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    }
    
    public Patient addNewPatient(String name, String ageStr, String contact, String address,
                                   String admissionDateStr, String doctor, String treatment,
                                   String history, String baseBillStr, String insuranceStr) throws Exception {
        
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Patient Name is required.");
        }
        
        int age = validateAndParseInt(ageStr, "Age", 0, 150);
        double baseBill = validateAndParseDouble(baseBillStr, "Base Bill", 0.0);
        double insurance = validateAndParseDouble(insuranceStr, "Insurance %", 0.0, 100.0);
        LocalDate admissionDate = validateAndParseDate(admissionDateStr, "Admission Date");

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

        patientDAO.addPatient(patient);
        
        return patient;
    }

    public void updatePatient(String patientId, String name, String ageStr, String contact, String address,
                                String admissionDateStr, String doctor, String treatment,
                                String history, String baseBillStr, String insuranceStr,
                                boolean isDischarged, String dischargeDateStr) throws Exception {

        Patient patient = patientDAO.getPatient(patientId);
        if (patient == null) {
            throw new Exception("Could not find patient to update.");
        }
        
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Patient Name is required.");
        }
        int age = validateAndParseInt(ageStr, "Age", 0, 150);
        double baseBill = validateAndParseDouble(baseBillStr, "Base Bill", 0.0);
        double insurance = validateAndParseDouble(insuranceStr, "Insurance %", 0.0, 100.0);
        LocalDate admissionDate = validateAndParseDate(admissionDateStr, "Admission Date");
        LocalDate dischargeDate = null;

        if (isDischarged) {
            if (dischargeDateStr == null || dischargeDateStr.trim().isEmpty()) {
                throw new Exception("Discharge Date is required if 'Is Discharged' is checked.");
            }
            dischargeDate = validateAndParseDate(dischargeDateStr, "Discharge Date");
            
            if (dischargeDate.isBefore(admissionDate)) {
                throw new Exception("Discharge Date cannot be before Admission Date.");
            }
        }

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

        patientDAO.updatePatient(patient);
    }
    
    public void dischargePatient(String patientId) throws Exception {
        Patient patient = patientDAO.getPatient(patientId);
        if (patient == null) {
            throw new Exception("Could not find patient to discharge.");
        }
        if (patient.isDischarged()) {
            throw new Exception("Patient is already discharged.");
        }
        
        patient.setDischarged(true);
        patient.setDateOfDischarge(LocalDate.now());
        
        patientDAO.updatePatient(patient);
    }
    
    public Patient getPatient(String patientId) throws Exception {
        return patientDAO.getPatient(patientId);
    }

    public List<Patient> getAllPatients() throws Exception {
        return patientDAO.getAllPatients();
    }
    
    public double getTotalCollectedBills() throws Exception {
        double total = 0.0;
        for (Patient p : patientDAO.getAllPatients()) {
            if (p.isDischarged()) {
                total += p.getFinalBillAmount();
            }
        }
        return total;
    }

    public String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(dateFormatter);
    }

    public String formatDouble(double d) {
        return doubleFormatter.format(d);
    }
    
    public String formatCurrency(double d) {
        return currencyFormatter.format(d);
    }

    private int validateAndParseInt(String text, String fieldName, int min, int max) throws Exception {
        try {
            int value = Integer.parseInt(text.trim());
            if (value < min || value > max) {
                throw new Exception(fieldName + " must be between " + min + " and " + max + ".");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new Exception(fieldName + " must be a valid whole number.");
        }
    }

    private double validateAndParseDouble(String text, String fieldName, double min) throws Exception {
        try {
            double value = Double.parseDouble(text.trim());
            if (value < min) {
                throw new Exception(fieldName + " must be " + min + " or greater.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new Exception(fieldName + " must be a valid number.");
        }
    }

    private double validateAndParseDouble(String text, String fieldName, double min, double max) throws Exception {
        try {
            double value = Double.parseDouble(text.trim());
            if (value < min || value > max) {
                throw new Exception(fieldName + " must be between " + min + " and " + max + ".");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new Exception(fieldName + " must be a valid number.");
        }
    }
    
    private LocalDate validateAndParseDate(String dateStr, String fieldName) throws Exception {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new Exception(fieldName + " is required.");
        }
        try {
            return LocalDate.parse(dateStr.trim(), dateFormatter);
        } catch (DateTimeParseException e) {
            throw new Exception(fieldName + " must be in YYYY-MM-DD format.");
        }
    }
}
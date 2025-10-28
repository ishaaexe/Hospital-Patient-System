package com.hospital.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Patient implements Serializable {
    
    private static final long serialVersionUID = 2L;

    private String patientId;
    private String name;
    private int age;
    private String contact;
    private String address;

    private LocalDate dateOfAdmission;
    private LocalDate dateOfDischarge;
    
    private String doctorAssigned;
    private String treatmentGiven;
    private String medicalHistory;
    private boolean isDischarged;

    private double baseBillAmount;
    private double insuranceDiscountPercent;
    private double finalBillAmount;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfAdmission() {
        return dateOfAdmission;
    }

    public void setDateOfAdmission(LocalDate dateOfAdmission) {
        this.dateOfAdmission = dateOfAdmission;
    }

    public LocalDate getDateOfDischarge() {
        return dateOfDischarge;
    }

    public void setDateOfDischarge(LocalDate dateOfDischarge) {
        this.dateOfDischarge = dateOfDischarge;
    }

    public String getDoctorAssigned() {
        return doctorAssigned;
    }

    public void setDoctorAssigned(String doctorAssigned) {
        this.doctorAssigned = doctorAssigned;
    }

    public String getTreatmentGiven() {
        return treatmentGiven;
    }

    public void setTreatmentGiven(String treatmentGiven) {
        this.treatmentGiven = treatmentGiven;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public boolean isDischarged() {
        return isDischarged;
    }

    public void setDischarged(boolean isDischarged) {
        this.isDischarged = isDischarged;
    }

    public double getBaseBillAmount() {
        return baseBillAmount;
    }

    public void setBaseBillAmount(double baseBillAmount) {
        this.baseBillAmount = baseBillAmount;
    }

    public double getInsuranceDiscountPercent() {
        return insuranceDiscountPercent;
    }

    public void setInsuranceDiscountPercent(double insuranceDiscountPercent) {
        this.insuranceDiscountPercent = insuranceDiscountPercent;
    }

    public double getFinalBillAmount() {
        return finalBillAmount;
    }

    public void setFinalBillAmount(double finalBillAmount) {
        this.finalBillAmount = finalBillAmount;
    }

    public void calculateFinalBill() {
        if (baseBillAmount <= 0) {
            this.finalBillAmount = 0.0;
            return;
        }
        double discount = baseBillAmount * (insuranceDiscountPercent / 100.0);
        this.finalBillAmount = baseBillAmount - discount;
    }
}
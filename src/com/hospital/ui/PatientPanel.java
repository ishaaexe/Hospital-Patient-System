package com.hospital.ui;

import com.hospital.controller.PatientController;
import com.hospital.model.Patient;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class PatientPanel extends JPanel {

    private JTextField txtPatientId, txtName, txtContact, txtAge,
                       txtAdmissionDate, txtDischargeDate,
                       txtDoctor, txtTreatment,
                       txtBaseBill, txtInsurancePercent, txtFinalBill;
    private JTextArea txtAddress, txtHistory;
    private JCheckBox chkDischarged;
    
    private JButton btnAdd, btnUpdate, btnDischarge, btnClear, btnPrintBill;

    private JTextField txtSearch;
    
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;

    private JLabel lblTotalCollected;
    
    private PatientController controller;
    private Patient selectedPatient = null;

    private final String[] columnNames = {
        "ID", "Name", "Age", "Contact", "Doctor", "Admitted On", "Status"
    };

    public PatientPanel() {
        this.controller = new PatientController();
        
        initUI();
        addListeners();
        
        refreshTableData(); 
        clearForm();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = createFormPanel();
        JPanel tablePanel = createTablePanel();

        add(formPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel formContainerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 5, 0); 
        gbc.fill = GridBagConstraints.BOTH;

        JPanel personalPanel = createPersonalInfoPanel();
        JPanel medicalPanel = createMedicalInfoPanel();
        JPanel billingPanel = createBillingInfoPanel();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4; 
        gbc.weighty = 1.0; 
        formContainerPanel.add(personalPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.4; 
        gbc.insets = new Insets(0, 5, 5, 5); 
        formContainerPanel.add(medicalPanel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.2; 
        formContainerPanel.add(billingPanel, gbc);

        JPanel buttonPanel = createButtonPanel();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3; 
        gbc.weightx = 1.0;
        gbc.weighty = 0.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 0, 0); 
        formContainerPanel.add(buttonPanel, gbc);

        return formContainerPanel;
    }

    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Personal Info"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Age:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Contact:"), gbc);
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTHWEST; 
        panel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER; 
        txtPatientId = new JTextField(15);
        txtPatientId.setEditable(false);
        txtPatientId.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(txtPatientId, gbc);

        gbc.gridy++;
        txtName = new JTextField(15);
        panel.add(txtName, gbc);

        gbc.gridy++;
        txtAge = new JTextField(15);
        panel.add(txtAge, gbc);
        
        gbc.gridy++;
        txtContact = new JTextField(15);
        panel.add(txtContact, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH;
        txtAddress = new JTextArea(3, 15);
        panel.add(new JScrollPane(txtAddress), gbc);

        return panel;
    }
    
    private JPanel createMedicalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Medical Info"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Admission Date:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Doctor Assigned:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Treatment:"), gbc);
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Medical History:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        txtAdmissionDate = new JTextField(15);
        txtAdmissionDate.setToolTipText("YYYY-MM-DD");
        panel.add(txtAdmissionDate, gbc);

        gbc.gridy++;
        txtDoctor = new JTextField(15);
        panel.add(txtDoctor, gbc);

        gbc.gridy++;
        txtTreatment = new JTextField(15);
        panel.add(txtTreatment, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        txtHistory = new JTextArea(3, 15);
        panel.add(new JScrollPane(txtHistory), gbc);

        return panel;
    }

    private JPanel createBillingInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Billing Info"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Base Bill:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Insurance (%):"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Final Bill:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        txtBaseBill = new JTextField(10);
        panel.add(txtBaseBill, gbc);

        gbc.gridy++;
        txtInsurancePercent = new JTextField(10);
        panel.add(txtInsurancePercent, gbc);

        gbc.gridy++;
        txtFinalBill = new JTextField(10);
        txtFinalBill.setEditable(false);
        txtFinalBill.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(txtFinalBill, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        chkDischarged = new JCheckBox("Is Discharged?");
        panel.add(chkDischarged, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 5, 5, 5);
        panel.add(new JLabel("Discharge Date:"), gbc);
        
        gbc.gridy++;
        txtDischargeDate = new JTextField(10);
        txtDischargeDate.setToolTipText("YYYY-MM-DD");
        panel.add(txtDischargeDate, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        panel.add(new JLabel(""), gbc);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        btnAdd = new JButton("Add New Patient");
        btnUpdate = new JButton("Update Patient");
        btnDischarge = new JButton("Discharge Patient");
        btnClear = new JButton("Clear Form");
        btnPrintBill = new JButton("Print Bill");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDischarge);
        buttonPanel.add(btnClear);
        
        buttonPanel.add(new JLabel(" | ")); 
        buttonPanel.add(btnPrintBill);
        
        return buttonPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Patient List"));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Live Filter (by Name or ID):"));
        txtSearch = new JTextField(30);
        
        searchPanel.add(txtSearch);
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.getTableHeader().setReorderingAllowed(false);
        
        tableSorter = new TableRowSorter<>(tableModel);
        patientTable.setRowSorter(tableSorter);
        
        JScrollPane tableScrollPane = new JScrollPane(patientTable);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.add(new JLabel("Total Bill Collected (Discharged):"));
        lblTotalCollected = new JLabel("₹ 0.00");
        lblTotalCollected.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalCollected.setForeground(new Color(0, 100, 0));
        summaryPanel.add(lblTotalCollected);
        
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        tablePanel.add(summaryPanel, BorderLayout.SOUTH);
        
        return tablePanel;
    }
    
    private void addListeners() {
        btnAdd.addActionListener(e -> addPatient());
        btnUpdate.addActionListener(e -> updatePatient());
        btnDischarge.addActionListener(e -> dischargePatient());
        btnClear.addActionListener(e -> clearForm());
        btnPrintBill.addActionListener(e -> printBill());

        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    loadSelectedPatientDataToForm();
                }
            }
        });
        
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });
    }

    private void addPatient() {
        try {
            String name = txtName.getText();
            String age = txtAge.getText();
            String contact = txtContact.getText();
            String address = txtAddress.getText();
            String admissionDate = txtAdmissionDate.getText();
            String doctor = txtDoctor.getText();
            String treatment = txtTreatment.getText();
            String history = txtHistory.getText();
            String baseBill = txtBaseBill.getText();
            String insurance = txtInsurancePercent.getText();

            Patient newPatient = controller.addNewPatient(
                name, age, contact, address, admissionDate,
                doctor, treatment, history, baseBill, insurance
            );

            refreshTableData();
            clearForm();
            showMessage("Patient added successfully! ID: " + newPatient.getPatientId());

        } catch (Exception e) {
            showError("Error adding patient: " + e.getMessage());
        }
    }

    private void updatePatient() {
        if (selectedPatient == null) {
            showError("Please select a patient from the table to update.");
            return;
        }

        try {
            String patientId = txtPatientId.getText();
            String name = txtName.getText();
            String age = txtAge.getText();
            String contact = txtContact.getText();
            String address = txtAddress.getText();
            String admissionDate = txtAdmissionDate.getText();
            String doctor = txtDoctor.getText();
            String treatment = txtTreatment.getText();
            String history = txtHistory.getText();
            String baseBill = txtBaseBill.getText();
            String insurance = txtInsurancePercent.getText();
            boolean isDischarged = chkDischarged.isSelected();
            String dischargeDate = txtDischargeDate.getText();

            controller.updatePatient(
                patientId, name, age, contact, address, admissionDate,
                doctor, treatment, history, baseBill, insurance,
                isDischarged, dischargeDate
            );
            
            refreshTableData();
            clearForm();
            showMessage("Patient updated successfully!");

        } catch (Exception e) {
            showError("Error updating patient: " + e.getMessage());
        }
    }

    private void dischargePatient() {
        if (selectedPatient == null) {
            showError("Please select a patient from the table to discharge.");
            return;
        }
        
        if (selectedPatient.isDischarged()) {
            showMessage("This patient is already discharged.");
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to discharge " + selectedPatient.getName() + "?\n" +
            "This will set the discharge date to today.",
            "Confirm Discharge",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                controller.dischargePatient(selectedPatient.getPatientId());

                refreshTableData();
                clearForm();
                showMessage("Patient discharged successfully.");
                
            } catch (Exception e) {
                showError("Error discharging patient: " + e.getMessage());
            }
        }
    }
    
    private void printBill() {
        if (selectedPatient == null) {
            showError("Please select a patient from the table to print their bill.");
            return;
        }
        
        try {
            String filePath = controller.printBill(selectedPatient.getPatientId());
            showMessage("Bill saved successfully!\n" +
                        "File created at: " + filePath);
        } catch (Exception e) {
            showError("Error printing bill: " + e.getMessage());
        }
    }

    private void filterTable() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            tableSorter.setRowFilter(null);
        } else {
            RowFilter<DefaultTableModel, Object> nameFilter = RowFilter.regexFilter("(?i)" + searchText, 1);
            RowFilter<DefaultTableModel, Object> idFilter = RowFilter.regexFilter("(?i)" + searchText, 0);
            
            List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
            filters.add(nameFilter);
            filters.add(idFilter);
            
            tableSorter.setRowFilter(RowFilter.orFilter(filters));
        }
    }
    
    private void loadSelectedPatientDataToForm() {
        try {
            int viewRow = patientTable.getSelectedRow();
            if (viewRow == -1) return; 
            
            int modelRow = patientTable.convertRowIndexToModel(viewRow);
            String patientId = (String) tableModel.getValueAt(modelRow, 0);

            this.selectedPatient = controller.getPatient(patientId);
            if (this.selectedPatient == null) {
                showError("Could not find patient data. They may have been deleted.");
                refreshTableData();
                return;
            }

            txtPatientId.setText(selectedPatient.getPatientId());
            txtName.setText(selectedPatient.getName());
            txtAge.setText(String.valueOf(selectedPatient.getAge()));
            txtContact.setText(selectedPatient.getContact());
            txtAddress.setText(selectedPatient.getAddress());
            
            txtAdmissionDate.setText(controller.formatDate(selectedPatient.getDateOfAdmission()));
            txtDoctor.setText(selectedPatient.getDoctorAssigned());
            txtTreatment.setText(selectedPatient.getTreatmentGiven());
            txtHistory.setText(selectedPatient.getMedicalHistory());
            
            txtBaseBill.setText(controller.formatDouble(selectedPatient.getBaseBillAmount()));
            txtInsurancePercent.setText(controller.formatDouble(selectedPatient.getInsuranceDiscountPercent()));
            txtFinalBill.setText(controller.formatCurrency(selectedPatient.getFinalBillAmount()));

            chkDischarged.setSelected(selectedPatient.isDischarged());
            txtDischargeDate.setText(controller.formatDate(selectedPatient.getDateOfDischarge()));
            
            setFormState(false); 

        } catch (Exception e) {
            showError("Error loading patient data: " + e.getMessage());
            this.selectedPatient = null; 
        }
    }

    private void clearForm() {
        txtPatientId.setText("(Auto-generated)");
        txtName.setText("");
        txtAge.setText("");
        txtContact.setText("");
        txtAddress.setText("");
        
        txtAdmissionDate.setText(controller.formatDate(LocalDate.now()));
        
        txtDoctor.setText("");
        txtTreatment.setText("");
        txtHistory.setText("");
        txtBaseBill.setText("0.0");
        txtInsurancePercent.setText("0.0");
        txtFinalBill.setText("₹ 0.00");
        txtDischargeDate.setText("");
        
        chkDischarged.setSelected(false);
        
        patientTable.clearSelection();
        this.selectedPatient = null;
        setFormState(true); 
    }

    private void refreshTableData() {
        try {
            tableModel.setRowCount(0);

            List<Patient> allPatients = controller.getAllPatients();

            for (Patient p : allPatients) {
                tableModel.addRow(new Object[]{
                    p.getPatientId(),
                    p.getName(),
                    p.getAge(),
                    p.getContact(),
                    p.getDoctorAssigned(),
                    controller.formatDate(p.getDateOfAdmission()),
                    p.isDischarged() ? "Discharged" : "Admitted"
                });
            }
            
            double total = controller.getTotalCollectedBills();
            lblTotalCollected.setText(controller.formatCurrency(total));
            
        } catch (Exception e) {
            showError("Failed to refresh patient table: " + e.getMessage());
        }
    }
    
    private void setFormState(boolean isNewPatient) {
        btnAdd.setEnabled(isNewPatient);
        btnUpdate.setEnabled(!isNewPatient);
        btnDischarge.setEnabled(!isNewPatient && (selectedPatient != null && !selectedPatient.isDischarged()));
        btnPrintBill.setEnabled(!isNewPatient);
        
        txtAdmissionDate.setEditable(!isNewPatient); 
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}


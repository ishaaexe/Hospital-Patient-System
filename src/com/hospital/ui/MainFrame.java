package com.hospital.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;

public class MainFrame extends JFrame {

    private PatientPanel patientPanel;

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Hospital Patient Record System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblTitle = new JLabel("Hospital Patient Record System");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblFooter = new JLabel("Project by Ishaa Sangam & Soumya Jain");
        lblFooter.setFont(new Font("Arial", Font.ITALIC, 12));
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        lblFooter.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        patientPanel = new PatientPanel();
        
        add(lblTitle, BorderLayout.NORTH);
        add(patientPanel, BorderLayout.CENTER);
        add(lblFooter, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }
}
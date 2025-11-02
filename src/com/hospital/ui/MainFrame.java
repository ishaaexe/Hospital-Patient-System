package com.hospital.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Image;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("NewLife Hospital - Patient Record System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null); 
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        // --- 1. NEW HEADER PANEL ---
        // A simple panel for the top-left logo and title.
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setBackground(Color.WHITE);
        
        // --- Load and scale the logo ---
        ImageIcon logoIcon = new ImageIcon("icons/logo.png");
        Image logoImage = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
        
        // --- Add title ---
        JLabel titleLabel = new JLabel("NewLife Hospital Patient Record System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 152, 219)); 
        
        headerPanel.add(logoLabel);
        headerPanel.add(titleLabel);
        
        // --- 2. PATIENT PANEL (Center) ---
        PatientPanel patientPanel = new PatientPanel();
        
        // --- 3. FOOTER PANEL (South) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(Color.WHITE);
        JLabel footerLabel = new JLabel("Project by Ishaa Sangam & Soumya Jain");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerPanel.add(footerLabel);

        // --- Add all panels to the frame ---
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(patientPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
}


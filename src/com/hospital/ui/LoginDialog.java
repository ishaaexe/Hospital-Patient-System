package com.hospital.ui;

import com.hospital.controller.LoginController;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;

public class LoginDialog extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblStatus;
    private LoginController controller;
    private boolean loginSuccessful = false;

    public LoginDialog(JFrame parent) {
        super(parent, "NewLife Hospital Login", true); 
        this.controller = new LoginController();
        
        initUI();
        pack(); 
        setLocationRelativeTo(parent); 
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ImageIcon logoIcon = new ImageIcon("icons/logo.png");
        JLabel lblLogo = new JLabel(logoIcon);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(lblLogo, gbc);

        JLabel lblWelcome = new JLabel("Welcome to NewLife Hospital");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 20));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        panel.add(lblWelcome, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START; 
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridy = 2; 
        gbc.gridx = 0;
        panel.add(new JLabel("Username (Email):"), gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(25);
        panel.add(txtUsername, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(25);
        panel.add(txtPassword, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        lblStatus = new JLabel(" "); 
        lblStatus.setForeground(Color.RED);
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 12));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblStatus, gbc);

        JPanel buttonPanel = new JPanel();
        JButton btnLogin = new JButton("Login");
        JButton btnCancel = new JButton("Cancel");
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);
        
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
        
        btnLogin.addActionListener(e -> attemptLogin());
        btnCancel.addActionListener(e -> dispose()); 
        
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> attemptLogin());

        getContentPane().add(panel, BorderLayout.CENTER);
    }

    private void attemptLogin() {
        String username = txtUsername.getText();
        char[] password = txtPassword.getPassword();
        
        String result = controller.validateLogin(username, password);
        
        if (result.equals("Success")) {
            loginSuccessful = true;
            dispose(); 
        } else {
            loginSuccessful = false;
            lblStatus.setText(result); 
        }
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
}


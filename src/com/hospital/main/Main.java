package com.hospital.main;

import com.hospital.ui.LoginDialog;
import com.hospital.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Font;

public class Main {

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            
            // --- 1. SET YOUR PREFERRED NIMBUS THEME ---
            // This is the theme code you provided and liked.
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

                Color primaryBlue = new Color(52, 152, 219);
                Color lightGray = new Color(240, 244, 248);
                Color darkText = new Color(51, 51, 51);
                
                UIManager.put("control", lightGray);
                UIManager.put("nimbusBase", primaryBlue);
                UIManager.put("Panel.background", Color.WHITE);
                UIManager.put("List.background", Color.WHITE);
                UIManager.put("Table.background", Color.WHITE);
                UIManager.put("TextArea.background", Color.WHITE);
                UIManager.put("TextField.background", Color.WHITE);

                UIManager.put("text", darkText);
                UIManager.put("TitledBorder.font", new Font("Segoe UI", Font.BOLD, 14));
                UIManager.put("TitledBorder.titleColor", primaryBlue);
                UIManager.put("TitledBorder.border", BorderFactory.createLineBorder(new Color(220, 220, 220)));

                UIManager.put("Button.background", primaryBlue);
                UIManager.put("Button.foreground", Color.WHITE);
                UIManager.put("Button[Focused].background", primaryBlue.darker());
                UIManager.put("Button[Pressed].background", primaryBlue.darker());

                UIManager.put("Table.selectionBackground", primaryBlue);
                UIManager.put("Table.selectionForeground", Color.WHITE);
                
            } catch (Exception e) {
                e.printStackTrace();
            }

            // --- 2. RUN THE LOGIN DIALOG (NOW WITH THEME) ---
            LoginDialog loginDialog = new LoginDialog(null); 
            loginDialog.setVisible(true);

            // --- 3. LAUNCH MAIN APP ON SUCCESS ---
            if (loginDialog.isLoginSuccessful()) {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } else {
                // If login is cancelled or failed, exit the application
                System.exit(0);
            }
        });
    }
}


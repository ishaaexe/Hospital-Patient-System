package com.hospital.main;

import com.hospital.ui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Font;

public class Main {

    public static void main(String[] args) {
        
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

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
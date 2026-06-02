package ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.Properties;

public class SettingsPanel extends JPanel {

    private static JSlider volumeSlider = new JSlider();
    private static JSlider micSensSlider = new JSlider();

    public SettingsPanel(){

        setLayout(new BorderLayout(20, 20));

        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        add(titleLabel, BorderLayout.NORTH);

        JPanel volumePanel = new JPanel(new BorderLayout(5,5));
        JLabel volumeLabel = new JLabel("Output Volume", SwingConstants.CENTER);
        volumeLabel.setFont(new Font("Arial", Font.BOLD, 15));
        volumeSlider = new JSlider(0, 100, 50);

        volumePanel.add(volumeLabel, BorderLayout.NORTH);
        volumePanel.add(volumeSlider, BorderLayout.CENTER);
        add(volumePanel, BorderLayout.WEST);

        JPanel micPanel = new JPanel(new BorderLayout(5,5));
        JLabel micLabel = new JLabel("Mic Sensitivity", SwingConstants.CENTER);
        micLabel.setFont(new Font("Arial", Font.BOLD, 15));
        micSensSlider = new JSlider(0, 100, 50);

        micPanel.add(micLabel, BorderLayout.NORTH);
        micPanel.add(micSensSlider, BorderLayout.CENTER);
        add(micPanel, BorderLayout.EAST);


        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!volumeSlider.getValueIsAdjusting()) {
                    changeProperties(volumeSlider.getValue(), micSensSlider.getValue());
                }
            }
        });

        micSensSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!micSensSlider.getValueIsAdjusting()) {
                    changeProperties(volumeSlider.getValue(), micSensSlider.getValue());
                }
            }
        });
    }


    private void changeProperties(int volumeValue, int sensValue) {

        String filePath = "settings.properties";
        Properties properties = new Properties();

        try (FileInputStream in = new FileInputStream(filePath)) {
            properties.load(in);
        } catch (IOException e) {
            System.err.println("Err reading " + e.getMessage());
        }

        properties.setProperty("micSensitivity", String.valueOf(sensValue));
        properties.setProperty("volume", (String.valueOf(volumeValue)));

        try(FileOutputStream in = new FileOutputStream(filePath)){
            properties.store(in, "Volume has been changed");
        }catch (IOException e) {
            System.err.println("Err reading " + e.getMessage());
        }
    }
}

package ui;

import loader.SettingsManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;
import javax.sound.sampled.*;
import java.util.ArrayList;

public class SettingsPanel extends JPanel {

    private JSlider volumeSlider = new JSlider();
    private JSlider micSensSlider = new JSlider();
    private JComboBox<String> dropdown;
    private static SettingsManager sm = new SettingsManager();

    String filePath = "settings.properties";
    Properties properties = new Properties();

    public SettingsPanel(){

        loadProperties();

        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        ArrayList<String> inputs = new ArrayList<>();

        Line.Info targetLineInfo = new Line.Info(TargetDataLine.class);

        for (Mixer.Info mixerInfo : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);

            if (mixer.isLineSupported(targetLineInfo)) {
                inputs.add(mixerInfo.getName());
            }
        }

        setLayout(new BorderLayout(20, 20));

        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        add(titleLabel, BorderLayout.NORTH);

        JPanel volumePanel = new JPanel(new BorderLayout(5,5));
        JLabel volumeLabel = new JLabel("Output Volume", SwingConstants.CENTER);
        volumeLabel.setFont(new Font("Arial", Font.BOLD, 15));
        volumeSlider = new JSlider(0, 100, Integer.parseInt(properties.getProperty("volume")));

        volumePanel.add(volumeLabel, BorderLayout.NORTH);
        volumePanel.add(volumeSlider, BorderLayout.CENTER);
        add(volumePanel, BorderLayout.WEST);

        JPanel micPanel = new JPanel(new BorderLayout(5,5));
        JLabel micLabel = new JLabel("Mic Sensitivity", SwingConstants.CENTER);
        micLabel.setFont(new Font("Arial", Font.BOLD, 15));
        micSensSlider = new JSlider(0, 100, Integer.parseInt(properties.getProperty("micSensitivity")));

        micPanel.add(micLabel, BorderLayout.NORTH);
        micPanel.add(micSensSlider, BorderLayout.CENTER);
        add(micPanel, BorderLayout.EAST);

        JPanel inputPanel = new JPanel(new BorderLayout(5,5));
        JLabel inputLabel = new JLabel("Mic Input", SwingConstants.CENTER);
        inputPanel.setFont(new Font("Arial", Font.BOLD, 15));
        dropdown = new JComboBox<>(inputs.toArray(new String[0]));

        inputPanel.add(inputLabel, BorderLayout.NORTH);
        inputPanel.add(dropdown, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);


        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!volumeSlider.getValueIsAdjusting()) {
                    sm.changeVolume(volumeSlider.getValue());
                }
            }
        });

        micSensSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!micSensSlider.getValueIsAdjusting()) {
                    sm.changeSens(micSensSlider.getValue());
                }
            }
        });

        dropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selected = dropdown.getSelectedItem();
                if (selected != null){
                    sm.changeInput(selected.toString());
                }
            }
        });

    }
    private void loadProperties() {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException ex) {
            System.out.println("Err loading settings.properties");
            properties.setProperty("volume", "50");
            properties.setProperty("micSensitivity", "50");
        }
    }
}

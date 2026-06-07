package ui;

import audio.AudioInputManager;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestPanel extends JPanel {

    private final AudioInputManager AIM = new AudioInputManager();
    private final JButton recordButton = new JButton("Record");
    private final JButton playButton = new JButton("Play");

    String filePath = "settings.properties";
    Properties properties = new Properties();

    public TestPanel() {
        loadProperties();

        recordButton.addActionListener(e -> {
            if (!AIM.isRecording()) {
                AIM.startRecording(properties.getProperty("micDevice"));
                recordButton.setText("Stop");
            } else {
                AIM.stopRecording();
                recordButton.setText("Record");
            }
        });

        playButton.addActionListener(e -> {
            if (AIM.isPlayReady()) {
                AIM.playBack();
            }
        });

        add(recordButton);
        add(playButton);
    }

    private void loadProperties() {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException ex) {
            System.out.println("err loading settings.properties, using defaults");
            properties.setProperty("volume", "50");
            properties.setProperty("micSensitivity", "50");
        }
    }
}

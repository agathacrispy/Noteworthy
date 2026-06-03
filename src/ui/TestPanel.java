package ui;

import audio.AudioInputManager;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

// class to confirm johnnys audio recording and playback works
// in GameplayPanel, AIM.startRecording() and AIM.stopRecording() are used - check that class

public class TestPanel extends JPanel {

    private final AudioInputManager AIM = new AudioInputManager();
    private final JButton recordButton = new JButton("Record");
    private final JButton playButton = new JButton("Play");

    String filePath = "settings.properties";
    Properties properties = new Properties();

    public TestPanel() {
        //setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        loadProperties();

        recordButton.addActionListener(e -> {
            if (!AIM.isRecording()) {
                AIM.startRecording(properties.getProperty(micDevice));
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
}

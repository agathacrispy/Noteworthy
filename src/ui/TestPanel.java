package ui;

import audio.AudioInputManager;

import javax.swing.*;
import java.awt.*;

// class to confirm johnnys audio recording and playback works

public class TestPanel extends JPanel {

    private final AudioInputManager AIM = new AudioInputManager();
    private final JButton recordButton = new JButton("Record");
    private final JButton playButton = new JButton("Play");

    public TestPanel() {
        //setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        recordButton.addActionListener(e -> {
            if (!AIM.isRecording()) {
                AIM.startRecording();
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

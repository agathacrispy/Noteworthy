package ui;

import audio.AudioInputManager;

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

public class SettingsPanel extends BackgroundPanel {

    private JSlider volumeSlider = new JSlider();
    private JSlider micSensSlider = new JSlider();
    private JComboBox<String> dropdown;

    private Font minecraftFont;
    private Font minecraftFontLarge;

    private final AudioInputManager AIM = new AudioInputManager();
    private final JButton recordButton = new JButton("Record");
    private final JButton playButton = new JButton("Play");

    Color customColor = new Color(0xff, 0xff, 0xff, 180);

    String filePath = "settings.properties";
    Properties properties = new Properties();

    public SettingsPanel(Runnable onBack) {
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

        try {
            minecraftFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Minecraft.ttf")).deriveFont(16f);
        } catch (FontFormatException | IOException e) {
            minecraftFont = new Font("Segoe UI", Font.PLAIN, 16);
        }

        try {
            minecraftFontLarge = Font.createFont(Font.TRUETYPE_FONT, new File("res/Minecraft.ttf")).deriveFont(35f);
        } catch (FontFormatException | IOException e) {
            minecraftFontLarge = new Font("Segoe UI", Font.PLAIN, 26);
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        UIManager.put("Slider.focus", new Color(0, 0, 0, 0));

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 60, 30));

        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.setOpaque(false);

        JPanel settingsWordPanel = getJPanel();

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> onBack.run());
        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.setOpaque(false);

        northContainer.add(topPanel);
        northContainer.add(settingsWordPanel);

        add(northContainer, BorderLayout.NORTH);

        JPanel volumePanel = new JPanel(new BorderLayout(5, 5));
        JLabel volumeLabel = new JLabel("output volume", SwingConstants.CENTER);
        volumeLabel.setForeground(customColor);
        volumeLabel.setFont(minecraftFont);
        volumeSlider = new JSlider(0, 100, Integer.parseInt(properties.getProperty("volume")));
        volumeSlider.setOpaque(false);
        volumePanel.add(volumeLabel, BorderLayout.NORTH);
        volumePanel.add(volumeSlider, BorderLayout.CENTER);
        volumePanel.setOpaque(false);

        JPanel westWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        westWrapper.setPreferredSize(new Dimension(200, 100));
        westWrapper.setOpaque(false);
        westWrapper.add(volumePanel);

        add(westWrapper, BorderLayout.WEST);

        JPanel micPanel = new JPanel(new BorderLayout(5, 5));
        JLabel micLabel = new JLabel("mic sensitivity", SwingConstants.CENTER);
        micLabel.setForeground(customColor);
        micLabel.setFont(minecraftFont);
        micSensSlider = new JSlider(0, 100, Integer.parseInt(properties.getProperty("micSensitivity")));
        micSensSlider.setOpaque(false);
        micPanel.add(micLabel, BorderLayout.NORTH);
        micPanel.add(micSensSlider, BorderLayout.CENTER);
        micPanel.setOpaque(false);

        JPanel eastWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        eastWrapper.setPreferredSize(new Dimension(200, 100));
        eastWrapper.setOpaque(false);
        eastWrapper.add(micPanel);

        add(eastWrapper, BorderLayout.EAST);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JLabel inputLabel = new JLabel("mic input", SwingConstants.CENTER);
        inputLabel.setForeground(customColor);
        inputLabel.setFont(minecraftFont);
        inputPanel.setFont(minecraftFont);
        dropdown = new JComboBox<>(inputs.toArray(new String[0]));
        dropdown.setPreferredSize(new Dimension(250, 30));

        inputPanel.setOpaque(false);
        inputPanel.add(inputLabel, BorderLayout.NORTH);
        inputPanel.add(dropdown, BorderLayout.CENTER);

        JPanel southWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southWrapper.setOpaque(false);
        southWrapper.add(inputPanel);

        add(southWrapper, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        recordButton.addActionListener(e -> {
            if (!AIM.isRecording()) {
                loadProperties();
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

        buttonPanel.add(recordButton);
        buttonPanel.add(playButton);
        buttonPanel.setOpaque(false);
        add(buttonPanel, BorderLayout.CENTER);

        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!volumeSlider.getValueIsAdjusting()) {
                    saveSetting("volume", String.valueOf(volumeSlider.getValue()));
                }
            }
        });

        micSensSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!micSensSlider.getValueIsAdjusting()) {
                    saveSetting("micSensitivity", String.valueOf(micSensSlider.getValue()));
                }
            }
        });

        dropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selected = dropdown.getSelectedItem();
                if (selected != null) {
                    saveSetting("micDevice", selected.toString());
                }
            }
        });
    }

    private JPanel getJPanel() {
        JPanel settingsWordPanel = new JPanel(new BorderLayout());
        settingsWordPanel.setPreferredSize(new Dimension(100, 175));
        JLabel titleLabel = new JLabel("settings", SwingConstants.CENTER);
        JLabel testLabel = new JLabel("test mic", SwingConstants.CENTER);
        titleLabel.setForeground(customColor);
        titleLabel.setFont(minecraftFontLarge);
        testLabel.setForeground(customColor);
        testLabel.setFont(minecraftFont);
        settingsWordPanel.add(titleLabel, BorderLayout.CENTER);
        settingsWordPanel.add(testLabel, BorderLayout.SOUTH);
        settingsWordPanel.setOpaque(false);
        return settingsWordPanel;
    }

    private void saveSetting(String key, String value) {
        properties.setProperty(key, value);
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            properties.store(out, null);
        } catch (IOException e) {
            System.err.println("err saving settings: " + e.getMessage());
        }
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

package loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class SettingsManager {

    String filePath = "settings.properties";
    Properties properties = new Properties();

    public SettingsManager() {
        try (FileInputStream in = new FileInputStream(filePath)) {
            properties.load(in);
        } catch (IOException e) {
            System.err.println("err reading settings: " + e.getMessage());
        }
    }

    public void changeVolume(int volume) {
        properties.setProperty("volume", String.valueOf(volume));
        save("volume changed");
    }

    public void changeSens(int sens) {
        properties.setProperty("micSensitivity", String.valueOf(sens));
        save("sensitivity changed");
    }

    public void changeInput(String input) {
        properties.setProperty("micDevice", input);
        save("input device changed");
    }

    private void save(String comment) {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            properties.store(out, comment);
        } catch (IOException e) {
            System.err.println("err saving settings: " + e.getMessage());
        }
    }
}

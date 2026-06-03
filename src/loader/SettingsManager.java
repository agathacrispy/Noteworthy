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
            System.err.println("Err reading " + e.getMessage());
        }

    }

    public void changeVolume(int volume) {
        properties.setProperty("volume", (String.valueOf(volume)));

        try(FileOutputStream in = new FileOutputStream(filePath)){
            properties.store(in, "Volume has been changed");
        }catch (IOException e) {
            System.err.println("Err reading " + e.getMessage());
        }
    }

    public void changeSens(int sens){
        properties.setProperty("micSensitivity", (String.valueOf(sens)));

        try(FileOutputStream in = new FileOutputStream(filePath)){
            properties.store(in, "Sensitivity has been changed");
        }catch (IOException e) {
            System.err.println("Err reading " + e.getMessage());
        }
    }

    public void changeInput(String input){
        properties.setProperty("micDevice", input);

        try(FileOutputStream in = new FileOutputStream(filePath)){
            properties.store(in, "Input device has been changed");
        }catch (IOException e) {
            System.err.println("Err reading " + e.getMessage());
        }
    }
}

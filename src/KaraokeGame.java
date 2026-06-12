import model.PerformanceResult;
import ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class KaraokeGame {

    private static JFrame frame;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Noteworthy");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);

            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
                if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
                return false;
            });

            showTitle();
            frame.setVisible(true);
        });
    }

    private static void swap(JPanel panel) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();
    }

    public static void showTitle() {
        swap(new TitlePanel(() -> showMainMenu()));
    }

    public static void showMainMenu() {
        swap(new MainMenuPanel(song -> showGameplay(song), () -> showSettings()));
    }

    public static void showSettings() {
        swap(new SettingsPanel(() -> showMainMenu()));
    }

    public static void showGameplay(String song) {
        swap(new GameplayPanel(song, result -> showResults(result)));
    }

    public static void showResults(PerformanceResult result) {
        swap(new ResultsPanel(result, () -> showMainMenu()));
    }
}

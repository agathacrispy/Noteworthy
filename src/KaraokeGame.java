import model.PerformanceResult;
import ui.GameplayPanel;
import ui.MainMenuPanel;
import ui.ResultsPanel;
import ui.SettingsPanel;
import ui.TitlePanel;

import javax.swing.*;

public class KaraokeGame {

    private static JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Noteworthy");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
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
        swap(new MainMenuPanel(
            song -> showGameplay(song),
            () -> showSettings()
        ));
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
